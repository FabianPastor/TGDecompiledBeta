package org.telegram.messenger.exoplayer2;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExoPlayerImpl
  implements ExoPlayer
{
  private static final String TAG = "ExoPlayerImpl";
  private final TrackSelectorResult emptyTrackSelectorResult;
  private final Handler eventHandler;
  private boolean hasPendingPrepare;
  private boolean hasPendingSeek;
  private final ExoPlayerImplInternal internalPlayer;
  private final Handler internalPlayerHandler;
  private final CopyOnWriteArraySet<Player.EventListener> listeners;
  private int maskingPeriodIndex;
  private int maskingWindowIndex;
  private long maskingWindowPositionMs;
  private int pendingOperationAcks;
  private final Timeline.Period period;
  private boolean playWhenReady;
  private PlaybackInfo playbackInfo;
  private PlaybackParameters playbackParameters;
  private final Renderer[] renderers;
  private int repeatMode;
  private boolean shuffleModeEnabled;
  private final TrackSelector trackSelector;
  private final Timeline.Window window;
  
  @SuppressLint({"HandlerLeak"})
  public ExoPlayerImpl(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl, Clock paramClock)
  {
    Log.i("ExoPlayerImpl", "Init " + Integer.toHexString(System.identityHashCode(this)) + " [" + "ExoPlayerLib/2.6.1" + "] [" + Util.DEVICE_DEBUG_INFO + "]");
    boolean bool;
    if (paramArrayOfRenderer.length > 0)
    {
      bool = true;
      Assertions.checkState(bool);
      this.renderers = ((Renderer[])Assertions.checkNotNull(paramArrayOfRenderer));
      this.trackSelector = ((TrackSelector)Assertions.checkNotNull(paramTrackSelector));
      this.playWhenReady = false;
      this.repeatMode = 0;
      this.shuffleModeEnabled = false;
      this.listeners = new CopyOnWriteArraySet();
      this.emptyTrackSelectorResult = new TrackSelectorResult(TrackGroupArray.EMPTY, new boolean[paramArrayOfRenderer.length], new TrackSelectionArray(new TrackSelection[paramArrayOfRenderer.length]), null, new RendererConfiguration[paramArrayOfRenderer.length]);
      this.window = new Timeline.Window();
      this.period = new Timeline.Period();
      this.playbackParameters = PlaybackParameters.DEFAULT;
      if (Looper.myLooper() == null) {
        break label293;
      }
    }
    label293:
    for (Looper localLooper = Looper.myLooper();; localLooper = Looper.getMainLooper())
    {
      this.eventHandler = new Handler(localLooper)
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          ExoPlayerImpl.this.handleEvent(paramAnonymousMessage);
        }
      };
      this.playbackInfo = new PlaybackInfo(Timeline.EMPTY, 0L, this.emptyTrackSelectorResult);
      this.internalPlayer = new ExoPlayerImplInternal(paramArrayOfRenderer, paramTrackSelector, this.emptyTrackSelectorResult, paramLoadControl, this.playWhenReady, this.repeatMode, this.shuffleModeEnabled, this.eventHandler, this, paramClock);
      this.internalPlayerHandler = new Handler(this.internalPlayer.getPlaybackLooper());
      return;
      bool = false;
      break;
    }
  }
  
  private PlaybackInfo getResetPlaybackInfo(boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    Timeline localTimeline;
    label28:
    Object localObject;
    label35:
    MediaSource.MediaPeriodId localMediaPeriodId;
    long l1;
    long l2;
    if (paramBoolean1)
    {
      this.maskingWindowIndex = 0;
      this.maskingPeriodIndex = 0;
      this.maskingWindowPositionMs = 0L;
      if (!paramBoolean2) {
        break label121;
      }
      localTimeline = Timeline.EMPTY;
      if (!paramBoolean2) {
        break label133;
      }
      localObject = null;
      localMediaPeriodId = this.playbackInfo.periodId;
      l1 = this.playbackInfo.startPositionUs;
      l2 = this.playbackInfo.contentPositionUs;
      if (!paramBoolean2) {
        break label145;
      }
    }
    label121:
    label133:
    label145:
    for (TrackSelectorResult localTrackSelectorResult = this.emptyTrackSelectorResult;; localTrackSelectorResult = this.playbackInfo.trackSelectorResult)
    {
      return new PlaybackInfo(localTimeline, localObject, localMediaPeriodId, l1, l2, paramInt, false, localTrackSelectorResult);
      this.maskingWindowIndex = getCurrentWindowIndex();
      this.maskingPeriodIndex = getCurrentPeriodIndex();
      this.maskingWindowPositionMs = getCurrentPosition();
      break;
      localTimeline = this.playbackInfo.timeline;
      break label28;
      localObject = this.playbackInfo.manifest;
      break label35;
    }
  }
  
  private void handlePlaybackInfo(PlaybackInfo paramPlaybackInfo, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    this.pendingOperationAcks -= paramInt1;
    if (this.pendingOperationAcks == 0)
    {
      PlaybackInfo localPlaybackInfo = paramPlaybackInfo;
      if (paramPlaybackInfo.timeline == null) {
        localPlaybackInfo = paramPlaybackInfo.copyWithTimeline(Timeline.EMPTY, paramPlaybackInfo.manifest);
      }
      paramPlaybackInfo = localPlaybackInfo;
      if (localPlaybackInfo.startPositionUs == -9223372036854775807L) {
        paramPlaybackInfo = localPlaybackInfo.fromNewPosition(localPlaybackInfo.periodId, 0L, localPlaybackInfo.contentPositionUs);
      }
      if (((!this.playbackInfo.timeline.isEmpty()) || (this.hasPendingPrepare)) && (paramPlaybackInfo.timeline.isEmpty()))
      {
        this.maskingPeriodIndex = 0;
        this.maskingWindowIndex = 0;
        this.maskingWindowPositionMs = 0L;
      }
      if (!this.hasPendingPrepare) {
        break label154;
      }
    }
    label154:
    for (paramInt1 = 0;; paramInt1 = 2)
    {
      boolean bool = this.hasPendingSeek;
      this.hasPendingPrepare = false;
      this.hasPendingSeek = false;
      updatePlaybackInfo(paramPlaybackInfo, paramBoolean, paramInt2, paramInt1, bool);
      return;
    }
  }
  
  private long playbackInfoPositionUsToWindowPositionMs(long paramLong)
  {
    long l = C.usToMs(paramLong);
    paramLong = l;
    if (!this.playbackInfo.periodId.isAd())
    {
      this.playbackInfo.timeline.getPeriod(this.playbackInfo.periodId.periodIndex, this.period);
      paramLong = l + this.period.getPositionInWindowMs();
    }
    return paramLong;
  }
  
  private boolean shouldMaskPosition()
  {
    if ((this.playbackInfo.timeline.isEmpty()) || (this.pendingOperationAcks > 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void updatePlaybackInfo(PlaybackInfo paramPlaybackInfo, boolean paramBoolean1, int paramInt1, int paramInt2, boolean paramBoolean2)
  {
    int i;
    int j;
    label48:
    int k;
    if ((this.playbackInfo.timeline != paramPlaybackInfo.timeline) || (this.playbackInfo.manifest != paramPlaybackInfo.manifest))
    {
      i = 1;
      if (this.playbackInfo.playbackState == paramPlaybackInfo.playbackState) {
        break label153;
      }
      j = 1;
      if (this.playbackInfo.isLoading == paramPlaybackInfo.isLoading) {
        break label159;
      }
      k = 1;
      label65:
      if (this.playbackInfo.trackSelectorResult == paramPlaybackInfo.trackSelectorResult) {
        break label165;
      }
    }
    label153:
    label159:
    label165:
    for (int m = 1;; m = 0)
    {
      this.playbackInfo = paramPlaybackInfo;
      if ((i == 0) && (paramInt2 != 0)) {
        break label171;
      }
      paramPlaybackInfo = this.listeners.iterator();
      while (paramPlaybackInfo.hasNext()) {
        ((Player.EventListener)paramPlaybackInfo.next()).onTimelineChanged(this.playbackInfo.timeline, this.playbackInfo.manifest, paramInt2);
      }
      i = 0;
      break;
      j = 0;
      break label48;
      k = 0;
      break label65;
    }
    label171:
    if (paramBoolean1)
    {
      paramPlaybackInfo = this.listeners.iterator();
      while (paramPlaybackInfo.hasNext()) {
        ((Player.EventListener)paramPlaybackInfo.next()).onPositionDiscontinuity(paramInt1);
      }
    }
    if (m != 0)
    {
      this.trackSelector.onSelectionActivated(this.playbackInfo.trackSelectorResult.info);
      paramPlaybackInfo = this.listeners.iterator();
      while (paramPlaybackInfo.hasNext()) {
        ((Player.EventListener)paramPlaybackInfo.next()).onTracksChanged(this.playbackInfo.trackSelectorResult.groups, this.playbackInfo.trackSelectorResult.selections);
      }
    }
    if (k != 0)
    {
      paramPlaybackInfo = this.listeners.iterator();
      while (paramPlaybackInfo.hasNext()) {
        ((Player.EventListener)paramPlaybackInfo.next()).onLoadingChanged(this.playbackInfo.isLoading);
      }
    }
    if (j != 0)
    {
      paramPlaybackInfo = this.listeners.iterator();
      while (paramPlaybackInfo.hasNext()) {
        ((Player.EventListener)paramPlaybackInfo.next()).onPlayerStateChanged(this.playWhenReady, this.playbackInfo.playbackState);
      }
    }
    if (paramBoolean2)
    {
      paramPlaybackInfo = this.listeners.iterator();
      while (paramPlaybackInfo.hasNext()) {
        ((Player.EventListener)paramPlaybackInfo.next()).onSeekProcessed();
      }
    }
  }
  
  public void addListener(Player.EventListener paramEventListener)
  {
    this.listeners.add(paramEventListener);
  }
  
  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramVarArgs.length;
    Object localObject;
    for (int j = 0; j < i; j++)
    {
      localObject = paramVarArgs[j];
      localArrayList.add(createMessage(((ExoPlayer.ExoPlayerMessage)localObject).target).setType(((ExoPlayer.ExoPlayerMessage)localObject).messageType).setPayload(((ExoPlayer.ExoPlayerMessage)localObject).message).send());
    }
    int k = 0;
    paramVarArgs = localArrayList.iterator();
    if (paramVarArgs.hasNext())
    {
      localObject = (PlayerMessage)paramVarArgs.next();
      i = 1;
      j = k;
      for (;;)
      {
        k = j;
        if (i == 0) {
          break;
        }
        try
        {
          ((PlayerMessage)localObject).blockUntilDelivered();
          i = 0;
        }
        catch (InterruptedException localInterruptedException)
        {
          j = 1;
        }
      }
    }
    if (k != 0) {
      Thread.currentThread().interrupt();
    }
  }
  
  public PlayerMessage createMessage(PlayerMessage.Target paramTarget)
  {
    return new PlayerMessage(this.internalPlayer, paramTarget, this.playbackInfo.timeline, getCurrentWindowIndex(), this.internalPlayerHandler);
  }
  
  public int getBufferedPercentage()
  {
    int i = 100;
    long l1 = getBufferedPosition();
    long l2 = getDuration();
    if ((l1 == -9223372036854775807L) || (l2 == -9223372036854775807L)) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if (l2 != 0L) {
        i = Util.constrainValue((int)(100L * l1 / l2), 0, 100);
      }
    }
  }
  
  public long getBufferedPosition()
  {
    if (shouldMaskPosition()) {}
    for (long l = this.maskingWindowPositionMs;; l = playbackInfoPositionUsToWindowPositionMs(this.playbackInfo.bufferedPositionUs)) {
      return l;
    }
  }
  
  public long getContentPosition()
  {
    if (isPlayingAd()) {
      this.playbackInfo.timeline.getPeriod(this.playbackInfo.periodId.periodIndex, this.period);
    }
    for (long l = this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.contentPositionUs);; l = getCurrentPosition()) {
      return l;
    }
  }
  
  public int getCurrentAdGroupIndex()
  {
    if (isPlayingAd()) {}
    for (int i = this.playbackInfo.periodId.adGroupIndex;; i = -1) {
      return i;
    }
  }
  
  public int getCurrentAdIndexInAdGroup()
  {
    if (isPlayingAd()) {}
    for (int i = this.playbackInfo.periodId.adIndexInAdGroup;; i = -1) {
      return i;
    }
  }
  
  public Object getCurrentManifest()
  {
    return this.playbackInfo.manifest;
  }
  
  public int getCurrentPeriodIndex()
  {
    if (shouldMaskPosition()) {}
    for (int i = this.maskingPeriodIndex;; i = this.playbackInfo.periodId.periodIndex) {
      return i;
    }
  }
  
  public long getCurrentPosition()
  {
    if (shouldMaskPosition()) {}
    for (long l = this.maskingWindowPositionMs;; l = playbackInfoPositionUsToWindowPositionMs(this.playbackInfo.positionUs)) {
      return l;
    }
  }
  
  public Timeline getCurrentTimeline()
  {
    return this.playbackInfo.timeline;
  }
  
  public TrackGroupArray getCurrentTrackGroups()
  {
    return this.playbackInfo.trackSelectorResult.groups;
  }
  
  public TrackSelectionArray getCurrentTrackSelections()
  {
    return this.playbackInfo.trackSelectorResult.selections;
  }
  
  public int getCurrentWindowIndex()
  {
    if (shouldMaskPosition()) {}
    for (int i = this.maskingWindowIndex;; i = this.playbackInfo.timeline.getPeriod(this.playbackInfo.periodId.periodIndex, this.period).windowIndex) {
      return i;
    }
  }
  
  public long getDuration()
  {
    Timeline localTimeline = this.playbackInfo.timeline;
    long l;
    if (localTimeline.isEmpty()) {
      l = -9223372036854775807L;
    }
    for (;;)
    {
      return l;
      if (isPlayingAd())
      {
        MediaSource.MediaPeriodId localMediaPeriodId = this.playbackInfo.periodId;
        localTimeline.getPeriod(localMediaPeriodId.periodIndex, this.period);
        l = C.usToMs(this.period.getAdDurationUs(localMediaPeriodId.adGroupIndex, localMediaPeriodId.adIndexInAdGroup));
      }
      else
      {
        l = localTimeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
      }
    }
  }
  
  public int getNextWindowIndex()
  {
    Timeline localTimeline = this.playbackInfo.timeline;
    if (localTimeline.isEmpty()) {}
    for (int i = -1;; i = localTimeline.getNextWindowIndex(getCurrentWindowIndex(), this.repeatMode, this.shuffleModeEnabled)) {
      return i;
    }
  }
  
  public boolean getPlayWhenReady()
  {
    return this.playWhenReady;
  }
  
  public Looper getPlaybackLooper()
  {
    return this.internalPlayer.getPlaybackLooper();
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return this.playbackParameters;
  }
  
  public int getPlaybackState()
  {
    return this.playbackInfo.playbackState;
  }
  
  public int getPreviousWindowIndex()
  {
    Timeline localTimeline = this.playbackInfo.timeline;
    if (localTimeline.isEmpty()) {}
    for (int i = -1;; i = localTimeline.getPreviousWindowIndex(getCurrentWindowIndex(), this.repeatMode, this.shuffleModeEnabled)) {
      return i;
    }
  }
  
  public int getRendererCount()
  {
    return this.renderers.length;
  }
  
  public int getRendererType(int paramInt)
  {
    return this.renderers[paramInt].getTrackType();
  }
  
  public int getRepeatMode()
  {
    return this.repeatMode;
  }
  
  public boolean getShuffleModeEnabled()
  {
    return this.shuffleModeEnabled;
  }
  
  public Player.TextComponent getTextComponent()
  {
    return null;
  }
  
  public Player.VideoComponent getVideoComponent()
  {
    return null;
  }
  
  void handleEvent(Message paramMessage)
  {
    Object localObject;
    boolean bool;
    switch (paramMessage.what)
    {
    default: 
      throw new IllegalStateException();
    case 0: 
      localObject = (PlaybackInfo)paramMessage.obj;
      int i = paramMessage.arg1;
      if (paramMessage.arg2 != -1)
      {
        bool = true;
        handlePlaybackInfo((PlaybackInfo)localObject, i, bool, paramMessage.arg2);
      }
      break;
    }
    for (;;)
    {
      return;
      bool = false;
      break;
      paramMessage = (PlaybackParameters)paramMessage.obj;
      if (!this.playbackParameters.equals(paramMessage))
      {
        this.playbackParameters = paramMessage;
        localObject = this.listeners.iterator();
        while (((Iterator)localObject).hasNext()) {
          ((Player.EventListener)((Iterator)localObject).next()).onPlaybackParametersChanged(paramMessage);
        }
        continue;
        localObject = (ExoPlaybackException)paramMessage.obj;
        paramMessage = this.listeners.iterator();
        while (paramMessage.hasNext()) {
          ((Player.EventListener)paramMessage.next()).onPlayerError((ExoPlaybackException)localObject);
        }
      }
    }
  }
  
  public boolean isCurrentWindowDynamic()
  {
    Timeline localTimeline = this.playbackInfo.timeline;
    if ((!localTimeline.isEmpty()) && (localTimeline.getWindow(getCurrentWindowIndex(), this.window).isDynamic)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isCurrentWindowSeekable()
  {
    Timeline localTimeline = this.playbackInfo.timeline;
    if ((!localTimeline.isEmpty()) && (localTimeline.getWindow(getCurrentWindowIndex(), this.window).isSeekable)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isLoading()
  {
    return this.playbackInfo.isLoading;
  }
  
  public boolean isPlayingAd()
  {
    if ((!shouldMaskPosition()) && (this.playbackInfo.periodId.isAd())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void prepare(MediaSource paramMediaSource)
  {
    prepare(paramMediaSource, true, true);
  }
  
  public void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    PlaybackInfo localPlaybackInfo = getResetPlaybackInfo(paramBoolean1, paramBoolean2, 2);
    this.hasPendingPrepare = true;
    this.pendingOperationAcks += 1;
    this.internalPlayer.prepare(paramMediaSource, paramBoolean1);
    updatePlaybackInfo(localPlaybackInfo, false, 4, 1, false);
  }
  
  public void release()
  {
    Log.i("ExoPlayerImpl", "Release " + Integer.toHexString(System.identityHashCode(this)) + " [" + "ExoPlayerLib/2.6.1" + "] [" + Util.DEVICE_DEBUG_INFO + "] [" + ExoPlayerLibraryInfo.registeredModules() + "]");
    this.internalPlayer.release();
    this.eventHandler.removeCallbacksAndMessages(null);
  }
  
  public void removeListener(Player.EventListener paramEventListener)
  {
    this.listeners.remove(paramEventListener);
  }
  
  public void seekTo(int paramInt, long paramLong)
  {
    Object localObject = this.playbackInfo.timeline;
    if ((paramInt < 0) || ((!((Timeline)localObject).isEmpty()) && (paramInt >= ((Timeline)localObject).getWindowCount()))) {
      throw new IllegalSeekPositionException((Timeline)localObject, paramInt, paramLong);
    }
    this.hasPendingSeek = true;
    this.pendingOperationAcks += 1;
    if (isPlayingAd())
    {
      Log.w("ExoPlayerImpl", "seekTo ignored because an ad is playing");
      this.eventHandler.obtainMessage(0, 1, -1, this.playbackInfo).sendToTarget();
      return;
    }
    this.maskingWindowIndex = paramInt;
    if (((Timeline)localObject).isEmpty())
    {
      if (paramLong == -9223372036854775807L) {}
      for (l = 0L;; l = paramLong)
      {
        this.maskingWindowPositionMs = l;
        this.maskingPeriodIndex = 0;
        this.internalPlayer.seekTo((Timeline)localObject, paramInt, C.msToUs(paramLong));
        localObject = this.listeners.iterator();
        while (((Iterator)localObject).hasNext()) {
          ((Player.EventListener)((Iterator)localObject).next()).onPositionDiscontinuity(1);
        }
        break;
      }
    }
    if (paramLong == -9223372036854775807L) {}
    for (long l = ((Timeline)localObject).getWindow(paramInt, this.window).getDefaultPositionUs();; l = C.msToUs(paramLong))
    {
      Pair localPair = ((Timeline)localObject).getPeriodPosition(this.window, this.period, paramInt, l);
      this.maskingWindowPositionMs = C.usToMs(l);
      this.maskingPeriodIndex = ((Integer)localPair.first).intValue();
      break;
    }
  }
  
  public void seekTo(long paramLong)
  {
    seekTo(getCurrentWindowIndex(), paramLong);
  }
  
  public void seekToDefaultPosition()
  {
    seekToDefaultPosition(getCurrentWindowIndex());
  }
  
  public void seekToDefaultPosition(int paramInt)
  {
    seekTo(paramInt, -9223372036854775807L);
  }
  
  public void sendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    int i = paramVarArgs.length;
    for (int j = 0; j < i; j++)
    {
      ExoPlayer.ExoPlayerMessage localExoPlayerMessage = paramVarArgs[j];
      createMessage(localExoPlayerMessage.target).setType(localExoPlayerMessage.messageType).setPayload(localExoPlayerMessage.message).send();
    }
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    if (this.playWhenReady != paramBoolean)
    {
      this.playWhenReady = paramBoolean;
      this.internalPlayer.setPlayWhenReady(paramBoolean);
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext()) {
        ((Player.EventListener)localIterator.next()).onPlayerStateChanged(paramBoolean, this.playbackInfo.playbackState);
      }
    }
  }
  
  public void setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    PlaybackParameters localPlaybackParameters = paramPlaybackParameters;
    if (paramPlaybackParameters == null) {
      localPlaybackParameters = PlaybackParameters.DEFAULT;
    }
    this.internalPlayer.setPlaybackParameters(localPlaybackParameters);
  }
  
  public void setRepeatMode(int paramInt)
  {
    if (this.repeatMode != paramInt)
    {
      this.repeatMode = paramInt;
      this.internalPlayer.setRepeatMode(paramInt);
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext()) {
        ((Player.EventListener)localIterator.next()).onRepeatModeChanged(paramInt);
      }
    }
  }
  
  public void setSeekParameters(SeekParameters paramSeekParameters)
  {
    SeekParameters localSeekParameters = paramSeekParameters;
    if (paramSeekParameters == null) {
      localSeekParameters = SeekParameters.DEFAULT;
    }
    this.internalPlayer.setSeekParameters(localSeekParameters);
  }
  
  public void setShuffleModeEnabled(boolean paramBoolean)
  {
    if (this.shuffleModeEnabled != paramBoolean)
    {
      this.shuffleModeEnabled = paramBoolean;
      this.internalPlayer.setShuffleModeEnabled(paramBoolean);
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext()) {
        ((Player.EventListener)localIterator.next()).onShuffleModeEnabledChanged(paramBoolean);
      }
    }
  }
  
  public void stop()
  {
    stop(false);
  }
  
  public void stop(boolean paramBoolean)
  {
    PlaybackInfo localPlaybackInfo = getResetPlaybackInfo(paramBoolean, paramBoolean, 1);
    this.pendingOperationAcks += 1;
    this.internalPlayer.stop(paramBoolean);
    updatePlaybackInfo(localPlaybackInfo, false, 4, 1, false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ExoPlayerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */