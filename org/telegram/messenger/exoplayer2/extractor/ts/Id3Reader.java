package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.ElementaryStreamReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class Id3Reader extends ElementaryStreamReader {
    private static final int ID3_HEADER_SIZE = 10;
    private final ParsableByteArray id3Header = new ParsableByteArray(10);
    private TrackOutput output;
    private int sampleBytesRead;
    private int sampleSize;
    private long sampleTimeUs;
    private boolean writingSample;

    public void seek() {
        this.writingSample = false;
    }

    public void init(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        this.output = extractorOutput.track(idGenerator.getNextId());
        this.output.format(Format.createSampleFormat(null, MimeTypes.APPLICATION_ID3, null, -1, null));
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        if (dataAlignmentIndicator) {
            this.writingSample = true;
            this.sampleTimeUs = pesTimeUs;
            this.sampleSize = 0;
            this.sampleBytesRead = 0;
        }
    }

    public void consume(ParsableByteArray data) {
        if (this.writingSample) {
            int bytesAvailable = data.bytesLeft();
            if (this.sampleBytesRead < 10) {
                int headerBytesAvailable = Math.min(bytesAvailable, 10 - this.sampleBytesRead);
                System.arraycopy(data.data, data.getPosition(), this.id3Header.data, this.sampleBytesRead, headerBytesAvailable);
                if (this.sampleBytesRead + headerBytesAvailable == 10) {
                    this.id3Header.setPosition(6);
                    this.sampleSize = this.id3Header.readSynchSafeInt() + 10;
                }
            }
            int bytesToWrite = Math.min(bytesAvailable, this.sampleSize - this.sampleBytesRead);
            this.output.sampleData(data, bytesToWrite);
            this.sampleBytesRead += bytesToWrite;
        }
    }

    public void packetFinished() {
        if (this.writingSample && this.sampleSize != 0 && this.sampleBytesRead == this.sampleSize) {
            this.output.sampleMetadata(this.sampleTimeUs, 1, this.sampleSize, 0, null);
            this.writingSample = false;
        }
    }
}
