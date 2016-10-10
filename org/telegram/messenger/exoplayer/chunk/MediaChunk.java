package org.telegram.messenger.exoplayer.chunk;

import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.Assertions;

public abstract class MediaChunk
  extends Chunk
{
  public final int chunkIndex;
  public final long endTimeUs;
  public final long startTimeUs;
  
  public MediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, Format paramFormat, long paramLong1, long paramLong2, int paramInt2)
  {
    this(paramDataSource, paramDataSpec, paramInt1, paramFormat, paramLong1, paramLong2, paramInt2, -1);
  }
  
  public MediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, Format paramFormat, long paramLong1, long paramLong2, int paramInt2, int paramInt3)
  {
    super(paramDataSource, paramDataSpec, 1, paramInt1, paramFormat, paramInt3);
    Assertions.checkNotNull(paramFormat);
    this.startTimeUs = paramLong1;
    this.endTimeUs = paramLong2;
    this.chunkIndex = paramInt2;
  }
  
  public long getDurationUs()
  {
    return this.endTimeUs - this.startTimeUs;
  }
  
  public int getNextChunkIndex()
  {
    return this.chunkIndex + 1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/chunk/MediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */