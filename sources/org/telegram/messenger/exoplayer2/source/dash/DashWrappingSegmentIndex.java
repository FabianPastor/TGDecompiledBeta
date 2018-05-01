package org.telegram.messenger.exoplayer2.source.dash;

import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;

public final class DashWrappingSegmentIndex implements DashSegmentIndex {
    private final ChunkIndex chunkIndex;

    public int getFirstSegmentNum() {
        return 0;
    }

    public boolean isExplicit() {
        return true;
    }

    public DashWrappingSegmentIndex(ChunkIndex chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public int getSegmentCount(long j) {
        return this.chunkIndex.length;
    }

    public long getTimeUs(int i) {
        return this.chunkIndex.timesUs[i];
    }

    public long getDurationUs(int i, long j) {
        return this.chunkIndex.durationsUs[i];
    }

    public RangedUri getSegmentUrl(int i) {
        return new RangedUri(null, this.chunkIndex.offsets[i], (long) this.chunkIndex.sizes[i]);
    }

    public int getSegmentNum(long j, long j2) {
        return this.chunkIndex.getChunkIndex(j);
    }
}
