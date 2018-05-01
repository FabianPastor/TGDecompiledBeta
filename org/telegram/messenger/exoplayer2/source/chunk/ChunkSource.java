package org.telegram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.SeekParameters;

public abstract interface ChunkSource
{
  public abstract long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters);
  
  public abstract void getNextChunk(MediaChunk paramMediaChunk, long paramLong1, long paramLong2, ChunkHolder paramChunkHolder);
  
  public abstract int getPreferredQueueSize(long paramLong, List<? extends MediaChunk> paramList);
  
  public abstract void maybeThrowError()
    throws IOException;
  
  public abstract void onChunkLoadCompleted(Chunk paramChunk);
  
  public abstract boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, Exception paramException);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/ChunkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */