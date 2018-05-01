package org.telegram.messenger.exoplayer2.extractor.ts;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class AdtsExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new C18441();
    private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
    private static final int MAX_PACKET_SIZE = 200;
    private static final int MAX_SNIFF_BYTES = 8192;
    private final long firstSampleTimestampUs;
    private final ParsableByteArray packetBuffer;
    private final AdtsReader reader;
    private boolean startedPacket;

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.ts.AdtsExtractor$1 */
    static class C18441 implements ExtractorsFactory {
        C18441() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new AdtsExtractor()};
        }
    }

    public void release() {
    }

    public AdtsExtractor() {
        this(0);
    }

    public AdtsExtractor(long j) {
        this.firstSampleTimestampUs = j;
        this.reader = new AdtsReader(true);
        this.packetBuffer = new ParsableByteArray(200);
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        int readSynchSafeInt;
        ParsableByteArray parsableByteArray = new ParsableByteArray(10);
        ParsableBitArray parsableBitArray = new ParsableBitArray(parsableByteArray.data);
        int i = 0;
        while (true) {
            extractorInput.peekFully(parsableByteArray.data, 0, 10);
            parsableByteArray.setPosition(0);
            if (parsableByteArray.readUnsignedInt24() != ID3_TAG) {
                break;
            }
            parsableByteArray.skipBytes(3);
            readSynchSafeInt = parsableByteArray.readSynchSafeInt();
            i += 10 + readSynchSafeInt;
            extractorInput.advancePeekPosition(readSynchSafeInt);
        }
        extractorInput.resetPeekPosition();
        extractorInput.advancePeekPosition(i);
        readSynchSafeInt = 0;
        int i2 = readSynchSafeInt;
        int i3 = i;
        while (true) {
            extractorInput.peekFully(parsableByteArray.data, 0, 2);
            parsableByteArray.setPosition(0);
            if ((parsableByteArray.readUnsignedShort() & 65526) != 65520) {
                extractorInput.resetPeekPosition();
                i3++;
                if (i3 - i >= 8192) {
                    return false;
                }
                extractorInput.advancePeekPosition(i3);
                readSynchSafeInt = 0;
                i2 = readSynchSafeInt;
            } else {
                readSynchSafeInt++;
                if (readSynchSafeInt >= 4 && i2 > 188) {
                    return true;
                }
                extractorInput.peekFully(parsableByteArray.data, 0, 4);
                parsableBitArray.setPosition(14);
                int readBits = parsableBitArray.readBits(13);
                if (readBits <= 6) {
                    return false;
                }
                extractorInput.advancePeekPosition(readBits - 6);
                i2 += readBits;
            }
        }
    }

    public void init(ExtractorOutput extractorOutput) {
        this.reader.createTracks(extractorOutput, new TrackIdGenerator(0, 1));
        extractorOutput.endTracks();
        extractorOutput.seekMap(new Unseekable(C0542C.TIME_UNSET));
    }

    public void seek(long j, long j2) {
        this.startedPacket = 0;
        this.reader.seek();
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        extractorInput = extractorInput.read(this.packetBuffer.data, 0, 200);
        if (extractorInput == -1) {
            return -1;
        }
        this.packetBuffer.setPosition(0);
        this.packetBuffer.setLimit(extractorInput);
        if (this.startedPacket == null) {
            this.reader.packetStarted(this.firstSampleTimestampUs, true);
            this.startedPacket = true;
        }
        this.reader.consume(this.packetBuffer);
        return 0;
    }
}
