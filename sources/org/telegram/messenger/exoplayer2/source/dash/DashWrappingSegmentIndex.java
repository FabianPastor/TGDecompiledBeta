package org.telegram.messenger.exoplayer2.source.dash;

import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;

public final class DashWrappingSegmentIndex implements DashSegmentIndex {
    private final ChunkIndex chunkIndex;

    public DashWrappingSegmentIndex(ChunkIndex chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public long getFirstSegmentNum() {
        return 0;
    }

    public int getSegmentCount(long periodDurationUs) {
        return this.chunkIndex.length;
    }

    public long getTimeUs(long segmentNum) {
        return this.chunkIndex.timesUs[(int) segmentNum];
    }

    public long getDurationUs(long segmentNum, long periodDurationUs) {
        return this.chunkIndex.durationsUs[(int) segmentNum];
    }

    public RangedUri getSegmentUrl(long segmentNum) {
        return new RangedUri(null, this.chunkIndex.offsets[(int) segmentNum], (long) this.chunkIndex.sizes[(int) segmentNum]);
    }

    public long getSegmentNum(long timeUs, long periodDurationUs) {
        return (long) this.chunkIndex.getChunkIndex(timeUs);
    }

    public boolean isExplicit() {
        return true;
    }
}
