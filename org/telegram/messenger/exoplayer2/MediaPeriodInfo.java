package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;

final class MediaPeriodInfo
{
  public final long contentPositionUs;
  public final long durationUs;
  public final long endPositionUs;
  public final MediaSource.MediaPeriodId id;
  public final boolean isFinal;
  public final boolean isLastInTimelinePeriod;
  public final long startPositionUs;
  
  MediaPeriodInfo(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong1, long paramLong2, long paramLong3, long paramLong4, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.id = paramMediaPeriodId;
    this.startPositionUs = paramLong1;
    this.endPositionUs = paramLong2;
    this.contentPositionUs = paramLong3;
    this.durationUs = paramLong4;
    this.isLastInTimelinePeriod = paramBoolean1;
    this.isFinal = paramBoolean2;
  }
  
  public MediaPeriodInfo copyWithPeriodIndex(int paramInt)
  {
    return new MediaPeriodInfo(this.id.copyWithPeriodIndex(paramInt), this.startPositionUs, this.endPositionUs, this.contentPositionUs, this.durationUs, this.isLastInTimelinePeriod, this.isFinal);
  }
  
  public MediaPeriodInfo copyWithStartPositionUs(long paramLong)
  {
    return new MediaPeriodInfo(this.id, paramLong, this.endPositionUs, this.contentPositionUs, this.durationUs, this.isLastInTimelinePeriod, this.isFinal);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/MediaPeriodInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */