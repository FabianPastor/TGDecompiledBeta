package org.telegram.messenger.exoplayer2.source.dash;

import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;

public final class DashWrappingSegmentIndex
  implements DashSegmentIndex
{
  private final ChunkIndex chunkIndex;
  
  public DashWrappingSegmentIndex(ChunkIndex paramChunkIndex)
  {
    this.chunkIndex = paramChunkIndex;
  }
  
  public long getDurationUs(int paramInt, long paramLong)
  {
    return this.chunkIndex.durationsUs[paramInt];
  }
  
  public int getFirstSegmentNum()
  {
    return 0;
  }
  
  public int getSegmentCount(long paramLong)
  {
    return this.chunkIndex.length;
  }
  
  public int getSegmentNum(long paramLong1, long paramLong2)
  {
    return this.chunkIndex.getChunkIndex(paramLong1);
  }
  
  public RangedUri getSegmentUrl(int paramInt)
  {
    return new RangedUri(null, this.chunkIndex.offsets[paramInt], this.chunkIndex.sizes[paramInt]);
  }
  
  public long getTimeUs(int paramInt)
  {
    return this.chunkIndex.timesUs[paramInt];
  }
  
  public boolean isExplicit()
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/DashWrappingSegmentIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */