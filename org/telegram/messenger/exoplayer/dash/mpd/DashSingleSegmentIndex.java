package org.telegram.messenger.exoplayer.dash.mpd;

import org.telegram.messenger.exoplayer.dash.DashSegmentIndex;

final class DashSingleSegmentIndex
  implements DashSegmentIndex
{
  private final RangedUri uri;
  
  public DashSingleSegmentIndex(RangedUri paramRangedUri)
  {
    this.uri = paramRangedUri;
  }
  
  public long getDurationUs(int paramInt, long paramLong)
  {
    return paramLong;
  }
  
  public int getFirstSegmentNum()
  {
    return 0;
  }
  
  public int getLastSegmentNum(long paramLong)
  {
    return 0;
  }
  
  public int getSegmentNum(long paramLong1, long paramLong2)
  {
    return 0;
  }
  
  public RangedUri getSegmentUrl(int paramInt)
  {
    return this.uri;
  }
  
  public long getTimeUs(int paramInt)
  {
    return 0L;
  }
  
  public boolean isExplicit()
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/mpd/DashSingleSegmentIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */