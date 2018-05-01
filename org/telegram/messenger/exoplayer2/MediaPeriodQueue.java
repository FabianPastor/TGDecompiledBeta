package org.telegram.messenger.exoplayer2;

import android.util.Pair;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class MediaPeriodQueue
{
  private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
  private int length;
  private MediaPeriodHolder loading;
  private final Timeline.Period period = new Timeline.Period();
  private MediaPeriodHolder playing;
  private MediaPeriodHolder reading;
  private int repeatMode;
  private boolean shuffleModeEnabled;
  private Timeline timeline;
  private final Timeline.Window window = new Timeline.Window();
  
  private MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo paramPlaybackInfo)
  {
    return getMediaPeriodInfo(paramPlaybackInfo.periodId, paramPlaybackInfo.contentPositionUs, paramPlaybackInfo.startPositionUs);
  }
  
  private MediaPeriodInfo getFollowingMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo, long paramLong1, long paramLong2)
  {
    int i;
    if (paramMediaPeriodInfo.isLastInTimelinePeriod)
    {
      i = this.timeline.getNextPeriodIndex(paramMediaPeriodInfo.id.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
      if (i == -1) {
        paramMediaPeriodInfo = null;
      }
    }
    for (;;)
    {
      return paramMediaPeriodInfo;
      int j = this.timeline.getPeriod(i, this.period).windowIndex;
      if (this.timeline.getWindow(j, this.window).firstPeriodIndex == i)
      {
        long l = paramMediaPeriodInfo.durationUs;
        paramMediaPeriodInfo = this.timeline.getPeriodPosition(this.window, this.period, j, -9223372036854775807L, Math.max(0L, l + paramLong1 - paramLong2));
        if (paramMediaPeriodInfo == null) {
          paramMediaPeriodInfo = null;
        } else {
          i = ((Integer)paramMediaPeriodInfo.first).intValue();
        }
      }
      else
      {
        for (paramLong1 = ((Long)paramMediaPeriodInfo.second).longValue();; paramLong1 = 0L)
        {
          paramMediaPeriodInfo = getMediaPeriodInfo(resolveMediaPeriodIdForAds(i, paramLong1), paramLong1, paramLong1);
          break;
        }
        MediaSource.MediaPeriodId localMediaPeriodId = paramMediaPeriodInfo.id;
        if (localMediaPeriodId.isAd())
        {
          i = localMediaPeriodId.adGroupIndex;
          this.timeline.getPeriod(localMediaPeriodId.periodIndex, this.period);
          int k = this.period.getAdCountInAdGroup(i);
          if (k == -1)
          {
            paramMediaPeriodInfo = null;
          }
          else
          {
            j = localMediaPeriodId.adIndexInAdGroup + 1;
            if (j < k)
            {
              if (!this.period.isAdAvailable(i, j)) {
                paramMediaPeriodInfo = null;
              } else {
                paramMediaPeriodInfo = getMediaPeriodInfoForAd(localMediaPeriodId.periodIndex, i, j, paramMediaPeriodInfo.contentPositionUs);
              }
            }
            else
            {
              i = this.period.getAdGroupIndexAfterPositionUs(paramMediaPeriodInfo.contentPositionUs);
              if (i == -1) {}
              for (paramLong1 = Long.MIN_VALUE;; paramLong1 = this.period.getAdGroupTimeUs(i))
              {
                paramMediaPeriodInfo = getMediaPeriodInfoForContent(localMediaPeriodId.periodIndex, paramMediaPeriodInfo.contentPositionUs, paramLong1);
                break;
              }
            }
          }
        }
        else if (paramMediaPeriodInfo.endPositionUs != Long.MIN_VALUE)
        {
          i = this.period.getAdGroupIndexForPositionUs(paramMediaPeriodInfo.endPositionUs);
          if (!this.period.isAdAvailable(i, 0)) {
            paramMediaPeriodInfo = null;
          } else {
            paramMediaPeriodInfo = getMediaPeriodInfoForAd(localMediaPeriodId.periodIndex, i, 0, paramMediaPeriodInfo.endPositionUs);
          }
        }
        else
        {
          i = this.period.getAdGroupCount();
          if ((i == 0) || (this.period.getAdGroupTimeUs(i - 1) != Long.MIN_VALUE) || (this.period.hasPlayedAdGroup(i - 1)) || (!this.period.isAdAvailable(i - 1, 0)))
          {
            paramMediaPeriodInfo = null;
          }
          else
          {
            paramLong1 = this.period.getDurationUs();
            paramMediaPeriodInfo = getMediaPeriodInfoForAd(localMediaPeriodId.periodIndex, i - 1, 0, paramLong1);
          }
        }
      }
    }
  }
  
  private MediaPeriodInfo getMediaPeriodInfo(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong1, long paramLong2)
  {
    this.timeline.getPeriod(paramMediaPeriodId.periodIndex, this.period);
    if (paramMediaPeriodId.isAd())
    {
      if (!this.period.isAdAvailable(paramMediaPeriodId.adGroupIndex, paramMediaPeriodId.adIndexInAdGroup)) {}
      for (paramMediaPeriodId = null;; paramMediaPeriodId = getMediaPeriodInfoForAd(paramMediaPeriodId.periodIndex, paramMediaPeriodId.adGroupIndex, paramMediaPeriodId.adIndexInAdGroup, paramLong1)) {
        return paramMediaPeriodId;
      }
    }
    int i = this.period.getAdGroupIndexAfterPositionUs(paramLong2);
    if (i == -1) {}
    for (paramLong1 = Long.MIN_VALUE;; paramLong1 = this.period.getAdGroupTimeUs(i))
    {
      paramMediaPeriodId = getMediaPeriodInfoForContent(paramMediaPeriodId.periodIndex, paramLong2, paramLong1);
      break;
    }
  }
  
  private MediaPeriodInfo getMediaPeriodInfoForAd(int paramInt1, int paramInt2, int paramInt3, long paramLong)
  {
    MediaSource.MediaPeriodId localMediaPeriodId = new MediaSource.MediaPeriodId(paramInt1, paramInt2, paramInt3);
    boolean bool1 = isLastInPeriod(localMediaPeriodId, Long.MIN_VALUE);
    boolean bool2 = isLastInTimeline(localMediaPeriodId, bool1);
    long l1 = this.timeline.getPeriod(localMediaPeriodId.periodIndex, this.period).getAdDurationUs(localMediaPeriodId.adGroupIndex, localMediaPeriodId.adIndexInAdGroup);
    if (paramInt3 == this.period.getNextAdIndexToPlay(paramInt2)) {}
    for (long l2 = this.period.getAdResumePositionUs();; l2 = 0L) {
      return new MediaPeriodInfo(localMediaPeriodId, l2, Long.MIN_VALUE, paramLong, l1, bool1, bool2);
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
      return new MediaPeriodInfo(localMediaPeriodId, paramLong1, paramLong2, -9223372036854775807L, l, bool1, bool2);
    }
  }
  
  private MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    long l1 = paramMediaPeriodInfo.startPositionUs;
    long l2 = paramMediaPeriodInfo.endPositionUs;
    boolean bool1 = isLastInPeriod(paramMediaPeriodId, l2);
    boolean bool2 = isLastInTimeline(paramMediaPeriodId, bool1);
    this.timeline.getPeriod(paramMediaPeriodId.periodIndex, this.period);
    long l3;
    if (paramMediaPeriodId.isAd()) {
      l3 = this.period.getAdDurationUs(paramMediaPeriodId.adGroupIndex, paramMediaPeriodId.adIndexInAdGroup);
    }
    for (;;)
    {
      return new MediaPeriodInfo(paramMediaPeriodId, l1, l2, paramMediaPeriodInfo.contentPositionUs, l3, bool1, bool2);
      if (l2 == Long.MIN_VALUE) {
        l3 = this.period.getDurationUs();
      } else {
        l3 = l2;
      }
    }
  }
  
  private boolean isLastInPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    int i = this.timeline.getPeriod(paramMediaPeriodId.periodIndex, this.period).getAdGroupCount();
    if (i == 0) {}
    int j;
    boolean bool3;
    int k;
    for (;;)
    {
      return bool1;
      j = i - 1;
      bool3 = paramMediaPeriodId.isAd();
      if (this.period.getAdGroupTimeUs(j) != Long.MIN_VALUE)
      {
        if ((bool3) || (paramLong != Long.MIN_VALUE)) {
          bool1 = false;
        }
      }
      else
      {
        k = this.period.getAdCountInAdGroup(j);
        if (k != -1) {
          break;
        }
        bool1 = false;
      }
    }
    if ((bool3) && (paramMediaPeriodId.adGroupIndex == j) && (paramMediaPeriodId.adIndexInAdGroup == k - 1)) {}
    for (i = 1;; i = 0)
    {
      if (i == 0)
      {
        bool1 = bool2;
        if (!bool3)
        {
          bool1 = bool2;
          if (this.period.getNextAdIndexToPlay(j) != k) {}
        }
      }
      else
      {
        bool1 = true;
      }
      break;
    }
  }
  
  private boolean isLastInTimeline(MediaSource.MediaPeriodId paramMediaPeriodId, boolean paramBoolean)
  {
    int i = this.timeline.getPeriod(paramMediaPeriodId.periodIndex, this.period).windowIndex;
    if ((!this.timeline.getWindow(i, this.window).isDynamic) && (this.timeline.isLastPeriod(paramMediaPeriodId.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled)) && (paramBoolean)) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  public MediaPeriodHolder advancePlayingPeriod()
  {
    if (this.playing != null)
    {
      if (this.playing == this.reading) {
        this.reading = this.playing.next;
      }
      this.playing.release();
      this.playing = this.playing.next;
      this.length -= 1;
      if (this.length == 0) {
        this.loading = null;
      }
    }
    for (;;)
    {
      return this.playing;
      this.playing = this.loading;
      this.reading = this.loading;
    }
  }
  
  public MediaPeriodHolder advanceReadingPeriod()
  {
    if ((this.reading != null) && (this.reading.next != null)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.reading = this.reading.next;
      return this.reading;
    }
  }
  
  public void clear()
  {
    MediaPeriodHolder localMediaPeriodHolder = getFrontPeriod();
    if (localMediaPeriodHolder != null)
    {
      localMediaPeriodHolder.release();
      removeAfter(localMediaPeriodHolder);
    }
    this.playing = null;
    this.loading = null;
    this.reading = null;
    this.length = 0;
  }
  
  public MediaPeriod enqueueNextMediaPeriod(RendererCapabilities[] paramArrayOfRendererCapabilities, long paramLong, TrackSelector paramTrackSelector, Allocator paramAllocator, MediaSource paramMediaSource, Object paramObject, MediaPeriodInfo paramMediaPeriodInfo)
  {
    if (this.loading == null) {}
    for (paramLong = paramMediaPeriodInfo.startPositionUs + paramLong;; paramLong = this.loading.getRendererOffset() + this.loading.info.durationUs)
    {
      paramArrayOfRendererCapabilities = new MediaPeriodHolder(paramArrayOfRendererCapabilities, paramLong, paramTrackSelector, paramAllocator, paramMediaSource, paramObject, paramMediaPeriodInfo);
      if (this.loading != null)
      {
        Assertions.checkState(hasPlayingPeriod());
        this.loading.next = paramArrayOfRendererCapabilities;
      }
      this.loading = paramArrayOfRendererCapabilities;
      this.length += 1;
      return paramArrayOfRendererCapabilities.mediaPeriod;
    }
  }
  
  public MediaPeriodHolder getFrontPeriod()
  {
    if (hasPlayingPeriod()) {}
    for (MediaPeriodHolder localMediaPeriodHolder = this.playing;; localMediaPeriodHolder = this.loading) {
      return localMediaPeriodHolder;
    }
  }
  
  public MediaPeriodHolder getLoadingPeriod()
  {
    return this.loading;
  }
  
  public MediaPeriodInfo getNextMediaPeriodInfo(long paramLong, PlaybackInfo paramPlaybackInfo)
  {
    if (this.loading == null) {}
    for (paramPlaybackInfo = getFirstMediaPeriodInfo(paramPlaybackInfo);; paramPlaybackInfo = getFollowingMediaPeriodInfo(this.loading.info, this.loading.getRendererOffset(), paramLong)) {
      return paramPlaybackInfo;
    }
  }
  
  public MediaPeriodHolder getPlayingPeriod()
  {
    return this.playing;
  }
  
  public MediaPeriodHolder getReadingPeriod()
  {
    return this.reading;
  }
  
  public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo)
  {
    return getUpdatedMediaPeriodInfo(paramMediaPeriodInfo, paramMediaPeriodInfo.id);
  }
  
  public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo paramMediaPeriodInfo, int paramInt)
  {
    return getUpdatedMediaPeriodInfo(paramMediaPeriodInfo, paramMediaPeriodInfo.id.copyWithPeriodIndex(paramInt));
  }
  
  public TrackSelectorResult handleLoadingPeriodPrepared(float paramFloat)
    throws ExoPlaybackException
  {
    return this.loading.handlePrepared(paramFloat);
  }
  
  public boolean hasPlayingPeriod()
  {
    if (this.playing != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isLoading(MediaPeriod paramMediaPeriod)
  {
    if ((this.loading != null) && (this.loading.mediaPeriod == paramMediaPeriod)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    if (this.loading != null) {
      this.loading.reevaluateBuffer(paramLong);
    }
  }
  
  public boolean removeAfter(MediaPeriodHolder paramMediaPeriodHolder)
  {
    if (paramMediaPeriodHolder != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      bool = false;
      this.loading = paramMediaPeriodHolder;
      while (paramMediaPeriodHolder.next != null)
      {
        paramMediaPeriodHolder = paramMediaPeriodHolder.next;
        if (paramMediaPeriodHolder == this.reading)
        {
          this.reading = this.playing;
          bool = true;
        }
        paramMediaPeriodHolder.release();
        this.length -= 1;
      }
    }
    this.loading.next = null;
    return bool;
  }
  
  public MediaSource.MediaPeriodId resolveMediaPeriodIdForAds(int paramInt, long paramLong)
  {
    this.timeline.getPeriod(paramInt, this.period);
    int i = this.period.getAdGroupIndexForPositionUs(paramLong);
    if (i == -1) {}
    for (MediaSource.MediaPeriodId localMediaPeriodId = new MediaSource.MediaPeriodId(paramInt);; localMediaPeriodId = new MediaSource.MediaPeriodId(paramInt, i, this.period.getNextAdIndexToPlay(i))) {
      return localMediaPeriodId;
    }
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
  
  public boolean shouldLoadNextMediaPeriod()
  {
    if ((this.loading == null) || ((!this.loading.info.isFinal) && (this.loading.isFullyBuffered()) && (this.loading.info.durationUs != -9223372036854775807L) && (this.length < 100))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/MediaPeriodQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */