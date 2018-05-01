package org.telegram.messenger.exoplayer2.extractor.ts;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.Ac3Util;
import org.telegram.messenger.exoplayer2.audio.Ac3Util.Ac3SyncFrameInfo;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class Ac3Reader implements ElementaryStreamReader {
    private static final int HEADER_SIZE = 128;
    private static final int STATE_FINDING_SYNC = 0;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private int bytesRead;
    private Format format;
    private final ParsableBitArray headerScratchBits;
    private final ParsableByteArray headerScratchBytes;
    private final String language;
    private boolean lastByteWas0B;
    private TrackOutput output;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;
    private String trackFormatId;

    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    public void packetFinished() {
    }

    public Ac3Reader() {
        this(null);
    }

    public Ac3Reader(String str) {
        this.headerScratchBits = new ParsableBitArray(new byte[128]);
        this.headerScratchBytes = new ParsableByteArray(this.headerScratchBits.data);
        this.state = 0;
        this.language = str;
    }

    public void seek() {
        this.state = 0;
        this.bytesRead = 0;
        this.lastByteWas0B = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        trackIdGenerator.generateNewId();
        this.trackFormatId = trackIdGenerator.getFormatId();
        this.output = extractorOutput.track(trackIdGenerator.getTrackId(), 1);
    }

    public void packetStarted(long j, boolean z) {
        this.timeUs = j;
    }

    public void consume(ParsableByteArray parsableByteArray) {
        while (parsableByteArray.bytesLeft() > 0) {
            switch (this.state) {
                case 0:
                    if (!skipToNextSync(parsableByteArray)) {
                        break;
                    }
                    this.state = 1;
                    this.headerScratchBytes.data[0] = (byte) 11;
                    this.headerScratchBytes.data[1] = (byte) 119;
                    this.bytesRead = 2;
                    break;
                case 1:
                    if (!continueRead(parsableByteArray, this.headerScratchBytes.data, 128)) {
                        break;
                    }
                    parseHeader();
                    this.headerScratchBytes.setPosition(0);
                    this.output.sampleData(this.headerScratchBytes, 128);
                    this.state = 2;
                    break;
                case 2:
                    int min = Math.min(parsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
                    this.output.sampleData(parsableByteArray, min);
                    this.bytesRead += min;
                    if (this.bytesRead != this.sampleSize) {
                        break;
                    }
                    this.output.sampleMetadata(this.timeUs, 1, this.sampleSize, 0, null);
                    this.timeUs += this.sampleDurationUs;
                    this.state = 0;
                    break;
                default:
                    break;
            }
        }
    }

    private boolean continueRead(ParsableByteArray parsableByteArray, byte[] bArr, int i) {
        int min = Math.min(parsableByteArray.bytesLeft(), i - this.bytesRead);
        parsableByteArray.readBytes(bArr, this.bytesRead, min);
        this.bytesRead += min;
        return this.bytesRead == i ? true : null;
    }

    private boolean skipToNextSync(ParsableByteArray parsableByteArray) {
        while (true) {
            boolean z = false;
            if (parsableByteArray.bytesLeft() <= 0) {
                return false;
            }
            if (this.lastByteWas0B) {
                int readUnsignedByte = parsableByteArray.readUnsignedByte();
                if (readUnsignedByte == 119) {
                    this.lastByteWas0B = false;
                    return true;
                }
                if (readUnsignedByte == 11) {
                    z = true;
                }
                this.lastByteWas0B = z;
            } else {
                if (parsableByteArray.readUnsignedByte() == 11) {
                    z = true;
                }
                this.lastByteWas0B = z;
            }
        }
    }

    private void parseHeader() {
        this.headerScratchBits.setPosition(0);
        Ac3SyncFrameInfo parseAc3SyncframeInfo = Ac3Util.parseAc3SyncframeInfo(this.headerScratchBits);
        if (!(this.format != null && parseAc3SyncframeInfo.channelCount == this.format.channelCount && parseAc3SyncframeInfo.sampleRate == this.format.sampleRate && parseAc3SyncframeInfo.mimeType == this.format.sampleMimeType)) {
            this.format = Format.createAudioSampleFormat(this.trackFormatId, parseAc3SyncframeInfo.mimeType, null, -1, -1, parseAc3SyncframeInfo.channelCount, parseAc3SyncframeInfo.sampleRate, null, null, 0, this.language);
            this.output.format(this.format);
        }
        this.sampleSize = parseAc3SyncframeInfo.frameSize;
        this.sampleDurationUs = (C0542C.MICROS_PER_SECOND * ((long) parseAc3SyncframeInfo.sampleCount)) / ((long) this.format.sampleRate);
    }
}
