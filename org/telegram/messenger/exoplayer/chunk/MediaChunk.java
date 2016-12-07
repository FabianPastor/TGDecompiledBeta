package org.telegram.messenger.exoplayer.chunk;

import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.Assertions;

public abstract class MediaChunk extends Chunk {
    public final int chunkIndex;
    public final long endTimeUs;
    public final long startTimeUs;

    public MediaChunk(DataSource dataSource, DataSpec dataSpec, int trigger, Format format, long startTimeUs, long endTimeUs, int chunkIndex) {
        this(dataSource, dataSpec, trigger, format, startTimeUs, endTimeUs, chunkIndex, -1);
    }

    public MediaChunk(DataSource dataSource, DataSpec dataSpec, int trigger, Format format, long startTimeUs, long endTimeUs, int chunkIndex, int parentId) {
        super(dataSource, dataSpec, 1, trigger, format, parentId);
        Assertions.checkNotNull(format);
        this.startTimeUs = startTimeUs;
        this.endTimeUs = endTimeUs;
        this.chunkIndex = chunkIndex;
    }

    public int getNextChunkIndex() {
        return this.chunkIndex + 1;
    }

    public long getDurationUs() {
        return this.endTimeUs - this.startTimeUs;
    }
}
