package org.telegram.messenger.exoplayer2.source.dash;

import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;

final class DashWrappingSegmentIndex implements DashSegmentIndex {
    private final ChunkIndex chunkIndex;
    private final String uri;

    public DashWrappingSegmentIndex(ChunkIndex chunkIndex, String uri) {
        this.chunkIndex = chunkIndex;
        this.uri = uri;
    }

    public int getFirstSegmentNum() {
        return 0;
    }

    public int getLastSegmentNum(long periodDurationUs) {
        return this.chunkIndex.length - 1;
    }

    public long getTimeUs(int segmentNum) {
        return this.chunkIndex.timesUs[segmentNum];
    }

    public long getDurationUs(int segmentNum, long periodDurationUs) {
        return this.chunkIndex.durationsUs[segmentNum];
    }

    public RangedUri getSegmentUrl(int segmentNum) {
        return new RangedUri(this.uri, null, this.chunkIndex.offsets[segmentNum], (long) this.chunkIndex.sizes[segmentNum]);
    }

    public int getSegmentNum(long timeUs, long periodDurationUs) {
        return this.chunkIndex.getChunkIndex(timeUs);
    }

    public boolean isExplicit() {
        return true;
    }
}
