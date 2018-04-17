package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Log;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class Id3Reader implements ElementaryStreamReader {
    private static final int ID3_HEADER_SIZE = 10;
    private static final String TAG = "Id3Reader";
    private final ParsableByteArray id3Header = new ParsableByteArray(10);
    private TrackOutput output;
    private int sampleBytesRead;
    private int sampleSize;
    private long sampleTimeUs;
    private boolean writingSample;

    public void seek() {
        this.writingSample = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 4);
        this.output.format(Format.createSampleFormat(idGenerator.getFormatId(), MimeTypes.APPLICATION_ID3, null, -1, null));
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
            int headerBytesAvailable;
            int bytesAvailable = data.bytesLeft();
            if (this.sampleBytesRead < 10) {
                headerBytesAvailable = Math.min(bytesAvailable, 10 - this.sampleBytesRead);
                System.arraycopy(data.data, data.getPosition(), this.id3Header.data, this.sampleBytesRead, headerBytesAvailable);
                if (this.sampleBytesRead + headerBytesAvailable == 10) {
                    this.id3Header.setPosition(0);
                    if (73 == this.id3Header.readUnsignedByte() && 68 == this.id3Header.readUnsignedByte()) {
                        if (51 == this.id3Header.readUnsignedByte()) {
                            this.id3Header.skipBytes(3);
                            this.sampleSize = 10 + this.id3Header.readSynchSafeInt();
                        }
                    }
                    Log.w(TAG, "Discarding invalid ID3 tag");
                    this.writingSample = false;
                    return;
                }
            }
            headerBytesAvailable = Math.min(bytesAvailable, this.sampleSize - this.sampleBytesRead);
            this.output.sampleData(data, headerBytesAvailable);
            this.sampleBytesRead += headerBytesAvailable;
        }
    }

    public void packetFinished() {
        if (this.writingSample && this.sampleSize != 0) {
            if (this.sampleBytesRead == this.sampleSize) {
                this.output.sampleMetadata(this.sampleTimeUs, 1, this.sampleSize, 0, null);
                this.writingSample = false;
            }
        }
    }
}
