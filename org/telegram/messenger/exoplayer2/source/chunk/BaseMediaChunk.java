package org.telegram.messenger.exoplayer2.source.chunk;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

public abstract class BaseMediaChunk extends MediaChunk {
    private int firstSampleIndex;
    private DefaultTrackOutput trackOutput;

    public BaseMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, int chunkIndex) {
        super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkIndex);
    }

    public void init(DefaultTrackOutput trackOutput) {
        this.trackOutput = trackOutput;
        this.firstSampleIndex = trackOutput.getWriteIndex();
    }

    public final int getFirstSampleIndex() {
        return this.firstSampleIndex;
    }

    protected final DefaultTrackOutput getTrackOutput() {
        return this.trackOutput;
    }
}
