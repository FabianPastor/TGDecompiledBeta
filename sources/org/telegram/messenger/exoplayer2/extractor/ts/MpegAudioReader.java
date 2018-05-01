package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class MpegAudioReader implements ElementaryStreamReader {
    private static final int HEADER_SIZE = 4;
    private static final int STATE_FINDING_HEADER = 0;
    private static final int STATE_READING_FRAME = 2;
    private static final int STATE_READING_HEADER = 1;
    private String formatId;
    private int frameBytesRead;
    private long frameDurationUs;
    private int frameSize;
    private boolean hasOutputFormat;
    private final MpegAudioHeader header;
    private final ParsableByteArray headerScratch;
    private final String language;
    private boolean lastByteWasFF;
    private TrackOutput output;
    private int state;
    private long timeUs;

    public void packetFinished() {
    }

    public MpegAudioReader() {
        this(null);
    }

    public MpegAudioReader(String str) {
        this.state = 0;
        this.headerScratch = new ParsableByteArray(4);
        this.headerScratch.data[0] = (byte) -1;
        this.header = new MpegAudioHeader();
        this.language = str;
    }

    public void seek() {
        this.state = 0;
        this.frameBytesRead = 0;
        this.lastByteWasFF = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        trackIdGenerator.generateNewId();
        this.formatId = trackIdGenerator.getFormatId();
        this.output = extractorOutput.track(trackIdGenerator.getTrackId(), 1);
    }

    public void packetStarted(long j, boolean z) {
        this.timeUs = j;
    }

    public void consume(ParsableByteArray parsableByteArray) {
        while (parsableByteArray.bytesLeft() > 0) {
            switch (this.state) {
                case 0:
                    findHeader(parsableByteArray);
                    break;
                case 1:
                    readHeaderRemainder(parsableByteArray);
                    break;
                case 2:
                    readFrameRemainder(parsableByteArray);
                    break;
                default:
                    break;
            }
        }
    }

    private void findHeader(ParsableByteArray parsableByteArray) {
        byte[] bArr = parsableByteArray.data;
        int limit = parsableByteArray.limit();
        for (int position = parsableByteArray.getPosition(); position < limit; position++) {
            boolean z = (bArr[position] & 255) == 255;
            int i = (this.lastByteWasFF && (bArr[position] & 224) == 224) ? 1 : false;
            this.lastByteWasFF = z;
            if (i != 0) {
                parsableByteArray.setPosition(position + 1);
                this.lastByteWasFF = false;
                this.headerScratch.data[1] = bArr[position];
                this.frameBytesRead = 2;
                this.state = 1;
                return;
            }
        }
        parsableByteArray.setPosition(limit);
    }

    private void readHeaderRemainder(ParsableByteArray parsableByteArray) {
        int min = Math.min(parsableByteArray.bytesLeft(), 4 - this.frameBytesRead);
        parsableByteArray.readBytes(this.headerScratch.data, this.frameBytesRead, min);
        this.frameBytesRead += min;
        if (this.frameBytesRead >= 4) {
            r0.headerScratch.setPosition(0);
            if (MpegAudioHeader.populateHeader(r0.headerScratch.readInt(), r0.header)) {
                r0.frameSize = r0.header.frameSize;
                if (!r0.hasOutputFormat) {
                    r0.frameDurationUs = (C0542C.MICROS_PER_SECOND * ((long) r0.header.samplesPerFrame)) / ((long) r0.header.sampleRate);
                    r0.output.format(Format.createAudioSampleFormat(r0.formatId, r0.header.mimeType, null, -1, 4096, r0.header.channels, r0.header.sampleRate, null, null, 0, r0.language));
                    r0.hasOutputFormat = true;
                }
                r0.headerScratch.setPosition(0);
                r0.output.sampleData(r0.headerScratch, 4);
                r0.state = 2;
                return;
            }
            r0.frameBytesRead = 0;
            r0.state = 1;
        }
    }

    private void readFrameRemainder(ParsableByteArray parsableByteArray) {
        int min = Math.min(parsableByteArray.bytesLeft(), this.frameSize - this.frameBytesRead);
        this.output.sampleData(parsableByteArray, min);
        this.frameBytesRead += min;
        if (this.frameBytesRead >= this.frameSize) {
            this.output.sampleMetadata(this.timeUs, 1, this.frameSize, 0, null);
            this.timeUs += this.frameDurationUs;
            this.frameBytesRead = 0;
            this.state = 0;
        }
    }
}
