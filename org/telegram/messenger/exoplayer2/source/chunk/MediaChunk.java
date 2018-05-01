package org.telegram.messenger.exoplayer2.source.chunk;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class MediaChunk
  extends Chunk
{
  public final int chunkIndex;
  
  public MediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2)
  {
    super(paramDataSource, paramDataSpec, 1, paramFormat, paramInt1, paramObject, paramLong1, paramLong2);
    Assertions.checkNotNull(paramFormat);
    this.chunkIndex = paramInt2;
  }
  
  public int getNextChunkIndex()
  {
    return this.chunkIndex + 1;
  }
  
  public abstract boolean isLoadCompleted();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/MediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */