package org.telegram.messenger.exoplayer2.source.dash;

import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;

public abstract interface DashSegmentIndex
{
  public static final int INDEX_UNBOUNDED = -1;
  
  public abstract long getDurationUs(int paramInt, long paramLong);
  
  public abstract int getFirstSegmentNum();
  
  public abstract int getSegmentCount(long paramLong);
  
  public abstract int getSegmentNum(long paramLong1, long paramLong2);
  
  public abstract RangedUri getSegmentUrl(int paramInt);
  
  public abstract long getTimeUs(int paramInt);
  
  public abstract boolean isExplicit();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/DashSegmentIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */