package org.telegram.messenger.exoplayer2;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaPeriod.Callback;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.HandlerWrapper;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExoPlayerImplInternal
  implements Handler.Callback, DefaultMediaClock.PlaybackParameterListener, PlayerMessage.Sender, MediaPeriod.Callback, MediaSource.Listener, TrackSelector.InvalidationListener
{
  private static final int IDLE_INTERVAL_MS = 1000;
  private static final int MSG_DO_SOME_WORK = 2;
  public static final int MSG_ERROR = 2;
  private static final int MSG_PERIOD_PREPARED = 9;
  public static final int MSG_PLAYBACK_INFO_CHANGED = 0;
  public static final int MSG_PLAYBACK_PARAMETERS_CHANGED = 1;
  private static final int MSG_PREPARE = 0;
  private static final int MSG_REFRESH_SOURCE_INFO = 8;
  private static final int MSG_RELEASE = 7;
  private static final int MSG_SEEK_TO = 3;
  private static final int MSG_SEND_MESSAGE = 14;
  private static final int MSG_SEND_MESSAGE_TO_TARGET_THREAD = 15;
  private static final int MSG_SET_PLAYBACK_PARAMETERS = 4;
  private static final int MSG_SET_PLAY_WHEN_READY = 1;
  private static final int MSG_SET_REPEAT_MODE = 12;
  private static final int MSG_SET_SEEK_PARAMETERS = 5;
  private static final int MSG_SET_SHUFFLE_ENABLED = 13;
  private static final int MSG_SOURCE_CONTINUE_LOADING_REQUESTED = 10;
  private static final int MSG_STOP = 6;
  private static final int MSG_TRACK_SELECTION_INVALIDATED = 11;
  private static final int PREPARING_SOURCE_INTERVAL_MS = 10;
  private static final int RENDERER_TIMESTAMP_OFFSET_US = 60000000;
  private static final int RENDERING_INTERVAL_MS = 10;
  private static final String TAG = "ExoPlayerImplInternal";
  private final long backBufferDurationUs;
  private final Clock clock;
  private final TrackSelectorResult emptyTrackSelectorResult;
  private Renderer[] enabledRenderers;
  private final Handler eventHandler;
  private final HandlerWrapper handler;
  private final HandlerThread internalPlaybackThread;
  private final LoadControl loadControl;
  private final DefaultMediaClock mediaClock;
  private MediaSource mediaSource;
  private int nextPendingMessageIndex;
  private SeekPosition pendingInitialSeekPosition;
  private final ArrayList<PendingMessageInfo> pendingMessages;
  private int pendingPrepareCount;
  private final Timeline.Period period;
  private boolean playWhenReady;
  private PlaybackInfo playbackInfo;
  private final PlaybackInfoUpdate playbackInfoUpdate;
  private final ExoPlayer player;
  private final MediaPeriodQueue queue;
  private boolean rebuffering;
  private boolean released;
  private final RendererCapabilities[] rendererCapabilities;
  private long rendererPositionUs;
  private final Renderer[] renderers;
  private int repeatMode;
  private final boolean retainBackBufferFromKeyframe;
  private SeekParameters seekParameters;
  private boolean shuffleModeEnabled;
  private final TrackSelector trackSelector;
  private final Timeline.Window window;
  
  public ExoPlayerImplInternal(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, TrackSelectorResult paramTrackSelectorResult, LoadControl paramLoadControl, boolean paramBoolean1, int paramInt, boolean paramBoolean2, Handler paramHandler, ExoPlayer paramExoPlayer, Clock paramClock)
  {
    this.renderers = paramArrayOfRenderer;
    this.trackSelector = paramTrackSelector;
    this.emptyTrackSelectorResult = paramTrackSelectorResult;
    this.loadControl = paramLoadControl;
    this.playWhenReady = paramBoolean1;
    this.repeatMode = paramInt;
    this.shuffleModeEnabled = paramBoolean2;
    this.eventHandler = paramHandler;
    this.player = paramExoPlayer;
    this.clock = paramClock;
    this.queue = new MediaPeriodQueue();
    this.backBufferDurationUs = paramLoadControl.getBackBufferDurationUs();
    this.retainBackBufferFromKeyframe = paramLoadControl.retainBackBufferFromKeyframe();
    this.seekParameters = SeekParameters.DEFAULT;
    this.playbackInfo = new PlaybackInfo(null, -9223372036854775807L, paramTrackSelectorResult);
    this.playbackInfoUpdate = new PlaybackInfoUpdate(null);
    this.rendererCapabilities = new RendererCapabilities[paramArrayOfRenderer.length];
    for (paramInt = 0; paramInt < paramArrayOfRenderer.length; paramInt++)
    {
      paramArrayOfRenderer[paramInt].setIndex(paramInt);
      this.rendererCapabilities[paramInt] = paramArrayOfRenderer[paramInt].getCapabilities();
    }
    this.mediaClock = new DefaultMediaClock(this, paramClock);
    this.pendingMessages = new ArrayList();
    this.enabledRenderers = new Renderer[0];
    this.window = new Timeline.Window();
    this.period = new Timeline.Period();
    paramTrackSelector.init(this);
    this.internalPlaybackThread = new HandlerThread("ExoPlayerImplInternal:Handler", -16);
    this.internalPlaybackThread.start();
    this.handler = paramClock.createHandler(this.internalPlaybackThread.getLooper(), this);
  }
  
  private void deliverMessage(PlayerMessage paramPlayerMessage)
  {
    try
    {
      paramPlayerMessage.getTarget().handleMessage(paramPlayerMessage.getType(), paramPlayerMessage.getPayload());
      return;
    }
    catch (ExoPlaybackException localExoPlaybackException)
    {
      for (;;)
      {
        this.eventHandler.obtainMessage(2, localExoPlaybackException).sendToTarget();
        paramPlayerMessage.markAsProcessed(true);
      }
    }
    finally
    {
      paramPlayerMessage.markAsProcessed(true);
    }
  }
  
  private void disableRenderer(Renderer paramRenderer)
    throws ExoPlaybackException
  {
    this.mediaClock.onRendererDisabled(paramRenderer);
    ensureStopped(paramRenderer);
    paramRenderer.disable();
  }
  
  private void doSomeWork()
    throws ExoPlaybackException, IOException
  {
    long l1 = this.clock.uptimeMillis();
    updatePeriods();
    if (!this.queue.hasPlayingPeriod())
    {
      maybeThrowPeriodPrepareError();
      scheduleNextWork(l1, 10L);
      return;
    }
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getPlayingPeriod();
    TraceUtil.beginSection("doSomeWork");
    updatePlaybackPositions();
    long l2 = SystemClock.elapsedRealtime();
    localMediaPeriodHolder.mediaPeriod.discardBuffer(this.playbackInfo.positionUs - this.backBufferDurationUs, this.retainBackBufferFromKeyframe);
    int i = 1;
    boolean bool = true;
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int j = arrayOfRenderer.length;
    int k = 0;
    if (k < j)
    {
      Renderer localRenderer = arrayOfRenderer[k];
      localRenderer.render(this.rendererPositionUs, l2 * 1000L);
      label154:
      int m;
      if ((i != 0) && (localRenderer.isEnded()))
      {
        i = 1;
        if ((!localRenderer.isReady()) && (!localRenderer.isEnded()) && (!rendererWaitingForNextStream(localRenderer))) {
          break label223;
        }
        m = 1;
        label186:
        if (m == 0) {
          localRenderer.maybeThrowStreamError();
        }
        if ((!bool) || (m == 0)) {
          break label229;
        }
      }
      label223:
      label229:
      for (bool = true;; bool = false)
      {
        k++;
        break;
        i = 0;
        break label154;
        m = 0;
        break label186;
      }
    }
    if (!bool) {
      maybeThrowPeriodPrepareError();
    }
    l2 = localMediaPeriodHolder.info.durationUs;
    if ((i != 0) && ((l2 == -9223372036854775807L) || (l2 <= this.playbackInfo.positionUs)) && (localMediaPeriodHolder.info.isFinal))
    {
      setState(4);
      stopRenderers();
    }
    while (this.playbackInfo.playbackState == 2)
    {
      arrayOfRenderer = this.enabledRenderers;
      k = arrayOfRenderer.length;
      i = 0;
      for (;;)
      {
        if (i < k)
        {
          arrayOfRenderer[i].maybeThrowStreamError();
          i++;
          continue;
          if ((this.playbackInfo.playbackState == 2) && (shouldTransitionToReadyState(bool)))
          {
            setState(3);
            if (!this.playWhenReady) {
              break;
            }
            startRenderers();
            break;
          }
          if (this.playbackInfo.playbackState != 3) {
            break;
          }
          if (this.enabledRenderers.length == 0) {
            if (isTimelineReady()) {
              break;
            }
          }
          for (;;)
          {
            this.rebuffering = this.playWhenReady;
            setState(2);
            stopRenderers();
            break;
            if (bool) {
              break;
            }
          }
        }
      }
    }
    if (((this.playWhenReady) && (this.playbackInfo.playbackState == 3)) || (this.playbackInfo.playbackState == 2)) {
      scheduleNextWork(l1, 10L);
    }
    for (;;)
    {
      TraceUtil.endSection();
      break;
      if ((this.enabledRenderers.length != 0) && (this.playbackInfo.playbackState != 4)) {
        scheduleNextWork(l1, 1000L);
      } else {
        this.handler.removeMessages(2);
      }
    }
  }
  
  private void enableRenderer(int paramInt1, boolean paramBoolean, int paramInt2)
    throws ExoPlaybackException
  {
    boolean bool = true;
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getPlayingPeriod();
    Renderer localRenderer = this.renderers[paramInt1];
    this.enabledRenderers[paramInt2] = localRenderer;
    RendererConfiguration localRendererConfiguration;
    Format[] arrayOfFormat;
    if (localRenderer.getState() == 0)
    {
      localRendererConfiguration = localMediaPeriodHolder.trackSelectorResult.rendererConfigurations[paramInt1];
      arrayOfFormat = getFormats(localMediaPeriodHolder.trackSelectorResult.selections.get(paramInt1));
      if ((!this.playWhenReady) || (this.playbackInfo.playbackState != 3)) {
        break label147;
      }
      paramInt2 = 1;
      if ((paramBoolean) || (paramInt2 == 0)) {
        break label152;
      }
    }
    label147:
    label152:
    for (paramBoolean = bool;; paramBoolean = false)
    {
      localRenderer.enable(localRendererConfiguration, arrayOfFormat, localMediaPeriodHolder.sampleStreams[paramInt1], this.rendererPositionUs, paramBoolean, localMediaPeriodHolder.getRendererOffset());
      this.mediaClock.onRendererEnabled(localRenderer);
      if (paramInt2 != 0) {
        localRenderer.start();
      }
      return;
      paramInt2 = 0;
      break;
    }
  }
  
  private void enableRenderers(boolean[] paramArrayOfBoolean, int paramInt)
    throws ExoPlaybackException
  {
    this.enabledRenderers = new Renderer[paramInt];
    int i = 0;
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getPlayingPeriod();
    paramInt = 0;
    while (paramInt < this.renderers.length)
    {
      int j = i;
      if (localMediaPeriodHolder.trackSelectorResult.renderersEnabled[paramInt] != 0)
      {
        enableRenderer(paramInt, paramArrayOfBoolean[paramInt], i);
        j = i + 1;
      }
      paramInt++;
      i = j;
    }
  }
  
  private void ensureStopped(Renderer paramRenderer)
    throws ExoPlaybackException
  {
    if (paramRenderer.getState() == 2) {
      paramRenderer.stop();
    }
  }
  
  private int getFirstPeriodIndex()
  {
    Timeline localTimeline = this.playbackInfo.timeline;
    if ((localTimeline == null) || (localTimeline.isEmpty())) {}
    for (int i = 0;; i = localTimeline.getWindow(localTimeline.getFirstWindowIndex(this.shuffleModeEnabled), this.window).firstPeriodIndex) {
      return i;
    }
  }
  
  private static Format[] getFormats(TrackSelection paramTrackSelection)
  {
    if (paramTrackSelection != null) {}
    Format[] arrayOfFormat;
    for (int i = paramTrackSelection.length();; i = 0)
    {
      arrayOfFormat = new Format[i];
      for (int j = 0; j < i; j++) {
        arrayOfFormat[j] = paramTrackSelection.getFormat(j);
      }
    }
    return arrayOfFormat;
  }
  
  private Pair<Integer, Long> getPeriodPosition(Timeline paramTimeline, int paramInt, long paramLong)
  {
    return paramTimeline.getPeriodPosition(this.window, this.period, paramInt, paramLong);
  }
  
  private void handleContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    if (!this.queue.isLoading(paramMediaPeriod)) {}
    for (;;)
    {
      return;
      this.queue.reevaluateBuffer(this.rendererPositionUs);
      maybeContinueLoading();
    }
  }
  
  private void handlePeriodPrepared(MediaPeriod paramMediaPeriod)
    throws ExoPlaybackException
  {
    if (!this.queue.isLoading(paramMediaPeriod)) {}
    for (;;)
    {
      return;
      updateLoadControlTrackSelection(this.queue.handleLoadingPeriodPrepared(this.mediaClock.getPlaybackParameters().speed));
      if (!this.queue.hasPlayingPeriod())
      {
        resetRendererPosition(this.queue.advancePlayingPeriod().info.startPositionUs);
        updatePlayingPeriodRenderers(null);
      }
      maybeContinueLoading();
    }
  }
  
  private void handleSourceInfoRefreshEndedPlayback()
  {
    setState(4);
    resetInternal(false, true, false);
  }
  
  private void handleSourceInfoRefreshed(MediaSourceRefreshInfo paramMediaSourceRefreshInfo)
    throws ExoPlaybackException
  {
    if (paramMediaSourceRefreshInfo.source != this.mediaSource) {}
    for (;;)
    {
      return;
      Timeline localTimeline = this.playbackInfo.timeline;
      Object localObject1 = paramMediaSourceRefreshInfo.timeline;
      paramMediaSourceRefreshInfo = paramMediaSourceRefreshInfo.manifest;
      this.queue.setTimeline((Timeline)localObject1);
      this.playbackInfo = this.playbackInfo.copyWithTimeline((Timeline)localObject1, paramMediaSourceRefreshInfo);
      resolvePendingMessagePositions();
      int i;
      long l1;
      Object localObject2;
      long l2;
      if (localTimeline == null)
      {
        this.playbackInfoUpdate.incrementPendingOperationAcks(this.pendingPrepareCount);
        this.pendingPrepareCount = 0;
        if (this.pendingInitialSeekPosition != null)
        {
          paramMediaSourceRefreshInfo = resolveSeekPosition(this.pendingInitialSeekPosition, true);
          this.pendingInitialSeekPosition = null;
          if (paramMediaSourceRefreshInfo == null)
          {
            handleSourceInfoRefreshEndedPlayback();
          }
          else
          {
            i = ((Integer)paramMediaSourceRefreshInfo.first).intValue();
            l1 = ((Long)paramMediaSourceRefreshInfo.second).longValue();
            localObject2 = this.queue.resolveMediaPeriodIdForAds(i, l1);
            paramMediaSourceRefreshInfo = this.playbackInfo;
            if (((MediaSource.MediaPeriodId)localObject2).isAd()) {}
            for (l2 = 0L;; l2 = l1)
            {
              this.playbackInfo = paramMediaSourceRefreshInfo.fromNewPosition((MediaSource.MediaPeriodId)localObject2, l2, l1);
              break;
            }
          }
        }
        else if (this.playbackInfo.startPositionUs == -9223372036854775807L)
        {
          if (((Timeline)localObject1).isEmpty())
          {
            handleSourceInfoRefreshEndedPlayback();
          }
          else
          {
            paramMediaSourceRefreshInfo = getPeriodPosition((Timeline)localObject1, ((Timeline)localObject1).getFirstWindowIndex(this.shuffleModeEnabled), -9223372036854775807L);
            i = ((Integer)paramMediaSourceRefreshInfo.first).intValue();
            l1 = ((Long)paramMediaSourceRefreshInfo.second).longValue();
            paramMediaSourceRefreshInfo = this.queue.resolveMediaPeriodIdForAds(i, l1);
            localObject2 = this.playbackInfo;
            if (paramMediaSourceRefreshInfo.isAd()) {}
            for (l2 = 0L;; l2 = l1)
            {
              this.playbackInfo = ((PlaybackInfo)localObject2).fromNewPosition(paramMediaSourceRefreshInfo, l2, l1);
              break;
            }
          }
        }
      }
      else
      {
        int j = this.playbackInfo.periodId.periodIndex;
        localObject2 = this.queue.getFrontPeriod();
        if ((localObject2 != null) || (j < localTimeline.getPeriodCount()))
        {
          if (localObject2 == null) {}
          for (paramMediaSourceRefreshInfo = localTimeline.getPeriod(j, this.period, true).uid;; paramMediaSourceRefreshInfo = ((MediaPeriodHolder)localObject2).uid)
          {
            i = ((Timeline)localObject1).getIndexOfPeriod(paramMediaSourceRefreshInfo);
            if (i != -1) {
              break label591;
            }
            i = resolveSubsequentPeriod(j, localTimeline, (Timeline)localObject1);
            if (i != -1) {
              break label406;
            }
            handleSourceInfoRefreshEndedPlayback();
            break;
          }
          label406:
          paramMediaSourceRefreshInfo = getPeriodPosition((Timeline)localObject1, ((Timeline)localObject1).getPeriod(i, this.period).windowIndex, -9223372036854775807L);
          i = ((Integer)paramMediaSourceRefreshInfo.first).intValue();
          l2 = ((Long)paramMediaSourceRefreshInfo.second).longValue();
          ((Timeline)localObject1).getPeriod(i, this.period, true);
          if (localObject2 != null)
          {
            localObject1 = this.period.uid;
            ((MediaPeriodHolder)localObject2).info = ((MediaPeriodHolder)localObject2).info.copyWithPeriodIndex(-1);
            paramMediaSourceRefreshInfo = (MediaSourceRefreshInfo)localObject2;
            while (paramMediaSourceRefreshInfo.next != null)
            {
              paramMediaSourceRefreshInfo = paramMediaSourceRefreshInfo.next;
              if (paramMediaSourceRefreshInfo.uid.equals(localObject1)) {
                paramMediaSourceRefreshInfo.info = this.queue.getUpdatedMediaPeriodInfo(paramMediaSourceRefreshInfo.info, i);
              } else {
                paramMediaSourceRefreshInfo.info = paramMediaSourceRefreshInfo.info.copyWithPeriodIndex(-1);
              }
            }
          }
          paramMediaSourceRefreshInfo = new MediaSource.MediaPeriodId(i);
          l2 = seekToPeriodPosition(paramMediaSourceRefreshInfo, l2);
          this.playbackInfo = this.playbackInfo.fromNewPosition(paramMediaSourceRefreshInfo, l2, -9223372036854775807L);
          continue;
          label591:
          if (i != j) {
            this.playbackInfo = this.playbackInfo.copyWithPeriodIndex(i);
          }
          if (this.playbackInfo.periodId.isAd())
          {
            paramMediaSourceRefreshInfo = this.queue.resolveMediaPeriodIdForAds(i, this.playbackInfo.contentPositionUs);
            if ((!paramMediaSourceRefreshInfo.isAd()) || (paramMediaSourceRefreshInfo.adIndexInAdGroup != this.playbackInfo.periodId.adIndexInAdGroup))
            {
              l1 = seekToPeriodPosition(paramMediaSourceRefreshInfo, this.playbackInfo.contentPositionUs);
              if (paramMediaSourceRefreshInfo.isAd()) {}
              for (l2 = this.playbackInfo.contentPositionUs;; l2 = -9223372036854775807L)
              {
                this.playbackInfo = this.playbackInfo.fromNewPosition(paramMediaSourceRefreshInfo, l1, l2);
                break;
              }
            }
          }
          if (localObject2 != null)
          {
            for (paramMediaSourceRefreshInfo = updatePeriodInfo((MediaPeriodHolder)localObject2, i); paramMediaSourceRefreshInfo.next != null; paramMediaSourceRefreshInfo = updatePeriodInfo((MediaPeriodHolder)localObject2, i))
            {
              localObject2 = paramMediaSourceRefreshInfo.next;
              i = ((Timeline)localObject1).getNextPeriodIndex(i, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
              if ((i == -1) || (!((MediaPeriodHolder)localObject2).uid.equals(((Timeline)localObject1).getPeriod(i, this.period, true).uid))) {
                break label816;
              }
            }
            continue;
            label816:
            if (this.queue.removeAfter(paramMediaSourceRefreshInfo))
            {
              paramMediaSourceRefreshInfo = this.queue.getPlayingPeriod().info.id;
              l2 = seekToPeriodPosition(paramMediaSourceRefreshInfo, this.playbackInfo.positionUs, true);
              this.playbackInfo = this.playbackInfo.fromNewPosition(paramMediaSourceRefreshInfo, l2, this.playbackInfo.contentPositionUs);
            }
          }
        }
      }
    }
  }
  
  private boolean isTimelineReady()
  {
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getPlayingPeriod();
    long l = localMediaPeriodHolder.info.durationUs;
    if ((l == -9223372036854775807L) || (this.playbackInfo.positionUs < l) || ((localMediaPeriodHolder.next != null) && ((localMediaPeriodHolder.next.prepared) || (localMediaPeriodHolder.next.info.id.isAd())))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void maybeContinueLoading()
  {
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getLoadingPeriod();
    long l1 = localMediaPeriodHolder.getNextLoadPositionUs();
    if (l1 == Long.MIN_VALUE) {
      setIsLoading(false);
    }
    for (;;)
    {
      return;
      long l2 = localMediaPeriodHolder.toPeriodTime(this.rendererPositionUs);
      boolean bool = this.loadControl.shouldContinueLoading(l1 - l2, this.mediaClock.getPlaybackParameters().speed);
      setIsLoading(bool);
      if (bool) {
        localMediaPeriodHolder.continueLoading(this.rendererPositionUs);
      }
    }
  }
  
  private void maybeNotifyPlaybackInfoChanged()
  {
    Handler localHandler;
    int i;
    if (this.playbackInfoUpdate.hasPendingUpdate(this.playbackInfo))
    {
      localHandler = this.eventHandler;
      i = this.playbackInfoUpdate.operationAcks;
      if (!this.playbackInfoUpdate.positionDiscontinuity) {
        break label71;
      }
    }
    label71:
    for (int j = this.playbackInfoUpdate.discontinuityReason;; j = -1)
    {
      localHandler.obtainMessage(0, i, j, this.playbackInfo).sendToTarget();
      this.playbackInfoUpdate.reset(this.playbackInfo);
      return;
    }
  }
  
  private void maybeThrowPeriodPrepareError()
    throws IOException
  {
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getLoadingPeriod();
    Object localObject = this.queue.getReadingPeriod();
    int j;
    if ((localMediaPeriodHolder != null) && (!localMediaPeriodHolder.prepared) && ((localObject == null) || (((MediaPeriodHolder)localObject).next == localMediaPeriodHolder)))
    {
      localObject = this.enabledRenderers;
      int i = localObject.length;
      j = 0;
      if (j >= i) {
        break label75;
      }
      if (localObject[j].hasReadStreamToEnd()) {
        break label69;
      }
    }
    for (;;)
    {
      return;
      label69:
      j++;
      break;
      label75:
      localMediaPeriodHolder.mediaPeriod.maybeThrowPrepareError();
    }
  }
  
  private void maybeTriggerPendingMessages(long paramLong1, long paramLong2)
  {
    if ((this.pendingMessages.isEmpty()) || (this.playbackInfo.periodId.isAd())) {
      return;
    }
    long l = paramLong1;
    if (this.playbackInfo.startPositionUs == paramLong1) {
      l = paramLong1 - 1L;
    }
    int i = this.playbackInfo.periodId.periodIndex;
    if (this.nextPendingMessageIndex > 0)
    {
      localPendingMessageInfo1 = (PendingMessageInfo)this.pendingMessages.get(this.nextPendingMessageIndex - 1);
      if ((localPendingMessageInfo1 == null) || ((localPendingMessageInfo1.resolvedPeriodIndex <= i) && ((localPendingMessageInfo1.resolvedPeriodIndex != i) || (localPendingMessageInfo1.resolvedPeriodTimeUs <= l)))) {
        break label167;
      }
      this.nextPendingMessageIndex -= 1;
      if (this.nextPendingMessageIndex <= 0) {
        break label161;
      }
    }
    label161:
    for (PendingMessageInfo localPendingMessageInfo1 = (PendingMessageInfo)this.pendingMessages.get(this.nextPendingMessageIndex - 1);; localPendingMessageInfo1 = null)
    {
      break;
      localPendingMessageInfo1 = null;
      break;
    }
    label167:
    PendingMessageInfo localPendingMessageInfo2;
    if (this.nextPendingMessageIndex < this.pendingMessages.size())
    {
      localPendingMessageInfo1 = (PendingMessageInfo)this.pendingMessages.get(this.nextPendingMessageIndex);
      localPendingMessageInfo2 = localPendingMessageInfo1;
      if (localPendingMessageInfo1 == null) {
        break label312;
      }
      localPendingMessageInfo2 = localPendingMessageInfo1;
      if (localPendingMessageInfo1.resolvedPeriodUid == null) {
        break label312;
      }
      if (localPendingMessageInfo1.resolvedPeriodIndex >= i)
      {
        localPendingMessageInfo2 = localPendingMessageInfo1;
        if (localPendingMessageInfo1.resolvedPeriodIndex != i) {
          break label312;
        }
        localPendingMessageInfo2 = localPendingMessageInfo1;
        if (localPendingMessageInfo1.resolvedPeriodTimeUs > l) {
          break label312;
        }
      }
      this.nextPendingMessageIndex += 1;
      if (this.nextPendingMessageIndex >= this.pendingMessages.size()) {
        break label306;
      }
    }
    label306:
    for (localPendingMessageInfo1 = (PendingMessageInfo)this.pendingMessages.get(this.nextPendingMessageIndex);; localPendingMessageInfo1 = null)
    {
      break;
      localPendingMessageInfo1 = null;
      break;
    }
    label312:
    if ((localPendingMessageInfo2 != null) && (localPendingMessageInfo2.resolvedPeriodUid != null) && (localPendingMessageInfo2.resolvedPeriodIndex == i) && (localPendingMessageInfo2.resolvedPeriodTimeUs > l) && (localPendingMessageInfo2.resolvedPeriodTimeUs <= paramLong2))
    {
      sendMessageToTarget(localPendingMessageInfo2.message);
      if (!localPendingMessageInfo2.message.getDeleteAfterDelivery()) {
        break label425;
      }
      this.pendingMessages.remove(this.nextPendingMessageIndex);
      label388:
      if (this.nextPendingMessageIndex >= this.pendingMessages.size()) {
        break label438;
      }
    }
    label425:
    label438:
    for (localPendingMessageInfo1 = (PendingMessageInfo)this.pendingMessages.get(this.nextPendingMessageIndex);; localPendingMessageInfo1 = null)
    {
      localPendingMessageInfo2 = localPendingMessageInfo1;
      break label312;
      break;
      this.nextPendingMessageIndex += 1;
      break label388;
    }
  }
  
  private void maybeUpdateLoadingPeriod()
    throws IOException
  {
    this.queue.reevaluateBuffer(this.rendererPositionUs);
    MediaPeriodInfo localMediaPeriodInfo;
    if (this.queue.shouldLoadNextMediaPeriod())
    {
      localMediaPeriodInfo = this.queue.getNextMediaPeriodInfo(this.rendererPositionUs, this.playbackInfo);
      if (localMediaPeriodInfo != null) {
        break label51;
      }
      this.mediaSource.maybeThrowSourceInfoRefreshError();
    }
    for (;;)
    {
      return;
      label51:
      Object localObject = this.playbackInfo.timeline.getPeriod(localMediaPeriodInfo.id.periodIndex, this.period, true).uid;
      this.queue.enqueueNextMediaPeriod(this.rendererCapabilities, 60000000L, this.trackSelector, this.loadControl.getAllocator(), this.mediaSource, localObject, localMediaPeriodInfo).prepare(this, localMediaPeriodInfo.startPositionUs);
      setIsLoading(true);
    }
  }
  
  private void prepareInternal(MediaSource paramMediaSource, boolean paramBoolean)
  {
    this.pendingPrepareCount += 1;
    resetInternal(true, paramBoolean, true);
    this.loadControl.onPrepared();
    this.mediaSource = paramMediaSource;
    setState(2);
    paramMediaSource.prepareSource(this.player, true, this);
    this.handler.sendEmptyMessage(2);
  }
  
  private void releaseInternal()
  {
    resetInternal(true, true, true);
    this.loadControl.onReleased();
    setState(1);
    this.internalPlaybackThread.quit();
    try
    {
      this.released = true;
      notifyAll();
      return;
    }
    finally {}
  }
  
  private boolean rendererWaitingForNextStream(Renderer paramRenderer)
  {
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getReadingPeriod();
    if ((localMediaPeriodHolder.next != null) && (localMediaPeriodHolder.next.prepared) && (paramRenderer.hasReadStreamToEnd())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void reselectTracksInternal()
    throws ExoPlaybackException
  {
    if (!this.queue.hasPlayingPeriod()) {}
    label41:
    label200:
    label232:
    label311:
    label317:
    label339:
    label395:
    label444:
    for (;;)
    {
      return;
      float f = this.mediaClock.getPlaybackParameters().speed;
      Object localObject1 = this.queue.getPlayingPeriod();
      Object localObject2 = this.queue.getReadingPeriod();
      int i = 1;
      MediaPeriodHolder localMediaPeriodHolder;
      boolean bool;
      boolean[] arrayOfBoolean;
      int j;
      Renderer localRenderer;
      int k;
      if ((localObject1 != null) && (((MediaPeriodHolder)localObject1).prepared)) {
        if (((MediaPeriodHolder)localObject1).selectTracks(f))
        {
          if (i == 0) {
            break label395;
          }
          localMediaPeriodHolder = this.queue.getPlayingPeriod();
          bool = this.queue.removeAfter(localMediaPeriodHolder);
          arrayOfBoolean = new boolean[this.renderers.length];
          long l = localMediaPeriodHolder.applyTrackSelection(this.playbackInfo.positionUs, bool, arrayOfBoolean);
          updateLoadControlTrackSelection(localMediaPeriodHolder.trackSelectorResult);
          if ((this.playbackInfo.playbackState != 4) && (l != this.playbackInfo.positionUs))
          {
            this.playbackInfo = this.playbackInfo.fromNewPosition(this.playbackInfo.periodId, l, this.playbackInfo.contentPositionUs);
            this.playbackInfoUpdate.setPositionDiscontinuity(4);
            resetRendererPosition(l);
          }
          j = 0;
          localObject2 = new boolean[this.renderers.length];
          i = 0;
          if (i >= this.renderers.length) {
            break label339;
          }
          localRenderer = this.renderers[i];
          if (localRenderer.getState() == 0) {
            break label311;
          }
          bool = true;
          localObject2[i] = bool;
          localObject1 = localMediaPeriodHolder.sampleStreams[i];
          k = j;
          if (localObject1 != null) {
            k = j + 1;
          }
          if (localObject2[i] != 0)
          {
            if (localObject1 == localRenderer.getStream()) {
              break label317;
            }
            disableRenderer(localRenderer);
          }
        }
      }
      for (;;)
      {
        i++;
        j = k;
        break label200;
        if (localObject1 == localObject2) {
          i = 0;
        }
        localObject1 = ((MediaPeriodHolder)localObject1).next;
        break label41;
        break;
        bool = false;
        break label232;
        if (arrayOfBoolean[i] != 0) {
          localRenderer.resetPosition(this.rendererPositionUs);
        }
      }
      this.playbackInfo = this.playbackInfo.copyWithTrackSelectorResult(localMediaPeriodHolder.trackSelectorResult);
      enableRenderers((boolean[])localObject2, j);
      for (;;)
      {
        if (this.playbackInfo.playbackState == 4) {
          break label444;
        }
        maybeContinueLoading();
        updatePlaybackPositions();
        this.handler.sendEmptyMessage(2);
        break;
        this.queue.removeAfter((MediaPeriodHolder)localObject1);
        if (((MediaPeriodHolder)localObject1).prepared)
        {
          ((MediaPeriodHolder)localObject1).applyTrackSelection(Math.max(((MediaPeriodHolder)localObject1).info.startPositionUs, ((MediaPeriodHolder)localObject1).toPeriodTime(this.rendererPositionUs)), false);
          updateLoadControlTrackSelection(((MediaPeriodHolder)localObject1).trackSelectorResult);
        }
      }
    }
  }
  
  private void resetInternal(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.handler.removeMessages(2);
    this.rebuffering = false;
    this.mediaClock.stop();
    this.rendererPositionUs = 60000000L;
    Object localObject1 = this.enabledRenderers;
    int i = localObject1.length;
    int j = 0;
    for (;;)
    {
      Renderer localRenderer;
      if (j < i) {
        localRenderer = localObject1[j];
      }
      try
      {
        disableRenderer(localRenderer);
        j++;
      }
      catch (ExoPlaybackException localExoPlaybackException)
      {
        for (;;)
        {
          Log.e("ExoPlayerImplInternal", "Stop failed.", localExoPlaybackException);
        }
        this.enabledRenderers = new Renderer[0];
        this.queue.clear();
        setIsLoading(false);
        if (paramBoolean2) {
          this.pendingInitialSeekPosition = null;
        }
        Object localObject2;
        if (paramBoolean3)
        {
          this.queue.setTimeline(null);
          localObject2 = this.pendingMessages.iterator();
          while (((Iterator)localObject2).hasNext()) {
            ((PendingMessageInfo)((Iterator)localObject2).next()).message.markAsProcessed(false);
          }
          this.pendingMessages.clear();
          this.nextPendingMessageIndex = 0;
        }
        MediaSource.MediaPeriodId localMediaPeriodId;
        long l1;
        long l2;
        if (paramBoolean3)
        {
          localObject2 = null;
          if (!paramBoolean3) {
            break label309;
          }
          localObject1 = null;
          if (!paramBoolean2) {
            break label321;
          }
          localMediaPeriodId = new MediaSource.MediaPeriodId(getFirstPeriodIndex());
          if (!paramBoolean2) {
            break label333;
          }
          l1 = -9223372036854775807L;
          if (!paramBoolean2) {
            break label345;
          }
          l2 = -9223372036854775807L;
          j = this.playbackInfo.playbackState;
          if (!paramBoolean3) {
            break label357;
          }
        }
        for (TrackSelectorResult localTrackSelectorResult = this.emptyTrackSelectorResult;; localTrackSelectorResult = this.playbackInfo.trackSelectorResult)
        {
          this.playbackInfo = new PlaybackInfo((Timeline)localObject2, localObject1, localMediaPeriodId, l1, l2, j, false, localTrackSelectorResult);
          if ((paramBoolean1) && (this.mediaSource != null))
          {
            this.mediaSource.releaseSource();
            this.mediaSource = null;
          }
          return;
          localObject2 = this.playbackInfo.timeline;
          break;
          localObject1 = this.playbackInfo.manifest;
          break label191;
          localMediaPeriodId = this.playbackInfo.periodId;
          break label208;
          l1 = this.playbackInfo.startPositionUs;
          break label217;
          l2 = this.playbackInfo.contentPositionUs;
          break label226;
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        label191:
        label208:
        label217:
        label226:
        label309:
        label321:
        label333:
        label345:
        label357:
        for (;;) {}
      }
    }
  }
  
  private void resetRendererPosition(long paramLong)
    throws ExoPlaybackException
  {
    if (!this.queue.hasPlayingPeriod()) {}
    for (paramLong = 60000000L + paramLong;; paramLong = this.queue.getPlayingPeriod().toRendererTime(paramLong))
    {
      this.rendererPositionUs = paramLong;
      this.mediaClock.resetPosition(this.rendererPositionUs);
      Renderer[] arrayOfRenderer = this.enabledRenderers;
      int i = arrayOfRenderer.length;
      for (int j = 0; j < i; j++) {
        arrayOfRenderer[j].resetPosition(this.rendererPositionUs);
      }
    }
  }
  
  private boolean resolvePendingMessagePosition(PendingMessageInfo paramPendingMessageInfo)
  {
    boolean bool = false;
    if (paramPendingMessageInfo.resolvedPeriodUid == null)
    {
      Pair localPair = resolveSeekPosition(new SeekPosition(paramPendingMessageInfo.message.getTimeline(), paramPendingMessageInfo.message.getWindowIndex(), C.msToUs(paramPendingMessageInfo.message.getPositionMs())), false);
      if (localPair == null) {
        return bool;
      }
      paramPendingMessageInfo.setResolvedPosition(((Integer)localPair.first).intValue(), ((Long)localPair.second).longValue(), this.playbackInfo.timeline.getPeriod(((Integer)localPair.first).intValue(), this.period, true).uid);
    }
    for (;;)
    {
      bool = true;
      break;
      int i = this.playbackInfo.timeline.getIndexOfPeriod(paramPendingMessageInfo.resolvedPeriodUid);
      if (i == -1) {
        break;
      }
      paramPendingMessageInfo.resolvedPeriodIndex = i;
    }
  }
  
  private void resolvePendingMessagePositions()
  {
    for (int i = this.pendingMessages.size() - 1; i >= 0; i--) {
      if (!resolvePendingMessagePosition((PendingMessageInfo)this.pendingMessages.get(i)))
      {
        ((PendingMessageInfo)this.pendingMessages.get(i)).message.markAsProcessed(false);
        this.pendingMessages.remove(i);
      }
    }
    Collections.sort(this.pendingMessages);
  }
  
  private Pair<Integer, Long> resolveSeekPosition(SeekPosition paramSeekPosition, boolean paramBoolean)
  {
    Timeline localTimeline = this.playbackInfo.timeline;
    Object localObject1 = paramSeekPosition.timeline;
    if (localTimeline == null) {
      paramSeekPosition = null;
    }
    for (;;)
    {
      return paramSeekPosition;
      Object localObject2 = localObject1;
      if (((Timeline)localObject1).isEmpty()) {
        localObject2 = localTimeline;
      }
      int i;
      try
      {
        localObject1 = ((Timeline)localObject2).getPeriodPosition(this.window, this.period, paramSeekPosition.windowIndex, paramSeekPosition.windowPositionUs);
        paramSeekPosition = (SeekPosition)localObject1;
        if (localTimeline == localObject2) {
          continue;
        }
        i = localTimeline.getIndexOfPeriod(((Timeline)localObject2).getPeriod(((Integer)((Pair)localObject1).first).intValue(), this.period, true).uid);
        if (i != -1) {
          paramSeekPosition = Pair.create(Integer.valueOf(i), ((Pair)localObject1).second);
        }
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        throw new IllegalSeekPositionException(localTimeline, paramSeekPosition.windowIndex, paramSeekPosition.windowPositionUs);
      }
      if (paramBoolean)
      {
        i = resolveSubsequentPeriod(((Integer)((Pair)localObject1).first).intValue(), localIndexOutOfBoundsException, localTimeline);
        if (i != -1)
        {
          paramSeekPosition = getPeriodPosition(localTimeline, localTimeline.getPeriod(i, this.period).windowIndex, -9223372036854775807L);
          continue;
        }
      }
      paramSeekPosition = null;
    }
  }
  
  private int resolveSubsequentPeriod(int paramInt, Timeline paramTimeline1, Timeline paramTimeline2)
  {
    int i = -1;
    int j = paramTimeline1.getPeriodCount();
    for (int k = 0;; k++)
    {
      if ((k < j) && (i == -1))
      {
        paramInt = paramTimeline1.getNextPeriodIndex(paramInt, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
        if (paramInt != -1) {}
      }
      else
      {
        return i;
      }
      i = paramTimeline2.getIndexOfPeriod(paramTimeline1.getPeriod(paramInt, this.period, true).uid);
    }
  }
  
  private void scheduleNextWork(long paramLong1, long paramLong2)
  {
    this.handler.removeMessages(2);
    this.handler.sendEmptyMessageAtTime(2, paramLong1 + paramLong2);
  }
  
  private void seekToInternal(SeekPosition paramSeekPosition)
    throws ExoPlaybackException
  {
    Timeline localTimeline = this.playbackInfo.timeline;
    this.playbackInfoUpdate.incrementPendingOperationAcks(1);
    Pair localPair = resolveSeekPosition(paramSeekPosition, true);
    MediaSource.MediaPeriodId localMediaPeriodId;
    long l1;
    long l2;
    int i;
    if (localPair == null)
    {
      localMediaPeriodId = new MediaSource.MediaPeriodId(getFirstPeriodIndex());
      l1 = -9223372036854775807L;
      l2 = -9223372036854775807L;
      i = 1;
    }
    long l4;
    for (;;)
    {
      try
      {
        if ((this.mediaSource == null) || (localTimeline == null))
        {
          this.pendingInitialSeekPosition = paramSeekPosition;
          return;
          i = ((Integer)localPair.first).intValue();
          l2 = ((Long)localPair.second).longValue();
          localMediaPeriodId = this.queue.resolveMediaPeriodIdForAds(i, l2);
          if (localMediaPeriodId.isAd())
          {
            l1 = 0L;
            i = 1;
            continue;
          }
          l1 = ((Long)localPair.second).longValue();
          if (paramSeekPosition.windowPositionUs == -9223372036854775807L)
          {
            i = 1;
            continue;
          }
          i = 0;
          continue;
        }
        if (l1 == -9223372036854775807L)
        {
          setState(4);
          resetInternal(false, true, false);
          continue;
        }
        l3 = l1;
      }
      finally
      {
        this.playbackInfo = this.playbackInfo.fromNewPosition(localMediaPeriodId, l1, l2);
        if (i != 0) {
          this.playbackInfoUpdate.setPositionDiscontinuity(2);
        }
      }
      long l3;
      l4 = l3;
      if (!localMediaPeriodId.equals(this.playbackInfo.periodId)) {
        break;
      }
      paramSeekPosition = this.queue.getPlayingPeriod();
      l5 = l3;
      if (paramSeekPosition != null)
      {
        l5 = l3;
        if (l3 != 0L) {
          l5 = paramSeekPosition.mediaPeriod.getAdjustedSeekPositionUs(l3, this.seekParameters);
        }
      }
      l4 = l5;
      if (C.usToMs(l5) != C.usToMs(this.playbackInfo.positionUs)) {
        break;
      }
      l5 = this.playbackInfo.positionUs;
      this.playbackInfo = this.playbackInfo.fromNewPosition(localMediaPeriodId, l5, l2);
      if (i != 0) {
        this.playbackInfoUpdate.setPositionDiscontinuity(2);
      }
    }
    long l5 = seekToPeriodPosition(localMediaPeriodId, l4);
    if (l1 != l5) {}
    for (int j = 1;; j = 0)
    {
      i |= j;
      l1 = l5;
      break;
    }
  }
  
  private long seekToPeriodPosition(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong)
    throws ExoPlaybackException
  {
    if (this.queue.getPlayingPeriod() != this.queue.getReadingPeriod()) {}
    for (boolean bool = true;; bool = false) {
      return seekToPeriodPosition(paramMediaPeriodId, paramLong, bool);
    }
  }
  
  private long seekToPeriodPosition(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    stopRenderers();
    this.rebuffering = false;
    setState(2);
    MediaPeriodHolder localMediaPeriodHolder1 = this.queue.getPlayingPeriod();
    for (MediaPeriodHolder localMediaPeriodHolder2 = localMediaPeriodHolder1;; localMediaPeriodHolder2 = this.queue.advancePlayingPeriod()) {
      if (localMediaPeriodHolder2 != null)
      {
        if (shouldKeepPeriodHolder(paramMediaPeriodId, paramLong, localMediaPeriodHolder2)) {
          this.queue.removeAfter(localMediaPeriodHolder2);
        }
      }
      else
      {
        if ((localMediaPeriodHolder1 == localMediaPeriodHolder2) && (!paramBoolean)) {
          break label121;
        }
        paramMediaPeriodId = this.enabledRenderers;
        int i = paramMediaPeriodId.length;
        for (int j = 0; j < i; j++) {
          disableRenderer(paramMediaPeriodId[j]);
        }
      }
    }
    this.enabledRenderers = new Renderer[0];
    localMediaPeriodHolder1 = null;
    label121:
    if (localMediaPeriodHolder2 != null)
    {
      updatePlayingPeriodRenderers(localMediaPeriodHolder1);
      long l = paramLong;
      if (localMediaPeriodHolder2.hasEnabledTracks)
      {
        l = localMediaPeriodHolder2.mediaPeriod.seekToUs(paramLong);
        localMediaPeriodHolder2.mediaPeriod.discardBuffer(l - this.backBufferDurationUs, this.retainBackBufferFromKeyframe);
      }
      resetRendererPosition(l);
      maybeContinueLoading();
      paramLong = l;
    }
    for (;;)
    {
      this.handler.sendEmptyMessage(2);
      return paramLong;
      this.queue.clear();
      resetRendererPosition(paramLong);
    }
  }
  
  private void sendMessageInternal(PlayerMessage paramPlayerMessage)
  {
    if (paramPlayerMessage.getPositionMs() == -9223372036854775807L) {
      sendMessageToTarget(paramPlayerMessage);
    }
    for (;;)
    {
      return;
      if (this.playbackInfo.timeline == null)
      {
        this.pendingMessages.add(new PendingMessageInfo(paramPlayerMessage));
      }
      else
      {
        PendingMessageInfo localPendingMessageInfo = new PendingMessageInfo(paramPlayerMessage);
        if (resolvePendingMessagePosition(localPendingMessageInfo))
        {
          this.pendingMessages.add(localPendingMessageInfo);
          Collections.sort(this.pendingMessages);
        }
        else
        {
          paramPlayerMessage.markAsProcessed(false);
        }
      }
    }
  }
  
  private void sendMessageToTarget(PlayerMessage paramPlayerMessage)
  {
    if (paramPlayerMessage.getHandler().getLooper() == this.handler.getLooper())
    {
      deliverMessage(paramPlayerMessage);
      if ((this.playbackInfo.playbackState == 3) || (this.playbackInfo.playbackState == 2)) {
        this.handler.sendEmptyMessage(2);
      }
    }
    for (;;)
    {
      return;
      this.handler.obtainMessage(15, paramPlayerMessage).sendToTarget();
    }
  }
  
  private void sendMessageToTargetThread(final PlayerMessage paramPlayerMessage)
  {
    paramPlayerMessage.getHandler().post(new Runnable()
    {
      public void run()
      {
        ExoPlayerImplInternal.this.deliverMessage(paramPlayerMessage);
      }
    });
  }
  
  private void setIsLoading(boolean paramBoolean)
  {
    if (this.playbackInfo.isLoading != paramBoolean) {
      this.playbackInfo = this.playbackInfo.copyWithIsLoading(paramBoolean);
    }
  }
  
  private void setPlayWhenReadyInternal(boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.rebuffering = false;
    this.playWhenReady = paramBoolean;
    if (!paramBoolean)
    {
      stopRenderers();
      updatePlaybackPositions();
    }
    for (;;)
    {
      return;
      if (this.playbackInfo.playbackState == 3)
      {
        startRenderers();
        this.handler.sendEmptyMessage(2);
      }
      else if (this.playbackInfo.playbackState == 2)
      {
        this.handler.sendEmptyMessage(2);
      }
    }
  }
  
  private void setPlaybackParametersInternal(PlaybackParameters paramPlaybackParameters)
  {
    this.mediaClock.setPlaybackParameters(paramPlaybackParameters);
  }
  
  private void setRepeatModeInternal(int paramInt)
    throws ExoPlaybackException
  {
    this.repeatMode = paramInt;
    this.queue.setRepeatMode(paramInt);
    validateExistingPeriodHolders();
  }
  
  private void setSeekParametersInternal(SeekParameters paramSeekParameters)
  {
    this.seekParameters = paramSeekParameters;
  }
  
  private void setShuffleModeEnabledInternal(boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.shuffleModeEnabled = paramBoolean;
    this.queue.setShuffleModeEnabled(paramBoolean);
    validateExistingPeriodHolders();
  }
  
  private void setState(int paramInt)
  {
    if (this.playbackInfo.playbackState != paramInt) {
      this.playbackInfo = this.playbackInfo.copyWithPlaybackState(paramInt);
    }
  }
  
  private boolean shouldKeepPeriodHolder(MediaSource.MediaPeriodId paramMediaPeriodId, long paramLong, MediaPeriodHolder paramMediaPeriodHolder)
  {
    if ((paramMediaPeriodId.equals(paramMediaPeriodHolder.info.id)) && (paramMediaPeriodHolder.prepared))
    {
      this.playbackInfo.timeline.getPeriod(paramMediaPeriodHolder.info.id.periodIndex, this.period);
      int i = this.period.getAdGroupIndexAfterPositionUs(paramLong);
      if ((i != -1) && (this.period.getAdGroupTimeUs(i) != paramMediaPeriodHolder.info.endPositionUs)) {}
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean shouldTransitionToReadyState(boolean paramBoolean)
  {
    boolean bool1 = false;
    boolean bool2;
    if (this.enabledRenderers.length == 0) {
      bool2 = isTimelineReady();
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (paramBoolean)
      {
        if (this.playbackInfo.isLoading) {
          break;
        }
        bool2 = true;
      }
    }
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getLoadingPeriod();
    if (!localMediaPeriodHolder.info.isFinal) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      long l = localMediaPeriodHolder.getBufferedPositionUs(paramBoolean);
      if (l != Long.MIN_VALUE)
      {
        bool2 = bool1;
        if (!this.loadControl.shouldStartPlayback(l - localMediaPeriodHolder.toPeriodTime(this.rendererPositionUs), this.mediaClock.getPlaybackParameters().speed, this.rebuffering)) {
          break;
        }
      }
      bool2 = true;
      break;
    }
  }
  
  private void startRenderers()
    throws ExoPlaybackException
  {
    int i = 0;
    this.rebuffering = false;
    this.mediaClock.start();
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int j = arrayOfRenderer.length;
    while (i < j)
    {
      arrayOfRenderer[i].start();
      i++;
    }
  }
  
  private void stopInternal(boolean paramBoolean1, boolean paramBoolean2)
  {
    resetInternal(true, paramBoolean1, paramBoolean1);
    PlaybackInfoUpdate localPlaybackInfoUpdate = this.playbackInfoUpdate;
    int i = this.pendingPrepareCount;
    if (paramBoolean2) {}
    for (int j = 1;; j = 0)
    {
      localPlaybackInfoUpdate.incrementPendingOperationAcks(j + i);
      this.pendingPrepareCount = 0;
      this.loadControl.onStopped();
      setState(1);
      return;
    }
  }
  
  private void stopRenderers()
    throws ExoPlaybackException
  {
    this.mediaClock.stop();
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int i = arrayOfRenderer.length;
    for (int j = 0; j < i; j++) {
      ensureStopped(arrayOfRenderer[j]);
    }
  }
  
  private void updateLoadControlTrackSelection(TrackSelectorResult paramTrackSelectorResult)
  {
    this.loadControl.onTracksSelected(this.renderers, paramTrackSelectorResult.groups, paramTrackSelectorResult.selections);
  }
  
  private MediaPeriodHolder updatePeriodInfo(MediaPeriodHolder paramMediaPeriodHolder, int paramInt)
  {
    for (;;)
    {
      paramMediaPeriodHolder.info = this.queue.getUpdatedMediaPeriodInfo(paramMediaPeriodHolder.info, paramInt);
      if ((paramMediaPeriodHolder.info.isLastInTimelinePeriod) || (paramMediaPeriodHolder.next == null)) {
        return paramMediaPeriodHolder;
      }
      paramMediaPeriodHolder = paramMediaPeriodHolder.next;
    }
  }
  
  private void updatePeriods()
    throws ExoPlaybackException, IOException
  {
    if (this.mediaSource == null) {}
    label58:
    Object localObject2;
    label86:
    label217:
    label219:
    label224:
    do
    {
      for (;;)
      {
        return;
        if (this.playbackInfo.timeline == null)
        {
          this.mediaSource.maybeThrowSourceInfoRefreshError();
        }
        else
        {
          maybeUpdateLoadingPeriod();
          localObject1 = this.queue.getLoadingPeriod();
          if ((localObject1 == null) || (((MediaPeriodHolder)localObject1).isFullyBuffered()))
          {
            setIsLoading(false);
            if (!this.queue.hasPlayingPeriod()) {
              break label217;
            }
            localObject1 = this.queue.getPlayingPeriod();
            localObject2 = this.queue.getReadingPeriod();
            i = 0;
            if ((!this.playWhenReady) || (localObject1 == localObject2) || (this.rendererPositionUs < ((MediaPeriodHolder)localObject1).next.rendererPositionOffsetUs)) {
              break label224;
            }
            if (i != 0) {
              maybeNotifyPlaybackInfoChanged();
            }
            if (!((MediaPeriodHolder)localObject1).info.isLastInTimelinePeriod) {
              break label219;
            }
          }
          for (i = 0;; i = 3)
          {
            localObject3 = this.queue.advancePlayingPeriod();
            updatePlayingPeriodRenderers((MediaPeriodHolder)localObject1);
            this.playbackInfo = this.playbackInfo.fromNewPosition(((MediaPeriodHolder)localObject3).info.id, ((MediaPeriodHolder)localObject3).info.startPositionUs, ((MediaPeriodHolder)localObject3).info.contentPositionUs);
            this.playbackInfoUpdate.setPositionDiscontinuity(i);
            updatePlaybackPositions();
            i = 1;
            localObject1 = localObject3;
            break label86;
            if (this.playbackInfo.isLoading) {
              break label58;
            }
            maybeContinueLoading();
            break label58;
            break;
          }
          if (!((MediaPeriodHolder)localObject2).info.isFinal) {
            break;
          }
          for (i = 0; i < this.renderers.length; i++)
          {
            localObject3 = this.renderers[i];
            localObject1 = localObject2.sampleStreams[i];
            if ((localObject1 != null) && (((Renderer)localObject3).getStream() == localObject1) && (((Renderer)localObject3).hasReadStreamToEnd())) {
              ((Renderer)localObject3).setCurrentStreamFinal();
            }
          }
        }
      }
    } while ((((MediaPeriodHolder)localObject2).next == null) || (!((MediaPeriodHolder)localObject2).next.prepared));
    for (int i = 0;; i++)
    {
      if (i >= this.renderers.length) {
        break label372;
      }
      localObject3 = this.renderers[i];
      localObject1 = localObject2.sampleStreams[i];
      if ((((Renderer)localObject3).getStream() != localObject1) || ((localObject1 != null) && (!((Renderer)localObject3).hasReadStreamToEnd()))) {
        break;
      }
    }
    label372:
    Object localObject1 = ((MediaPeriodHolder)localObject2).trackSelectorResult;
    Object localObject3 = this.queue.advanceReadingPeriod();
    TrackSelectorResult localTrackSelectorResult = ((MediaPeriodHolder)localObject3).trackSelectorResult;
    label412:
    int j;
    label415:
    Renderer localRenderer;
    if (((MediaPeriodHolder)localObject3).mediaPeriod.readDiscontinuity() != -9223372036854775807L)
    {
      i = 1;
      j = 0;
      if (j < this.renderers.length)
      {
        localRenderer = this.renderers[j];
        if (localObject1.renderersEnabled[j] != 0) {
          break label455;
        }
      }
    }
    for (;;)
    {
      j++;
      break label415;
      break;
      i = 0;
      break label412;
      label455:
      if (i != 0)
      {
        localRenderer.setCurrentStreamFinal();
      }
      else if (!localRenderer.isCurrentStreamFinal())
      {
        TrackSelection localTrackSelection = localTrackSelectorResult.selections.get(j);
        int k = localTrackSelectorResult.renderersEnabled[j];
        if (this.rendererCapabilities[j].getTrackType() == 5) {}
        for (int m = 1;; m = 0)
        {
          RendererConfiguration localRendererConfiguration = localObject1.rendererConfigurations[j];
          localObject2 = localTrackSelectorResult.rendererConfigurations[j];
          if ((k == 0) || (!((RendererConfiguration)localObject2).equals(localRendererConfiguration)) || (m != 0)) {
            break label591;
          }
          localRenderer.replaceStream(getFormats(localTrackSelection), localObject3.sampleStreams[j], ((MediaPeriodHolder)localObject3).getRendererOffset());
          break;
        }
        label591:
        localRenderer.setCurrentStreamFinal();
      }
    }
  }
  
  private void updatePlaybackPositions()
    throws ExoPlaybackException
  {
    if (!this.queue.hasPlayingPeriod()) {
      return;
    }
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getPlayingPeriod();
    long l = localMediaPeriodHolder.mediaPeriod.readDiscontinuity();
    label88:
    PlaybackInfo localPlaybackInfo;
    if (l != -9223372036854775807L)
    {
      resetRendererPosition(l);
      if (l != this.playbackInfo.positionUs)
      {
        this.playbackInfo = this.playbackInfo.fromNewPosition(this.playbackInfo.periodId, l, this.playbackInfo.contentPositionUs);
        this.playbackInfoUpdate.setPositionDiscontinuity(4);
      }
      localPlaybackInfo = this.playbackInfo;
      if (this.enabledRenderers.length != 0) {
        break label162;
      }
    }
    label162:
    for (l = localMediaPeriodHolder.info.durationUs;; l = localMediaPeriodHolder.getBufferedPositionUs(true))
    {
      localPlaybackInfo.bufferedPositionUs = l;
      break;
      this.rendererPositionUs = this.mediaClock.syncAndGetPositionUs();
      l = localMediaPeriodHolder.toPeriodTime(this.rendererPositionUs);
      maybeTriggerPendingMessages(this.playbackInfo.positionUs, l);
      this.playbackInfo.positionUs = l;
      break label88;
    }
  }
  
  private void updatePlayingPeriodRenderers(MediaPeriodHolder paramMediaPeriodHolder)
    throws ExoPlaybackException
  {
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getPlayingPeriod();
    if ((localMediaPeriodHolder == null) || (paramMediaPeriodHolder == localMediaPeriodHolder)) {}
    for (;;)
    {
      return;
      int i = 0;
      boolean[] arrayOfBoolean = new boolean[this.renderers.length];
      int j = 0;
      if (j < this.renderers.length)
      {
        Renderer localRenderer = this.renderers[j];
        if (localRenderer.getState() != 0) {}
        for (int k = 1;; k = 0)
        {
          arrayOfBoolean[j] = k;
          int m = i;
          if (localMediaPeriodHolder.trackSelectorResult.renderersEnabled[j] != 0) {
            m = i + 1;
          }
          if ((arrayOfBoolean[j] != 0) && ((localMediaPeriodHolder.trackSelectorResult.renderersEnabled[j] == 0) || ((localRenderer.isCurrentStreamFinal()) && (localRenderer.getStream() == paramMediaPeriodHolder.sampleStreams[j])))) {
            disableRenderer(localRenderer);
          }
          j++;
          i = m;
          break;
        }
      }
      this.playbackInfo = this.playbackInfo.copyWithTrackSelectorResult(localMediaPeriodHolder.trackSelectorResult);
      enableRenderers(arrayOfBoolean, i);
    }
  }
  
  private void updateTrackSelectionPlaybackSpeed(float paramFloat)
  {
    for (MediaPeriodHolder localMediaPeriodHolder = this.queue.getFrontPeriod(); localMediaPeriodHolder != null; localMediaPeriodHolder = localMediaPeriodHolder.next) {
      if (localMediaPeriodHolder.trackSelectorResult != null) {
        for (TrackSelection localTrackSelection : localMediaPeriodHolder.trackSelectorResult.selections.getAll()) {
          if (localTrackSelection != null) {
            localTrackSelection.onPlaybackSpeed(paramFloat);
          }
        }
      }
    }
  }
  
  private void validateExistingPeriodHolders()
    throws ExoPlaybackException
  {
    MediaPeriodHolder localMediaPeriodHolder = this.queue.getFrontPeriod();
    Object localObject = localMediaPeriodHolder;
    if (localMediaPeriodHolder == null) {}
    for (;;)
    {
      return;
      int i;
      do
      {
        localObject = ((MediaPeriodHolder)localObject).next;
        i = this.playbackInfo.timeline.getNextPeriodIndex(((MediaPeriodHolder)localObject).info.id.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
        while ((((MediaPeriodHolder)localObject).next != null) && (!((MediaPeriodHolder)localObject).info.isLastInTimelinePeriod)) {
          localObject = ((MediaPeriodHolder)localObject).next;
        }
      } while ((i != -1) && (((MediaPeriodHolder)localObject).next != null) && (((MediaPeriodHolder)localObject).next.info.id.periodIndex == i));
      boolean bool = this.queue.removeAfter((MediaPeriodHolder)localObject);
      ((MediaPeriodHolder)localObject).info = this.queue.getUpdatedMediaPeriodInfo(((MediaPeriodHolder)localObject).info);
      if ((bool) && (this.queue.hasPlayingPeriod()))
      {
        localObject = this.queue.getPlayingPeriod().info.id;
        long l = seekToPeriodPosition((MediaSource.MediaPeriodId)localObject, this.playbackInfo.positionUs, true);
        if (l != this.playbackInfo.positionUs)
        {
          this.playbackInfo = this.playbackInfo.fromNewPosition((MediaSource.MediaPeriodId)localObject, l, this.playbackInfo.contentPositionUs);
          this.playbackInfoUpdate.setPositionDiscontinuity(4);
        }
      }
    }
  }
  
  public Looper getPlaybackLooper()
  {
    return this.internalPlaybackThread.getLooper();
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    try
    {
      switch (paramMessage.what)
      {
      default: 
        bool = false;
      }
    }
    catch (ExoPlaybackException paramMessage)
    {
      for (;;)
      {
        MediaSource localMediaSource;
        Log.e("ExoPlayerImplInternal", "Renderer error.", paramMessage);
        stopInternal(false, false);
        this.eventHandler.obtainMessage(2, paramMessage).sendToTarget();
        maybeNotifyPlaybackInfoChanged();
        continue;
        bool = false;
        continue;
        setRepeatModeInternal(paramMessage.arg1);
      }
    }
    catch (IOException paramMessage)
    {
      for (;;)
      {
        Log.e("ExoPlayerImplInternal", "Source error.", paramMessage);
        stopInternal(false, false);
        this.eventHandler.obtainMessage(2, ExoPlaybackException.createForSource(paramMessage)).sendToTarget();
        maybeNotifyPlaybackInfoChanged();
        continue;
        if (paramMessage.arg1 == 0) {
          break;
        }
        bool = true;
        setShuffleModeEnabledInternal(bool);
      }
    }
    catch (RuntimeException paramMessage)
    {
      for (;;)
      {
        label105:
        label178:
        Log.e("ExoPlayerImplInternal", "Internal runtime error.", paramMessage);
        stopInternal(false, false);
        this.eventHandler.obtainMessage(2, ExoPlaybackException.createForUnexpected(paramMessage)).sendToTarget();
        maybeNotifyPlaybackInfoChanged();
        continue;
        boolean bool = false;
        continue;
        doSomeWork();
        continue;
        seekToInternal((SeekPosition)paramMessage.obj);
        continue;
        setPlaybackParametersInternal((PlaybackParameters)paramMessage.obj);
        continue;
        setSeekParametersInternal((SeekParameters)paramMessage.obj);
        continue;
        if (paramMessage.arg1 != 0) {}
        for (bool = true;; bool = false)
        {
          stopInternal(bool, true);
          break;
        }
        handlePeriodPrepared((MediaPeriod)paramMessage.obj);
        continue;
        handleSourceInfoRefreshed((MediaSourceRefreshInfo)paramMessage.obj);
        continue;
        handleContinueLoadingRequested((MediaPeriod)paramMessage.obj);
        continue;
        reselectTracksInternal();
        continue;
        sendMessageInternal((PlayerMessage)paramMessage.obj);
        continue;
        sendMessageToTargetThread((PlayerMessage)paramMessage.obj);
        continue;
        releaseInternal();
        bool = true;
      }
    }
    return bool;
    localMediaSource = (MediaSource)paramMessage.obj;
    if (paramMessage.arg1 != 0)
    {
      bool = true;
      prepareInternal(localMediaSource, bool);
    }
    for (;;)
    {
      maybeNotifyPlaybackInfoChanged();
      bool = true;
      break;
      bool = false;
      break label105;
      if (paramMessage.arg1 == 0) {
        break label178;
      }
      bool = true;
      setPlayWhenReadyInternal(bool);
    }
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    this.handler.obtainMessage(10, paramMediaPeriod).sendToTarget();
  }
  
  public void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters)
  {
    this.eventHandler.obtainMessage(1, paramPlaybackParameters).sendToTarget();
    updateTrackSelectionPlaybackSpeed(paramPlaybackParameters.speed);
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    this.handler.obtainMessage(9, paramMediaPeriod).sendToTarget();
  }
  
  public void onSourceInfoRefreshed(MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
  {
    this.handler.obtainMessage(8, new MediaSourceRefreshInfo(paramMediaSource, paramTimeline, paramObject)).sendToTarget();
  }
  
  public void onTrackSelectionsInvalidated()
  {
    this.handler.sendEmptyMessage(11);
  }
  
  public void prepare(MediaSource paramMediaSource, boolean paramBoolean)
  {
    HandlerWrapper localHandlerWrapper = this.handler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandlerWrapper.obtainMessage(0, i, 0, paramMediaSource).sendToTarget();
      return;
    }
  }
  
  /* Error */
  public void release()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 869	org/telegram/messenger/exoplayer2/ExoPlayerImplInternal:released	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: getfield 262	org/telegram/messenger/exoplayer2/ExoPlayerImplInternal:handler	Lorg/telegram/messenger/exoplayer2/util/HandlerWrapper;
    //   18: bipush 7
    //   20: invokeinterface 860 2 0
    //   25: pop
    //   26: iconst_0
    //   27: istore_2
    //   28: aload_0
    //   29: getfield 869	org/telegram/messenger/exoplayer2/ExoPlayerImplInternal:released	Z
    //   32: istore_1
    //   33: iload_1
    //   34: ifne +16 -> 50
    //   37: aload_0
    //   38: invokevirtual 1237	java/lang/Object:wait	()V
    //   41: goto -13 -> 28
    //   44: astore_3
    //   45: iconst_1
    //   46: istore_2
    //   47: goto -19 -> 28
    //   50: iload_2
    //   51: ifeq -40 -> 11
    //   54: invokestatic 1243	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   57: invokevirtual 1246	java/lang/Thread:interrupt	()V
    //   60: goto -49 -> 11
    //   63: astore_3
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_3
    //   67: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	ExoPlayerImplInternal
    //   6	28	1	bool	boolean
    //   27	24	2	i	int
    //   44	1	3	localInterruptedException	InterruptedException
    //   63	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   37	41	44	java/lang/InterruptedException
    //   2	7	63	finally
    //   14	26	63	finally
    //   28	33	63	finally
    //   37	41	63	finally
    //   54	60	63	finally
  }
  
  public void seekTo(Timeline paramTimeline, int paramInt, long paramLong)
  {
    this.handler.obtainMessage(3, new SeekPosition(paramTimeline, paramInt, paramLong)).sendToTarget();
  }
  
  /* Error */
  public void sendMessage(PlayerMessage paramPlayerMessage)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 869	org/telegram/messenger/exoplayer2/ExoPlayerImplInternal:released	Z
    //   6: ifeq +20 -> 26
    //   9: ldc 76
    //   11: ldc_w 1250
    //   14: invokestatic 1254	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   17: pop
    //   18: aload_1
    //   19: iconst_0
    //   20: invokevirtual 295	org/telegram/messenger/exoplayer2/PlayerMessage:markAsProcessed	(Z)V
    //   23: aload_0
    //   24: monitorexit
    //   25: return
    //   26: aload_0
    //   27: getfield 262	org/telegram/messenger/exoplayer2/ExoPlayerImplInternal:handler	Lorg/telegram/messenger/exoplayer2/util/HandlerWrapper;
    //   30: bipush 14
    //   32: aload_1
    //   33: invokeinterface 1036 3 0
    //   38: invokevirtual 306	android/os/Message:sendToTarget	()V
    //   41: goto -18 -> 23
    //   44: astore_1
    //   45: aload_0
    //   46: monitorexit
    //   47: aload_1
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	ExoPlayerImplInternal
    //   0	49	1	paramPlayerMessage	PlayerMessage
    // Exception table:
    //   from	to	target	type
    //   2	23	44	finally
    //   26	41	44	finally
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    HandlerWrapper localHandlerWrapper = this.handler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandlerWrapper.obtainMessage(1, i, 0).sendToTarget();
      return;
    }
  }
  
  public void setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    this.handler.obtainMessage(4, paramPlaybackParameters).sendToTarget();
  }
  
  public void setRepeatMode(int paramInt)
  {
    this.handler.obtainMessage(12, paramInt, 0).sendToTarget();
  }
  
  public void setSeekParameters(SeekParameters paramSeekParameters)
  {
    this.handler.obtainMessage(5, paramSeekParameters).sendToTarget();
  }
  
  public void setShuffleModeEnabled(boolean paramBoolean)
  {
    HandlerWrapper localHandlerWrapper = this.handler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandlerWrapper.obtainMessage(13, i, 0).sendToTarget();
      return;
    }
  }
  
  public void stop(boolean paramBoolean)
  {
    HandlerWrapper localHandlerWrapper = this.handler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandlerWrapper.obtainMessage(6, i, 0).sendToTarget();
      return;
    }
  }
  
  private static final class MediaSourceRefreshInfo
  {
    public final Object manifest;
    public final MediaSource source;
    public final Timeline timeline;
    
    public MediaSourceRefreshInfo(MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
    {
      this.source = paramMediaSource;
      this.timeline = paramTimeline;
      this.manifest = paramObject;
    }
  }
  
  private static final class PendingMessageInfo
    implements Comparable<PendingMessageInfo>
  {
    public final PlayerMessage message;
    public int resolvedPeriodIndex;
    public long resolvedPeriodTimeUs;
    public Object resolvedPeriodUid;
    
    public PendingMessageInfo(PlayerMessage paramPlayerMessage)
    {
      this.message = paramPlayerMessage;
    }
    
    public int compareTo(PendingMessageInfo paramPendingMessageInfo)
    {
      int i = 1;
      int j;
      int k;
      if (this.resolvedPeriodUid == null)
      {
        j = 1;
        if (paramPendingMessageInfo.resolvedPeriodUid != null) {
          break label45;
        }
        k = 1;
        label21:
        if (j == k) {
          break label51;
        }
        j = i;
        if (this.resolvedPeriodUid != null) {
          j = -1;
        }
      }
      for (;;)
      {
        return j;
        j = 0;
        break;
        label45:
        k = 0;
        break label21;
        label51:
        if (this.resolvedPeriodUid == null)
        {
          j = 0;
        }
        else
        {
          j = this.resolvedPeriodIndex - paramPendingMessageInfo.resolvedPeriodIndex;
          if (j == 0) {
            j = Util.compareLong(this.resolvedPeriodTimeUs, paramPendingMessageInfo.resolvedPeriodTimeUs);
          }
        }
      }
    }
    
    public void setResolvedPosition(int paramInt, long paramLong, Object paramObject)
    {
      this.resolvedPeriodIndex = paramInt;
      this.resolvedPeriodTimeUs = paramLong;
      this.resolvedPeriodUid = paramObject;
    }
  }
  
  private static final class PlaybackInfoUpdate
  {
    private int discontinuityReason;
    private PlaybackInfo lastPlaybackInfo;
    private int operationAcks;
    private boolean positionDiscontinuity;
    
    public boolean hasPendingUpdate(PlaybackInfo paramPlaybackInfo)
    {
      if ((paramPlaybackInfo != this.lastPlaybackInfo) || (this.operationAcks > 0) || (this.positionDiscontinuity)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void incrementPendingOperationAcks(int paramInt)
    {
      this.operationAcks += paramInt;
    }
    
    public void reset(PlaybackInfo paramPlaybackInfo)
    {
      this.lastPlaybackInfo = paramPlaybackInfo;
      this.operationAcks = 0;
      this.positionDiscontinuity = false;
    }
    
    public void setPositionDiscontinuity(int paramInt)
    {
      boolean bool = true;
      if ((this.positionDiscontinuity) && (this.discontinuityReason != 4)) {
        if (paramInt == 4) {
          Assertions.checkArgument(bool);
        }
      }
      for (;;)
      {
        return;
        bool = false;
        break;
        this.positionDiscontinuity = true;
        this.discontinuityReason = paramInt;
      }
    }
  }
  
  private static final class SeekPosition
  {
    public final Timeline timeline;
    public final int windowIndex;
    public final long windowPositionUs;
    
    public SeekPosition(Timeline paramTimeline, int paramInt, long paramLong)
    {
      this.timeline = paramTimeline;
      this.windowIndex = paramInt;
      this.windowPositionUs = paramLong;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ExoPlayerImplInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */