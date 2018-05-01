package org.telegram.messenger.exoplayer2;

import android.util.Log;
import org.telegram.messenger.exoplayer2.source.ClippingMediaPeriod;
import org.telegram.messenger.exoplayer2.source.EmptySampleStream;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class MediaPeriodHolder
{
  private static final String TAG = "MediaPeriodHolder";
  public boolean hasEnabledTracks;
  public MediaPeriodInfo info;
  public final boolean[] mayRetainStreamFlags;
  public final MediaPeriod mediaPeriod;
  private final MediaSource mediaSource;
  public MediaPeriodHolder next;
  private TrackSelectorResult periodTrackSelectorResult;
  public boolean prepared;
  private final RendererCapabilities[] rendererCapabilities;
  public long rendererPositionOffsetUs;
  public final SampleStream[] sampleStreams;
  private final TrackSelector trackSelector;
  public TrackSelectorResult trackSelectorResult;
  public final Object uid;
  
  public MediaPeriodHolder(RendererCapabilities[] paramArrayOfRendererCapabilities, long paramLong, TrackSelector paramTrackSelector, Allocator paramAllocator, MediaSource paramMediaSource, Object paramObject, MediaPeriodInfo paramMediaPeriodInfo)
  {
    this.rendererCapabilities = paramArrayOfRendererCapabilities;
    this.rendererPositionOffsetUs = (paramLong - paramMediaPeriodInfo.startPositionUs);
    this.trackSelector = paramTrackSelector;
    this.mediaSource = paramMediaSource;
    this.uid = Assertions.checkNotNull(paramObject);
    this.info = paramMediaPeriodInfo;
    this.sampleStreams = new SampleStream[paramArrayOfRendererCapabilities.length];
    this.mayRetainStreamFlags = new boolean[paramArrayOfRendererCapabilities.length];
    paramTrackSelector = paramMediaSource.createPeriod(paramMediaPeriodInfo.id, paramAllocator);
    paramArrayOfRendererCapabilities = paramTrackSelector;
    if (paramMediaPeriodInfo.endPositionUs != Long.MIN_VALUE)
    {
      paramArrayOfRendererCapabilities = new ClippingMediaPeriod(paramTrackSelector, true);
      paramArrayOfRendererCapabilities.setClipping(0L, paramMediaPeriodInfo.endPositionUs);
    }
    this.mediaPeriod = paramArrayOfRendererCapabilities;
  }
  
  private void associateNoSampleRenderersWithEmptySampleStream(SampleStream[] paramArrayOfSampleStream)
  {
    for (int i = 0; i < this.rendererCapabilities.length; i++) {
      if ((this.rendererCapabilities[i].getTrackType() == 5) && (this.trackSelectorResult.renderersEnabled[i] != 0)) {
        paramArrayOfSampleStream[i] = new EmptySampleStream();
      }
    }
  }
  
  private void disableTrackSelectionsInResult(TrackSelectorResult paramTrackSelectorResult)
  {
    for (int i = 0; i < paramTrackSelectorResult.renderersEnabled.length; i++)
    {
      int j = paramTrackSelectorResult.renderersEnabled[i];
      TrackSelection localTrackSelection = paramTrackSelectorResult.selections.get(i);
      if ((j != 0) && (localTrackSelection != null)) {
        localTrackSelection.disable();
      }
    }
  }
  
  private void disassociateNoSampleRenderersWithEmptySampleStream(SampleStream[] paramArrayOfSampleStream)
  {
    for (int i = 0; i < this.rendererCapabilities.length; i++) {
      if (this.rendererCapabilities[i].getTrackType() == 5) {
        paramArrayOfSampleStream[i] = null;
      }
    }
  }
  
  private void enableTrackSelectionsInResult(TrackSelectorResult paramTrackSelectorResult)
  {
    for (int i = 0; i < paramTrackSelectorResult.renderersEnabled.length; i++)
    {
      int j = paramTrackSelectorResult.renderersEnabled[i];
      TrackSelection localTrackSelection = paramTrackSelectorResult.selections.get(i);
      if ((j != 0) && (localTrackSelection != null)) {
        localTrackSelection.enable();
      }
    }
  }
  
  private void updatePeriodTrackSelectorResult(TrackSelectorResult paramTrackSelectorResult)
  {
    if (this.periodTrackSelectorResult != null) {
      disableTrackSelectionsInResult(this.periodTrackSelectorResult);
    }
    this.periodTrackSelectorResult = paramTrackSelectorResult;
    if (this.periodTrackSelectorResult != null) {
      enableTrackSelectionsInResult(this.periodTrackSelectorResult);
    }
  }
  
  public long applyTrackSelection(long paramLong, boolean paramBoolean)
  {
    return applyTrackSelection(paramLong, paramBoolean, new boolean[this.rendererCapabilities.length]);
  }
  
  public long applyTrackSelection(long paramLong, boolean paramBoolean, boolean[] paramArrayOfBoolean)
  {
    TrackSelectionArray localTrackSelectionArray = this.trackSelectorResult.selections;
    int i = 0;
    if (i < localTrackSelectionArray.length)
    {
      boolean[] arrayOfBoolean = this.mayRetainStreamFlags;
      if ((!paramBoolean) && (this.trackSelectorResult.isEquivalent(this.periodTrackSelectorResult, i))) {}
      for (int j = 1;; j = 0)
      {
        arrayOfBoolean[i] = j;
        i++;
        break;
      }
    }
    disassociateNoSampleRenderersWithEmptySampleStream(this.sampleStreams);
    updatePeriodTrackSelectorResult(this.trackSelectorResult);
    paramLong = this.mediaPeriod.selectTracks(localTrackSelectionArray.getAll(), this.mayRetainStreamFlags, this.sampleStreams, paramArrayOfBoolean, paramLong);
    associateNoSampleRenderersWithEmptySampleStream(this.sampleStreams);
    this.hasEnabledTracks = false;
    i = 0;
    while (i < this.sampleStreams.length) {
      if (this.sampleStreams[i] != null)
      {
        Assertions.checkState(this.trackSelectorResult.renderersEnabled[i]);
        if (this.rendererCapabilities[i].getTrackType() != 5) {
          this.hasEnabledTracks = true;
        }
        i++;
      }
      else
      {
        if (localTrackSelectionArray.get(i) == null) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          Assertions.checkState(paramBoolean);
          break;
        }
      }
    }
    return paramLong;
  }
  
  public void continueLoading(long paramLong)
  {
    paramLong = toPeriodTime(paramLong);
    this.mediaPeriod.continueLoading(paramLong);
  }
  
  public long getBufferedPositionUs(boolean paramBoolean)
  {
    long l1;
    if (!this.prepared) {
      l1 = this.info.startPositionUs;
    }
    for (;;)
    {
      return l1;
      long l2 = this.mediaPeriod.getBufferedPositionUs();
      l1 = l2;
      if (l2 == Long.MIN_VALUE)
      {
        l1 = l2;
        if (paramBoolean) {
          l1 = this.info.durationUs;
        }
      }
    }
  }
  
  public long getDurationUs()
  {
    return this.info.durationUs;
  }
  
  public long getNextLoadPositionUs()
  {
    if (!this.prepared) {}
    for (long l = 0L;; l = this.mediaPeriod.getNextLoadPositionUs()) {
      return l;
    }
  }
  
  public long getRendererOffset()
  {
    return this.rendererPositionOffsetUs;
  }
  
  public TrackSelectorResult handlePrepared(float paramFloat)
    throws ExoPlaybackException
  {
    this.prepared = true;
    selectTracks(paramFloat);
    long l = applyTrackSelection(this.info.startPositionUs, false);
    this.rendererPositionOffsetUs += this.info.startPositionUs - l;
    this.info = this.info.copyWithStartPositionUs(l);
    return this.trackSelectorResult;
  }
  
  public boolean isFullyBuffered()
  {
    if ((this.prepared) && ((!this.hasEnabledTracks) || (this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    if (this.prepared) {
      this.mediaPeriod.reevaluateBuffer(toPeriodTime(paramLong));
    }
  }
  
  public void release()
  {
    updatePeriodTrackSelectorResult(null);
    try
    {
      if (this.info.endPositionUs != Long.MIN_VALUE) {
        this.mediaSource.releasePeriod(((ClippingMediaPeriod)this.mediaPeriod).mediaPeriod);
      }
      for (;;)
      {
        return;
        this.mediaSource.releasePeriod(this.mediaPeriod);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Log.e("MediaPeriodHolder", "Period release failed.", localRuntimeException);
      }
    }
  }
  
  public boolean selectTracks(float paramFloat)
    throws ExoPlaybackException
  {
    int i = 0;
    boolean bool = false;
    Object localObject1 = this.trackSelector.selectTracks(this.rendererCapabilities, this.mediaPeriod.getTrackGroups());
    if (((TrackSelectorResult)localObject1).isEquivalent(this.periodTrackSelectorResult)) {}
    for (;;)
    {
      return bool;
      this.trackSelectorResult = ((TrackSelectorResult)localObject1);
      localObject1 = this.trackSelectorResult.selections.getAll();
      int j = localObject1.length;
      while (i < j)
      {
        Object localObject2 = localObject1[i];
        if (localObject2 != null) {
          ((TrackSelection)localObject2).onPlaybackSpeed(paramFloat);
        }
        i++;
      }
      bool = true;
    }
  }
  
  public long toPeriodTime(long paramLong)
  {
    return paramLong - getRendererOffset();
  }
  
  public long toRendererTime(long paramLong)
  {
    return getRendererOffset() + paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/MediaPeriodHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */