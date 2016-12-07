package org.telegram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public final class SingleSampleMediaChunk extends BaseMediaChunk {
    private volatile int bytesLoaded;
    private volatile boolean loadCanceled;
    private volatile boolean loadCompleted;
    private final Format sampleFormat;

    public SingleSampleMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, int chunkIndex, Format sampleFormat) {
        super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkIndex);
        this.sampleFormat = sampleFormat;
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public void load() throws IOException, InterruptedException {
        try {
            long length = this.dataSource.open(Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded));
            if (length != -1) {
                length += (long) this.bytesLoaded;
            }
            ExtractorInput extractorInput = new DefaultExtractorInput(this.dataSource, (long) this.bytesLoaded, length);
            DefaultTrackOutput trackOutput = getTrackOutput();
            trackOutput.formatWithOffset(this.sampleFormat, 0);
            for (int result = 0; result != -1; result = trackOutput.sampleData(extractorInput, ConnectionsManager.DEFAULT_DATACENTER_ID, true)) {
                this.bytesLoaded += result;
            }
            trackOutput.sampleMetadata(this.startTimeUs, 1, this.bytesLoaded, 0, null);
            this.loadCompleted = true;
        } finally {
            this.dataSource.close();
        }
    }
}
