package org.telegram.messenger.exoplayer2;

import android.util.Pair;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;

final class MediaPeriodInfoSequence
{
  private final Timeline.Period period = new Timeline.Period();
  private int repeatMode;
  private boolean shuffleModeEnabled;
  private Timeline timeline;
  private final Timeline.Window window = new Timeline.Window();
  
  private MediaPeriodInfo getMediaPeriodInfo(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong1, long paramLong2)
  {
    this.timeline.getPeriod(paramMediaPeriodId.periodIndex, this.period);
    if (paramMediaPeriodId.isAd())
    {
      if (!this.period.isAdAvailable(paramMediaPeriodId.adGroupIndex, paramMediaPeriodId.adIndexInAdGroup)) {
        return null;
      }
      return getMediaPeriodInfoForAd(paramMediaPeriodId.periodIndex, paramMediaPeriodId.adGroupIndex, paramMediaPeriodId.adIndexInAdGroup, paramLong1);
    }
    int i = this.period.getAdGroupIndexAfterPositionUs(paramLong2);
    if (i == -1) {}
    for (paramLong1 = Long.MIN_VALUE;; paramLong1 = this.period.getAdGroupTimeUs(i)) {
      return getMediaPeriodInfoForContent(paramMediaPeriodId.periodIndex, paramLong2, paramLong1);
    }
  }
  
  private MediaPeriodInfo getMediaPeriodInfoForAd(int paramInt1, int paramInt2, int paramInt3, long paramLong)
  {
    MediaSource.MediaPeriodId localMediaPeriodId = new MediaSource.MediaPeriodId(paramInt1, paramInt2, paramInt3);
    boolean bool1 = isLastInPeriod(localMediaPeriodId, Long.MIN_VALUE);
    boolean bool2 = isLastInTimeline(localMediaPeriodId, bool1);
    long l2 = this.timeline.getPeriod(localMediaPeriodId.periodIndex, this.period).getAdDurationUs(localMediaPeriodId.adGroupIndex, localMediaPeriodId.adIndexInAdGroup);
    if (paramInt3 == this.period.getPlayedAdCount(paramInt2)) {}
    for (long l1 = this.period.getAdResumePositionUs();; l1 = 0L) {
      return new MediaPeriodInfo(localMediaPeriodId, l1, Long.MIN_VALUE, paramLong, l2, bool1, bool2, null);
    }
  }
  
  private MediaPeriodInfo getMediaPeriodInfoForContent(int paramInt, long paramLong1, long paramLong2)
  {
    MediaSource.MediaPeriodId localMediaPeriodId = new MediaSource.MediaPeriodId(paramInt);
    boolean bool1 = isLastInPeriod(localMediaPeriodId, paramLong2);
    boolean bool2 = isLastInTimeline(localMediaPeriodId, bool1);
    this.timeline.getPeriod(localMediaPeriodId.periodIndex, this.period);
    if (paramLong2 == Long.MIN_VALUE) {}
    for (long l = this.period.getDurationUs();; l = paramLong2) {
      return new MediaPeriodInfo(localMediaPeriodId, paramLong1, paramLong2, -9223372036854775807L, l, bool1, bool2, null);
    }
  }
  
