package org.telegram.messenger.exoplayer.dash;

import org.telegram.messenger.exoplayer.dash.mpd.RangedUri;
import org.telegram.messenger.exoplayer.extractor.ChunkIndex;

final class DashWrappingSegmentIndex
  implements DashSegmentIndex
{
  private final ChunkIndex chunkIndex;
  private final String uri;
  
  public DashWrappingSegmentIndex(ChunkIndex paramChunkIndex, String paramString)
  {
    this.chunkIndex = paramChunkIndex;
    this.uri = paramString;
  }
  
  public long getDurationUs(int paramInt, long paramLong)
  {
    return this.chunkIndex.durationsUs[paramInt];
  }
  
  public int getFirstSegmentNum()
  {
    return 0;
  }
  
  public int getLastSegmentNum(long paramLong)
  {
    return this.chunkIndex.length - 1;
  }
  
  public int getSegmentNum(long paramLong1, long paramLong2)
  {
    return this.chunkIndex.getChunkIndex(paramLong1);
  }
  
  public RangedUri getSegmentUrl(int paramInt)
  {
    return new RangedUri(this.uri, null, this.chunkIndex.offsets[paramInt], this.chunkIndex.sizes[paramInt]);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/DashWrappingSegmentIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */