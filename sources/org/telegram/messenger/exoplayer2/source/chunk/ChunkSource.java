package org.telegram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.SeekParameters;

public interface ChunkSource {
    long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters);

    void getNextChunk(MediaChunk mediaChunk, long j, long j2, ChunkHolder chunkHolder);

    int getPreferredQueueSize(long j, List<? extends MediaChunk> list);

    void maybeThrowError() throws IOException;

    void onChunkLoadCompleted(Chunk chunk);

    boolean onChunkLoadError(Chunk chunk, boolean z, Exception exception);
}
