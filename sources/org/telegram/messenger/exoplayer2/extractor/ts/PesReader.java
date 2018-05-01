package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Log;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class PesReader implements TsPayloadReader {
    private static final int HEADER_SIZE = 9;
    private static final int MAX_HEADER_EXTENSION_SIZE = 10;
    private static final int PES_SCRATCH_SIZE = 10;
    private static final int STATE_FINDING_HEADER = 0;
    private static final int STATE_READING_BODY = 3;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_HEADER_EXTENSION = 2;
    private static final String TAG = "PesReader";
    private int bytesRead;
    private boolean dataAlignmentIndicator;
    private boolean dtsFlag;
    private int extendedHeaderLength;
    private int payloadSize;
    private final ParsableBitArray pesScratch = new ParsableBitArray(new byte[10]);
    private boolean ptsFlag;
    private final ElementaryStreamReader reader;
    private boolean seenFirstDts;
    private int state = null;
    private long timeUs;
    private TimestampAdjuster timestampAdjuster;

    public PesReader(ElementaryStreamReader elementaryStreamReader) {
        this.reader = elementaryStreamReader;
    }

    public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        this.timestampAdjuster = timestampAdjuster;
        this.reader.createTracks(extractorOutput, trackIdGenerator);
    }

    public final void seek() {
        this.state = 0;
        this.bytesRead = 0;
        this.seenFirstDts = false;
        this.reader.seek();
    }

    public final void consume(ParsableByteArray parsableByteArray, boolean z) throws ParserException {
        if (z) {
            switch (this.state) {
                case false:
                case true:
                    break;
                case true:
                    Log.w(TAG, "Unexpected start indicator reading extended header");
                    break;
                case true:
                    if (!this.payloadSize) {
                        z = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unexpected start indicator: expected ");
                        stringBuilder.append(this.payloadSize);
                        stringBuilder.append(" more bytes");
                        Log.w(z, stringBuilder.toString());
                    }
                    this.reader.packetFinished();
                    break;
                default:
                    break;
            }
            setState(1);
        }
        while (parsableByteArray.bytesLeft() <= false) {
            int i = 0;
            switch (this.state) {
                case false:
                    parsableByteArray.skipBytes(parsableByteArray.bytesLeft());
                    break;
                case true:
                    if (!continueRead(parsableByteArray, this.pesScratch.data, 9)) {
                        break;
                    }
                    if (parseHeader()) {
                        i = 2;
                    }
                    setState(i);
                    break;
                case true:
                    if (continueRead(parsableByteArray, this.pesScratch.data, Math.min(true, this.extendedHeaderLength)) && continueRead(parsableByteArray, false, this.extendedHeaderLength)) {
                        parseHeaderExtension();
                        this.reader.packetStarted(this.timeUs, this.dataAlignmentIndicator);
                        setState(true);
                        break;
                    }
                case true:
                    z = parsableByteArray.bytesLeft();
                    if (this.payloadSize != -1) {
                        i = z - this.payloadSize;
                    }
                    if (i > 0) {
                        z -= i;
                        parsableByteArray.setLimit(parsableByteArray.getPosition() + z);
                    }
                    this.reader.consume(parsableByteArray);
                    if (this.payloadSize == -1) {
                        break;
                    }
                    this.payloadSize -= z;
                    if (!this.payloadSize) {
                        this.reader.packetFinished();
                        setState(1);
                        break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void setState(int i) {
        this.state = i;
        this.bytesRead = 0;
    }

    private boolean continueRead(ParsableByteArray parsableByteArray, byte[] bArr, int i) {
        int min = Math.min(parsableByteArray.bytesLeft(), i - this.bytesRead);
        boolean z = true;
        if (min <= 0) {
            return true;
        }
        if (bArr == null) {
            parsableByteArray.skipBytes(min);
        } else {
            parsableByteArray.readBytes(bArr, this.bytesRead, min);
        }
        this.bytesRead += min;
        if (this.bytesRead != i) {
            z = false;
        }
        return z;
    }

    private boolean parseHeader() {
        this.pesScratch.setPosition(0);
        int readBits = this.pesScratch.readBits(24);
        if (readBits != 1) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected start code prefix: ");
            stringBuilder.append(readBits);
            Log.w(str, stringBuilder.toString());
            this.payloadSize = -1;
            return false;
        }
        this.pesScratch.skipBits(8);
        readBits = this.pesScratch.readBits(16);
        this.pesScratch.skipBits(5);
        this.dataAlignmentIndicator = this.pesScratch.readBit();
        this.pesScratch.skipBits(2);
        this.ptsFlag = this.pesScratch.readBit();
        this.dtsFlag = this.pesScratch.readBit();
        this.pesScratch.skipBits(6);
        this.extendedHeaderLength = this.pesScratch.readBits(8);
        if (readBits == 0) {
            this.payloadSize = -1;
        } else {
            this.payloadSize = ((readBits + 6) - 9) - this.extendedHeaderLength;
        }
        return true;
    }

    private void parseHeaderExtension() {
        this.pesScratch.setPosition(0);
        this.timeUs = C0542C.TIME_UNSET;
        if (this.ptsFlag) {
            this.pesScratch.skipBits(4);
            long readBits = ((long) this.pesScratch.readBits(3)) << 30;
            this.pesScratch.skipBits(1);
            long readBits2 = readBits | ((long) (this.pesScratch.readBits(15) << 15));
            this.pesScratch.skipBits(1);
            long readBits3 = readBits2 | ((long) this.pesScratch.readBits(15));
            this.pesScratch.skipBits(1);
            if (!this.seenFirstDts && this.dtsFlag) {
                this.pesScratch.skipBits(4);
                long readBits4 = ((long) this.pesScratch.readBits(3)) << 30;
                this.pesScratch.skipBits(1);
                long readBits5 = readBits4 | ((long) (this.pesScratch.readBits(15) << 15));
                this.pesScratch.skipBits(1);
                long readBits6 = readBits5 | ((long) this.pesScratch.readBits(15));
                this.pesScratch.skipBits(1);
                this.timestampAdjuster.adjustTsTimestamp(readBits6);
                this.seenFirstDts = true;
            }
            this.timeUs = this.timestampAdjuster.adjustTsTimestamp(readBits3);
        }
    }
}