  private MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    long l3 = paramMediaPeriodInfo.startPositionUs;
    long l2 = paramMediaPeriodInfo.endPositionUs;
    boolean bool1 = isLastInPeriod(paramMediaPeriodId, l2);
    boolean bool2 = isLastInTimeline(paramMediaPeriodId, bool1);
    this.timeline.getPeriod(paramMediaPeriodId.periodIndex, this.period);
    long l1;
    if (paramMediaPeriodId.isAd()) {
      l1 = this.period.getAdDurationUs(paramMediaPeriodId.adGroupIndex, paramMediaPeriodId.adIndexInAdGroup);
    }
    for (;;)
    {
      return new MediaPeriodInfo(paramMediaPeriodId, l3, l2, paramMediaPeriodInfo.contentPositionUs, l1, bool1, bool2, null);
      if (l2 == Long.MIN_VALUE) {
        l1 = this.period.getDurationUs();
      } else {
        l1 = l2;
      }
    }
  }
  
  private boolean isLastInPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong)
  {
    boolean bool2 = false;
    int i = this.timeline.getPeriod(paramMediaPeriodId.periodIndex, this.period).getAdGroupCount();
    if (i == 0) {}
    int j;
    boolean bool3;
    do
    {
      return true;
      j = i - 1;
      bool3 = paramMediaPeriodId.isAd();
      if (this.period.getAdGroupTimeUs(j) == Long.MIN_VALUE) {
        break;
      }
    } while ((!bool3) && (paramLong == Long.MIN_VALUE));
    return false;
    int k = this.period.getAdCountInAdGroup(j);
    if (k == -1) {
      return false;
    }
    if ((bool3) && (paramMediaPeriodId.adGroupIndex == j) && (paramMediaPeriodId.adIndexInAdGroup == k - 1)) {}
    for (i = 1;; i = 0)
    {
      boolean bool1;
      if (i == 0)
      {
        bool1 = bool2;
        if (!bool3)
        {
          bool1 = bool2;
          if (this.period.getPlayedAdCount(j) != k) {}
        }
      }
      else
      {
        bool1 = true;
      }
      return bool1;
    }
  }
  
  private boolean isLastInTimeline(MediaSource.MediaPeriodId paramMediaPeriodId, boolean paramBoolean)
  {
    int i = this.timeline.getPeriod(paramMediaPeriodId.periodIndex, this.period).windowIndex;
    return (!this.timeline.getWindow(i, this.window).isDynamic) && (this.timeline.isLastPeriod(paramMediaPeriodId.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled)) && (paramBoolean);
  }
  
  public MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo paramPlaybackInfo)
  {
    return getMediaPeriodInfo(paramPlaybackInfo.periodId, paramPlaybackInfo.contentPositionUs, paramPlaybackInfo.startPositionUs);
  }
  
  public MediaPeriodInfo getNextMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo, long paramLong1, long paramLong2)
  {
    int j;
    if (paramMediaPeriodInfo.isLastInTimelinePeriod)
    {
      i = this.timeline.getNextPeriodIndex(paramMediaPeriodInfo.id.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
      if (i == -1) {
        return null;
      }
      j = this.timeline.getPeriod(i, this.period).windowIndex;
      if (this.timeline.getWindow(j, this.window).firstPeriodIndex == i)
      {
        long l = paramMediaPeriodInfo.durationUs;
        paramMediaPeriodInfo = this.timeline.getPeriodPosition(this.window, this.period, j, -9223372036854775807L, Math.max(0L, l + paramLong1 - paramLong2));
        if (paramMediaPeriodInfo == null) {
          return null;
        }
        i = ((Integer)paramMediaPeriodInfo.first).intValue();
      }
      for (paramLong1 = ((Long)paramMediaPeriodInfo.second).longValue();; paramLong1 = 0L) {
        return getMediaPeriodInfo(resolvePeriodPositionForAds(i, paramLong1), paramLong1, paramLong1);
      }
    }
    MediaSource.MediaPeriodId localMediaPeriodId = paramMediaPeriodInfo.id;
    if (localMediaPeriodId.isAd())
    {
      i = localMediaPeriodId.adGroupIndex;
      this.timeline.getPeriod(localMediaPeriodId.periodIndex, this.period);
      j = this.period.getAdCountInAdGroup(i);
      if (j == -1) {
        return null;
      }
      int k = localMediaPeriodId.adIndexInAdGroup + 1;
      if (k < j)
      {
        if (!this.period.isAdAvailable(i, k)) {
          return null;
        }
        return getMediaPeriodInfoForAd(localMediaPeriodId.periodIndex, i, k, paramMediaPeriodInfo.contentPositionUs);
      }
      i = this.period.getAdGroupIndexAfterPositionUs(paramMediaPeriodInfo.contentPositionUs);
      if (i == -1) {}
      for (paramLong1 = Long.MIN_VALUE;; paramLong1 = this.period.getAdGroupTimeUs(i)) {
        return getMediaPeriodInfoForContent(localMediaPeriodId.periodIndex, paramMediaPeriodInfo.contentPositionUs, paramLong1);
      }
    }
    if (paramMediaPeriodInfo.endPositionUs != Long.MIN_VALUE)
    {
      i = this.period.getAdGroupIndexForPositionUs(paramMediaPeriodInfo.endPositionUs);
      if (!this.period.isAdAvailable(i, 0)) {
        return null;
      }
      return getMediaPeriodInfoForAd(localMediaPeriodId.periodIndex, i, 0, paramMediaPeriodInfo.endPositionUs);
    }
    int i = this.period.getAdGroupCount();
    if ((i == 0) || (this.period.getAdGroupTimeUs(i - 1) != Long.MIN_VALUE) || (this.period.hasPlayedAdGroup(i - 1)) || (!this.period.isAdAvailable(i - 1, 0))) {
      return null;
    }
    paramLong1 = this.period.getDurationUs();
    return getMediaPeriodInfoForAd(localMediaPeriodId.periodIndex, i - 1, 0, paramLong1);
  }
  
  public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo)
  {
    return getUpdatedMediaPeriodInfo(paramMediaPeriodInfo, paramMediaPeriodInfo.id);
  }
  
  public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo, int paramInt)
  {
    return getUpdatedMediaPeriodInfo(paramMediaPeriodInfo, paramMediaPeriodInfo.id.copyWithPeriodIndex(paramInt));
  }
  
  public MediaSource.MediaPeriodId resolvePeriodPositionForAds(int paramInt, long paramLong)
  {
    this.timeline.getPeriod(paramInt, this.period);
    int i = this.period.getAdGroupIndexForPositionUs(paramLong);
    if (i == -1) {
      return new MediaSource.MediaPeriodId(paramInt);
    }
    return new MediaSource.MediaPeriodId(paramInt, i, this.period.getPlayedAdCount(i));
  }
  
  public void setRepeatMode(int paramInt)
  {
    this.repeatMode = paramInt;
  }
  
  public void setShuffleModeEnabled(boolean paramBoolean)
  {
    this.shuffleModeEnabled = paramBoolean;
  }
  
  public void setTimeline(Timeline paramTimeline)
  {
    this.timeline = paramTimeline;
  }
  
  public static final class MediaPeriodInfo
  {
    public final long contentPositionUs;
    public final long durationUs;
    public final long endPositionUs;
    public final MediaSource.MediaPeriodId id;
    public final boolean isFinal;
    public final boolean isLastInTimelinePeriod;
    public final long startPositionUs;
    
    private MediaPeriodInfo(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong1, long paramLong2, long paramLong3, long paramLong4, boolean paramBoolean1, boolean paramBoolean2)
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/MediaPeriodInfoSequence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */