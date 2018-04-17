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
import org.telegram.messenger.exoplayer2.DefaultMediaClock.PlaybackParameterListener;
import org.telegram.messenger.exoplayer2.PlayerMessage.Sender;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.HandlerWrapper;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExoPlayerImplInternal implements Callback, PlaybackParameterListener, Sender, MediaPeriod.Callback, Listener, InvalidationListener {
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
    private final Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private final PlaybackInfoUpdate playbackInfoUpdate;
    private final ExoPlayer player;
    private final MediaPeriodQueue queue = new MediaPeriodQueue();
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
    private final Window window;

    private static final class MediaSourceRefreshInfo {
        public final Object manifest;
        public final MediaSource source;
        public final Timeline timeline;

        public MediaSourceRefreshInfo(MediaSource source, Timeline timeline, Object manifest) {
            this.source = source;
            this.timeline = timeline;
            this.manifest = manifest;
        }
    }

    private static final class PendingMessageInfo implements Comparable<PendingMessageInfo> {
        public final PlayerMessage message;
        public int resolvedPeriodIndex;
        public long resolvedPeriodTimeUs;
        public Object resolvedPeriodUid;

        public PendingMessageInfo(PlayerMessage message) {
            this.message = message;
        }

        public void setResolvedPosition(int periodIndex, long periodTimeUs, Object periodUid) {
            this.resolvedPeriodIndex = periodIndex;
            this.resolvedPeriodTimeUs = periodTimeUs;
            this.resolvedPeriodUid = periodUid;
        }

        public int compareTo(PendingMessageInfo other) {
            int i = 1;
            if ((this.resolvedPeriodUid == null ? 1 : 0) != (other.resolvedPeriodUid == null ? 1 : 0)) {
                if (this.resolvedPeriodUid != null) {
                    i = -1;
                }
                return i;
            } else if (this.resolvedPeriodUid == null) {
                return 0;
            } else {
                int comparePeriodIndex = this.resolvedPeriodIndex - other.resolvedPeriodIndex;
                if (comparePeriodIndex != 0) {
                    return comparePeriodIndex;
                }
                return Util.compareLong(this.resolvedPeriodTimeUs, other.resolvedPeriodTimeUs);
            }
        }
    }

    private static final class PlaybackInfoUpdate {
        private int discontinuityReason;
        private PlaybackInfo lastPlaybackInfo;
        private int operationAcks;
        private boolean positionDiscontinuity;

        private PlaybackInfoUpdate() {
        }

        public boolean hasPendingUpdate(PlaybackInfo playbackInfo) {
            if (playbackInfo == this.lastPlaybackInfo && this.operationAcks <= 0) {
                if (!this.positionDiscontinuity) {
                    return false;
                }
            }
            return true;
        }

        public void reset(PlaybackInfo playbackInfo) {
            this.lastPlaybackInfo = playbackInfo;
            this.operationAcks = 0;
            this.positionDiscontinuity = false;
        }

        public void incrementPendingOperationAcks(int operationAcks) {
            this.operationAcks += operationAcks;
        }

        public void setPositionDiscontinuity(int discontinuityReason) {
            boolean z = true;
            if (!this.positionDiscontinuity || this.discontinuityReason == 4) {
                this.positionDiscontinuity = true;
                this.discontinuityReason = discontinuityReason;
                return;
            }
            if (discontinuityReason != 4) {
                z = false;
            }
            Assertions.checkArgument(z);
        }
    }

    private static final class SeekPosition {
        public final Timeline timeline;
        public final int windowIndex;
        public final long windowPositionUs;

        public SeekPosition(Timeline timeline, int windowIndex, long windowPositionUs) {
            this.timeline = timeline;
            this.windowIndex = windowIndex;
            this.windowPositionUs = windowPositionUs;
        }
    }

    public ExoPlayerImplInternal(Renderer[] renderers, TrackSelector trackSelector, TrackSelectorResult emptyTrackSelectorResult, LoadControl loadControl, boolean playWhenReady, int repeatMode, boolean shuffleModeEnabled, Handler eventHandler, ExoPlayer player, Clock clock) {
        this.renderers = renderers;
        this.trackSelector = trackSelector;
        this.emptyTrackSelectorResult = emptyTrackSelectorResult;
        this.loadControl = loadControl;
        this.playWhenReady = playWhenReady;
        this.repeatMode = repeatMode;
        this.shuffleModeEnabled = shuffleModeEnabled;
        this.eventHandler = eventHandler;
        this.player = player;
        this.clock = clock;
        this.backBufferDurationUs = loadControl.getBackBufferDurationUs();
        this.retainBackBufferFromKeyframe = loadControl.retainBackBufferFromKeyframe();
        this.seekParameters = SeekParameters.DEFAULT;
        this.playbackInfo = new PlaybackInfo(null, C0539C.TIME_UNSET, emptyTrackSelectorResult);
        this.playbackInfoUpdate = new PlaybackInfoUpdate();
        this.rendererCapabilities = new RendererCapabilities[renderers.length];
        for (int i = 0; i < renderers.length; i++) {
            renderers[i].setIndex(i);
            this.rendererCapabilities[i] = renderers[i].getCapabilities();
        }
        this.mediaClock = new DefaultMediaClock(this, clock);
        this.pendingMessages = new ArrayList();
        this.enabledRenderers = new Renderer[0];
        this.window = new Window();
        this.period = new Period();
        trackSelector.init(this);
        this.internalPlaybackThread = new HandlerThread("ExoPlayerImplInternal:Handler", -16);
        this.internalPlaybackThread.start();
        this.handler = clock.createHandler(this.internalPlaybackThread.getLooper(), this);
    }

    public void prepare(MediaSource mediaSource, boolean resetPosition) {
        this.handler.obtainMessage(0, resetPosition, 0, mediaSource).sendToTarget();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.handler.obtainMessage(1, playWhenReady, 0).sendToTarget();
    }

    public void setRepeatMode(int repeatMode) {
        this.handler.obtainMessage(12, repeatMode, 0).sendToTarget();
    }

    public void setShuffleModeEnabled(boolean shuffleModeEnabled) {
        this.handler.obtainMessage(13, shuffleModeEnabled, 0).sendToTarget();
    }

    public void seekTo(Timeline timeline, int windowIndex, long positionUs) {
        this.handler.obtainMessage(3, new SeekPosition(timeline, windowIndex, positionUs)).sendToTarget();
    }

    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        this.handler.obtainMessage(4, playbackParameters).sendToTarget();
    }

    public void setSeekParameters(SeekParameters seekParameters) {
        this.handler.obtainMessage(5, seekParameters).sendToTarget();
    }

    public void stop(boolean reset) {
        this.handler.obtainMessage(6, reset, 0).sendToTarget();
    }

    public synchronized void sendMessage(PlayerMessage message) {
        if (this.released) {
            Log.w(TAG, "Ignoring messages sent after release.");
            message.markAsProcessed(false);
            return;
        }
        this.handler.obtainMessage(14, message).sendToTarget();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void release() {
        if (!this.released) {
            this.handler.sendEmptyMessage(7);
            boolean wasInterrupted = false;
            while (!this.released) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    wasInterrupted = true;
                }
            }
            if (wasInterrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Looper getPlaybackLooper() {
        return this.internalPlaybackThread.getLooper();
    }

    public void onSourceInfoRefreshed(MediaSource source, Timeline timeline, Object manifest) {
        this.handler.obtainMessage(8, new MediaSourceRefreshInfo(source, timeline, manifest)).sendToTarget();
    }

    public void onPrepared(MediaPeriod source) {
        this.handler.obtainMessage(9, source).sendToTarget();
    }

    public void onContinueLoadingRequested(MediaPeriod source) {
        this.handler.obtainMessage(10, source).sendToTarget();
    }

    public void onTrackSelectionsInvalidated() {
        this.handler.sendEmptyMessage(11);
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        this.eventHandler.obtainMessage(1, playbackParameters).sendToTarget();
        updateTrackSelectionPlaybackSpeed(playbackParameters.speed);
    }

    public boolean handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case 0:
                    prepareInternal((MediaSource) msg.obj, msg.arg1 != 0);
                    break;
                case 1:
                    setPlayWhenReadyInternal(msg.arg1 != 0);
                    break;
                case 2:
                    doSomeWork();
                    break;
                case 3:
                    seekToInternal((SeekPosition) msg.obj);
                    break;
                case 4:
                    setPlaybackParametersInternal((PlaybackParameters) msg.obj);
                    break;
                case 5:
                    setSeekParametersInternal((SeekParameters) msg.obj);
                    break;
                case 6:
                    stopInternal(msg.arg1 != 0, true);
                    break;
                case 7:
                    releaseInternal();
                    return true;
                case 8:
                    handleSourceInfoRefreshed((MediaSourceRefreshInfo) msg.obj);
                    break;
                case 9:
                    handlePeriodPrepared((MediaPeriod) msg.obj);
                    break;
                case 10:
                    handleContinueLoadingRequested((MediaPeriod) msg.obj);
                    break;
                case 11:
                    reselectTracksInternal();
                    break;
                case 12:
                    setRepeatModeInternal(msg.arg1);
                    break;
                case 13:
                    setShuffleModeEnabledInternal(msg.arg1 != 0);
                    break;
                case 14:
                    sendMessageInternal((PlayerMessage) msg.obj);
                    break;
                case 15:
                    sendMessageToTargetThread((PlayerMessage) msg.obj);
                    break;
                default:
                    return false;
            }
            maybeNotifyPlaybackInfoChanged();
        } catch (ExoPlaybackException e) {
            Log.e(TAG, "Renderer error.", e);
            stopInternal(false, false);
            this.eventHandler.obtainMessage(2, e).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
        } catch (IOException e2) {
            Log.e(TAG, "Source error.", e2);
            stopInternal(false, false);
            this.eventHandler.obtainMessage(2, ExoPlaybackException.createForSource(e2)).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
        } catch (RuntimeException e3) {
            Log.e(TAG, "Internal runtime error.", e3);
            stopInternal(false, false);
            this.eventHandler.obtainMessage(2, ExoPlaybackException.createForUnexpected(e3)).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
        }
        return true;
    }

    private void setState(int state) {
        if (this.playbackInfo.playbackState != state) {
            this.playbackInfo = this.playbackInfo.copyWithPlaybackState(state);
        }
    }

    private void setIsLoading(boolean isLoading) {
        if (this.playbackInfo.isLoading != isLoading) {
            this.playbackInfo = this.playbackInfo.copyWithIsLoading(isLoading);
        }
    }

    private void maybeNotifyPlaybackInfoChanged() {
        if (this.playbackInfoUpdate.hasPendingUpdate(this.playbackInfo)) {
            this.eventHandler.obtainMessage(0, this.playbackInfoUpdate.operationAcks, this.playbackInfoUpdate.positionDiscontinuity ? this.playbackInfoUpdate.discontinuityReason : -1, this.playbackInfo).sendToTarget();
            this.playbackInfoUpdate.reset(this.playbackInfo);
        }
    }

    private void prepareInternal(MediaSource mediaSource, boolean resetPosition) {
        this.pendingPrepareCount++;
        resetInternal(true, resetPosition, true);
        this.loadControl.onPrepared();
        this.mediaSource = mediaSource;
        setState(2);
        mediaSource.prepareSource(this.player, true, this);
        this.handler.sendEmptyMessage(2);
    }

    private void setPlayWhenReadyInternal(boolean playWhenReady) throws ExoPlaybackException {
        this.rebuffering = false;
        this.playWhenReady = playWhenReady;
        if (!playWhenReady) {
            stopRenderers();
            updatePlaybackPositions();
        } else if (this.playbackInfo.playbackState == 3) {
            startRenderers();
            this.handler.sendEmptyMessage(2);
        } else if (this.playbackInfo.playbackState == 2) {
            this.handler.sendEmptyMessage(2);
        }
    }

    private void setRepeatModeInternal(int repeatMode) throws ExoPlaybackException {
        this.repeatMode = repeatMode;
        this.queue.setRepeatMode(repeatMode);
        validateExistingPeriodHolders();
    }

    private void setShuffleModeEnabledInternal(boolean shuffleModeEnabled) throws ExoPlaybackException {
        this.shuffleModeEnabled = shuffleModeEnabled;
        this.queue.setShuffleModeEnabled(shuffleModeEnabled);
        validateExistingPeriodHolders();
    }

    private void validateExistingPeriodHolders() throws ExoPlaybackException {
        MediaPeriodHolder lastValidPeriodHolder = this.queue.getFrontPeriod();
        if (lastValidPeriodHolder != null) {
            while (true) {
                int nextPeriodIndex = this.playbackInfo.timeline.getNextPeriodIndex(lastValidPeriodHolder.info.id.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
                while (lastValidPeriodHolder.next != null && !lastValidPeriodHolder.info.isLastInTimelinePeriod) {
                    lastValidPeriodHolder = lastValidPeriodHolder.next;
                }
                if (nextPeriodIndex == -1 || lastValidPeriodHolder.next == null) {
                    break;
                } else if (lastValidPeriodHolder.next.info.id.periodIndex != nextPeriodIndex) {
                    break;
                } else {
                    lastValidPeriodHolder = lastValidPeriodHolder.next;
                }
            }
            boolean readingPeriodRemoved = this.queue.removeAfter(lastValidPeriodHolder);
            lastValidPeriodHolder.info = this.queue.getUpdatedMediaPeriodInfo(lastValidPeriodHolder.info);
            if (readingPeriodRemoved && this.queue.hasPlayingPeriod()) {
                MediaPeriodId periodId = this.queue.getPlayingPeriod().info.id;
                long newPositionUs = seekToPeriodPosition(periodId, this.playbackInfo.positionUs, true);
                if (newPositionUs != this.playbackInfo.positionUs) {
                    this.playbackInfo = this.playbackInfo.fromNewPosition(periodId, newPositionUs, this.playbackInfo.contentPositionUs);
                    this.playbackInfoUpdate.setPositionDiscontinuity(4);
                }
            }
        }
    }

    private void startRenderers() throws ExoPlaybackException {
        int i = 0;
        this.rebuffering = false;
        this.mediaClock.start();
        Renderer[] rendererArr = this.enabledRenderers;
        int length = rendererArr.length;
        while (i < length) {
            rendererArr[i].start();
            i++;
        }
    }

    private void stopRenderers() throws ExoPlaybackException {
        this.mediaClock.stop();
        for (Renderer renderer : this.enabledRenderers) {
            ensureStopped(renderer);
        }
    }

    private void updatePlaybackPositions() throws ExoPlaybackException {
        if (this.queue.hasPlayingPeriod()) {
            long j;
            MediaPeriodHolder playingPeriodHolder = this.queue.getPlayingPeriod();
            long periodPositionUs = playingPeriodHolder.mediaPeriod.readDiscontinuity();
            if (periodPositionUs != C0539C.TIME_UNSET) {
                resetRendererPosition(periodPositionUs);
                if (periodPositionUs != this.playbackInfo.positionUs) {
                    this.playbackInfo = this.playbackInfo.fromNewPosition(this.playbackInfo.periodId, periodPositionUs, this.playbackInfo.contentPositionUs);
                    this.playbackInfoUpdate.setPositionDiscontinuity(4);
                }
            } else {
                this.rendererPositionUs = this.mediaClock.syncAndGetPositionUs();
                periodPositionUs = playingPeriodHolder.toPeriodTime(this.rendererPositionUs);
                maybeTriggerPendingMessages(this.playbackInfo.positionUs, periodPositionUs);
                this.playbackInfo.positionUs = periodPositionUs;
            }
            PlaybackInfo playbackInfo = this.playbackInfo;
            if (this.enabledRenderers.length == 0) {
                j = playingPeriodHolder.info.durationUs;
            } else {
                j = playingPeriodHolder.getBufferedPositionUs(true);
            }
            playbackInfo.bufferedPositionUs = j;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doSomeWork() throws ExoPlaybackException, IOException {
        long operationStartTimeMs = this.clock.uptimeMillis();
        updatePeriods();
        if (this.queue.hasPlayingPeriod()) {
            MediaPeriodHolder playingPeriodHolder = r0.queue.getPlayingPeriod();
            TraceUtil.beginSection("doSomeWork");
            updatePlaybackPositions();
            long rendererPositionElapsedRealtimeUs = SystemClock.elapsedRealtime() * 1000;
            playingPeriodHolder.mediaPeriod.discardBuffer(r0.playbackInfo.positionUs - r0.backBufferDurationUs, r0.retainBackBufferFromKeyframe);
            Renderer[] rendererArr = r0.enabledRenderers;
            int length = rendererArr.length;
            boolean renderersReadyOrEnded = true;
            boolean renderersEnded = true;
            int renderersEnded2 = 0;
            while (renderersEnded2 < length) {
                Renderer renderer = rendererArr[renderersEnded2];
                int i = length;
                renderer.render(r0.rendererPositionUs, rendererPositionElapsedRealtimeUs);
                boolean z = true;
                boolean z2 = renderersEnded && renderer.isEnded();
                renderersEnded = z2;
                if (!(renderer.isReady() || renderer.isEnded())) {
                    if (!rendererWaitingForNextStream(renderer)) {
                        z2 = false;
                        if (!z2) {
                            renderer.maybeThrowStreamError();
                        }
                        if (renderersReadyOrEnded || !z2) {
                            z = false;
                        }
                        renderersReadyOrEnded = z;
                        renderersEnded2++;
                        length = i;
                    }
                }
                z2 = true;
                if (z2) {
                    renderer.maybeThrowStreamError();
                }
                if (renderersReadyOrEnded) {
                }
                z = false;
                renderersReadyOrEnded = z;
                renderersEnded2++;
                length = i;
            }
            if (!renderersReadyOrEnded) {
                maybeThrowPeriodPrepareError();
            }
            long playingPeriodDurationUs = playingPeriodHolder.info.durationUs;
            if (renderersEnded && ((playingPeriodDurationUs == C0539C.TIME_UNSET || playingPeriodDurationUs <= r0.playbackInfo.positionUs) && playingPeriodHolder.info.isFinal)) {
                setState(4);
                stopRenderers();
            } else if (r0.playbackInfo.playbackState == 2 && shouldTransitionToReadyState(renderersReadyOrEnded)) {
                setState(3);
                if (r0.playWhenReady) {
                    startRenderers();
                }
            } else if (r0.playbackInfo.playbackState == 3) {
                if (r0.enabledRenderers.length == 0) {
                    if (isTimelineReady()) {
                    }
                }
                r0.rebuffering = r0.playWhenReady;
                setState(2);
                stopRenderers();
            }
            if (r0.playbackInfo.playbackState == 2) {
                for (Renderer renderer2 : r0.enabledRenderers) {
                    renderer2.maybeThrowStreamError();
                }
            }
            if ((r0.playWhenReady && r0.playbackInfo.playbackState == 3) || r0.playbackInfo.playbackState == 2) {
                scheduleNextWork(operationStartTimeMs, 10);
            } else if (r0.enabledRenderers.length == 0 || r0.playbackInfo.playbackState == 4) {
                r0.handler.removeMessages(2);
            } else {
                scheduleNextWork(operationStartTimeMs, 1000);
            }
            TraceUtil.endSection();
            return;
        }
        maybeThrowPeriodPrepareError();
        scheduleNextWork(operationStartTimeMs, 10);
    }

    private void scheduleNextWork(long thisOperationStartTimeMs, long intervalMs) {
        this.handler.removeMessages(2);
        this.handler.sendEmptyMessageAtTime(2, thisOperationStartTimeMs + intervalMs);
    }

    private void seekToInternal(SeekPosition seekPosition) throws ExoPlaybackException {
        boolean seekPositionAdjusted;
        MediaPeriodId periodId;
        long periodPositionUs;
        long contentPositionUs;
        Throwable th;
        SeekPosition seekPosition2 = seekPosition;
        Timeline timeline = this.playbackInfo.timeline;
        this.playbackInfoUpdate.incrementPendingOperationAcks(1);
        Pair<Integer, Long> resolvedSeekPosition = resolveSeekPosition(seekPosition2, true);
        if (resolvedSeekPosition == null) {
            seekPositionAdjusted = true;
            periodId = new MediaPeriodId(getFirstPeriodIndex());
            periodPositionUs = C0539C.TIME_UNSET;
            contentPositionUs = C0539C.TIME_UNSET;
        } else {
            int periodIndex = ((Integer) resolvedSeekPosition.first).intValue();
            long contentPositionUs2 = ((Long) resolvedSeekPosition.second).longValue();
            MediaPeriodId periodId2 = r1.queue.resolveMediaPeriodIdForAds(periodIndex, contentPositionUs2);
            if (periodId2.isAd()) {
                contentPositionUs = contentPositionUs2;
                periodPositionUs = 0;
                periodId = periodId2;
                seekPositionAdjusted = true;
            } else {
                contentPositionUs = contentPositionUs2;
                periodPositionUs = ((Long) resolvedSeekPosition.second).longValue();
                seekPositionAdjusted = seekPosition2.windowPositionUs == C0539C.TIME_UNSET;
                periodId = periodId2;
            }
        }
        int timeline2;
        Timeline timeline3;
        try {
            if (r1.mediaSource == null) {
                timeline2 = 2;
            } else if (timeline == null) {
                timeline3 = timeline;
                timeline2 = 2;
            } else if (periodPositionUs == C0539C.TIME_UNSET) {
                try {
                    setState(4);
                    resetInternal(false, true, false);
                    timeline3 = timeline;
                    timeline2 = 2;
                    r1.playbackInfo = r1.playbackInfo.fromNewPosition(periodId, periodPositionUs, contentPositionUs);
                    if (seekPositionAdjusted) {
                        r1.playbackInfoUpdate.setPositionDiscontinuity(timeline2);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    timeline3 = timeline;
                    timeline2 = 2;
                    r1.playbackInfo = r1.playbackInfo.fromNewPosition(periodId, periodPositionUs, contentPositionUs);
                    if (seekPositionAdjusted) {
                        r1.playbackInfoUpdate.setPositionDiscontinuity(timeline2);
                    }
                    throw th;
                }
            } else {
                long newPeriodPositionUs;
                long newPeriodPositionUs2 = periodPositionUs;
                if (periodId.equals(r1.playbackInfo.periodId)) {
                    MediaPeriodHolder playingPeriodHolder = r1.queue.getPlayingPeriod();
                    if (!(playingPeriodHolder == null || newPeriodPositionUs2 == 0)) {
                        newPeriodPositionUs2 = playingPeriodHolder.mediaPeriod.getAdjustedSeekPositionUs(newPeriodPositionUs2, r1.seekParameters);
                    }
                    if (C0539C.usToMs(newPeriodPositionUs2) == C0539C.usToMs(r1.playbackInfo.positionUs)) {
                        timeline2 = 2;
                        r1.playbackInfo = r1.playbackInfo.fromNewPosition(periodId, r1.playbackInfo.positionUs, contentPositionUs);
                        if (seekPositionAdjusted) {
                            r1.playbackInfoUpdate.setPositionDiscontinuity(timeline2);
                        }
                        return;
                    }
                    newPeriodPositionUs = newPeriodPositionUs2;
                    timeline2 = 2;
                } else {
                    timeline2 = 2;
                    newPeriodPositionUs = newPeriodPositionUs2;
                }
                try {
                    long newPeriodPositionUs3 = seekToPeriodPosition(periodId, newPeriodPositionUs);
                    seekPositionAdjusted |= periodPositionUs != newPeriodPositionUs3 ? 1 : 0;
                    periodPositionUs = newPeriodPositionUs3;
                    r1.playbackInfo = r1.playbackInfo.fromNewPosition(periodId, periodPositionUs, contentPositionUs);
                    if (seekPositionAdjusted) {
                        r1.playbackInfoUpdate.setPositionDiscontinuity(timeline2);
                    }
                } catch (Throwable th22) {
                    th = th22;
                    r1.playbackInfo = r1.playbackInfo.fromNewPosition(periodId, periodPositionUs, contentPositionUs);
                    if (seekPositionAdjusted) {
                        r1.playbackInfoUpdate.setPositionDiscontinuity(timeline2);
                    }
                    throw th;
                }
            }
            r1.pendingInitialSeekPosition = seekPosition2;
            r1.playbackInfo = r1.playbackInfo.fromNewPosition(periodId, periodPositionUs, contentPositionUs);
            if (seekPositionAdjusted) {
                r1.playbackInfoUpdate.setPositionDiscontinuity(timeline2);
            }
        } catch (Throwable th222) {
            timeline3 = timeline;
            timeline2 = 2;
            th = th222;
            r1.playbackInfo = r1.playbackInfo.fromNewPosition(periodId, periodPositionUs, contentPositionUs);
            if (seekPositionAdjusted) {
                r1.playbackInfoUpdate.setPositionDiscontinuity(timeline2);
            }
            throw th;
        }
    }

    private long seekToPeriodPosition(MediaPeriodId periodId, long periodPositionUs) throws ExoPlaybackException {
        return seekToPeriodPosition(periodId, periodPositionUs, this.queue.getPlayingPeriod() != this.queue.getReadingPeriod());
    }

    private long seekToPeriodPosition(MediaPeriodId periodId, long periodPositionUs, boolean forceDisableRenderers) throws ExoPlaybackException {
        stopRenderers();
        this.rebuffering = false;
        setState(2);
        MediaPeriodHolder oldPlayingPeriodHolder = this.queue.getPlayingPeriod();
        MediaPeriodHolder newPlayingPeriodHolder = oldPlayingPeriodHolder;
        while (newPlayingPeriodHolder != null) {
            if (shouldKeepPeriodHolder(periodId, periodPositionUs, newPlayingPeriodHolder)) {
                this.queue.removeAfter(newPlayingPeriodHolder);
                break;
            }
            newPlayingPeriodHolder = this.queue.advancePlayingPeriod();
        }
        if (oldPlayingPeriodHolder != newPlayingPeriodHolder || forceDisableRenderers) {
            for (Renderer renderer : this.enabledRenderers) {
                disableRenderer(renderer);
            }
            this.enabledRenderers = new Renderer[0];
            oldPlayingPeriodHolder = null;
        }
        if (newPlayingPeriodHolder != null) {
            updatePlayingPeriodRenderers(oldPlayingPeriodHolder);
            if (newPlayingPeriodHolder.hasEnabledTracks) {
                periodPositionUs = newPlayingPeriodHolder.mediaPeriod.seekToUs(periodPositionUs);
                newPlayingPeriodHolder.mediaPeriod.discardBuffer(periodPositionUs - this.backBufferDurationUs, this.retainBackBufferFromKeyframe);
            }
            resetRendererPosition(periodPositionUs);
            maybeContinueLoading();
        } else {
            this.queue.clear();
            resetRendererPosition(periodPositionUs);
        }
        this.handler.sendEmptyMessage(2);
        return periodPositionUs;
    }

    private boolean shouldKeepPeriodHolder(MediaPeriodId seekPeriodId, long positionUs, MediaPeriodHolder holder) {
        if (seekPeriodId.equals(holder.info.id) && holder.prepared) {
            this.playbackInfo.timeline.getPeriod(holder.info.id.periodIndex, this.period);
            int nextAdGroupIndex = this.period.getAdGroupIndexAfterPositionUs(positionUs);
            if (nextAdGroupIndex == -1 || this.period.getAdGroupTimeUs(nextAdGroupIndex) == holder.info.endPositionUs) {
                return true;
            }
        }
        return false;
    }

    private void resetRendererPosition(long periodPositionUs) throws ExoPlaybackException {
        long toRendererTime;
        if (this.queue.hasPlayingPeriod()) {
            toRendererTime = this.queue.getPlayingPeriod().toRendererTime(periodPositionUs);
        } else {
            toRendererTime = periodPositionUs + 60000000;
        }
        this.rendererPositionUs = toRendererTime;
        this.mediaClock.resetPosition(this.rendererPositionUs);
        for (Renderer renderer : this.enabledRenderers) {
            renderer.resetPosition(this.rendererPositionUs);
        }
    }

    private void setPlaybackParametersInternal(PlaybackParameters playbackParameters) {
        this.mediaClock.setPlaybackParameters(playbackParameters);
    }

    private void setSeekParametersInternal(SeekParameters seekParameters) {
        this.seekParameters = seekParameters;
    }

    private void stopInternal(boolean reset, boolean acknowledgeStop) {
        resetInternal(true, reset, reset);
        this.playbackInfoUpdate.incrementPendingOperationAcks(this.pendingPrepareCount + acknowledgeStop);
        this.pendingPrepareCount = 0;
        this.loadControl.onStopped();
        setState(1);
    }

    private void releaseInternal() {
        resetInternal(true, true, true);
        this.loadControl.onReleased();
        setState(1);
        this.internalPlaybackThread.quit();
        synchronized (this) {
            this.released = true;
            notifyAll();
        }
    }

    private int getFirstPeriodIndex() {
        Timeline timeline = this.playbackInfo.timeline;
        if (timeline != null) {
            if (!timeline.isEmpty()) {
                return timeline.getWindow(timeline.getFirstWindowIndex(this.shuffleModeEnabled), this.window).firstPeriodIndex;
            }
        }
        return 0;
    }

    private void resetInternal(boolean releaseMediaSource, boolean resetPosition, boolean resetState) {
        this.handler.removeMessages(2);
        this.rebuffering = false;
        this.mediaClock.stop();
        this.rendererPositionUs = 60000000;
        for (Renderer renderer : this.enabledRenderers) {
            try {
                disableRenderer(renderer);
            } catch (Exception e) {
                Log.e(TAG, "Stop failed.", e);
            }
        }
        r1.enabledRenderers = new Renderer[0];
        r1.queue.clear();
        setIsLoading(false);
        if (resetPosition) {
            r1.pendingInitialSeekPosition = null;
        }
        if (resetState) {
            r1.queue.setTimeline(null);
            Iterator it = r1.pendingMessages.iterator();
            while (it.hasNext()) {
                ((PendingMessageInfo) it.next()).message.markAsProcessed(false);
            }
            r1.pendingMessages.clear();
            r1.nextPendingMessageIndex = 0;
        }
        Timeline timeline = resetState ? null : r1.playbackInfo.timeline;
        Object obj = resetState ? null : r1.playbackInfo.manifest;
        MediaPeriodId mediaPeriodId = resetPosition ? new MediaPeriodId(getFirstPeriodIndex()) : r1.playbackInfo.periodId;
        long j = C0539C.TIME_UNSET;
        long j2 = resetPosition ? C0539C.TIME_UNSET : r1.playbackInfo.startPositionUs;
        if (!resetPosition) {
            j = r1.playbackInfo.contentPositionUs;
        }
        r1.playbackInfo = new PlaybackInfo(timeline, obj, mediaPeriodId, j2, j, r1.playbackInfo.playbackState, false, resetState ? r1.emptyTrackSelectorResult : r1.playbackInfo.trackSelectorResult);
        if (releaseMediaSource && r1.mediaSource != null) {
            r1.mediaSource.releaseSource();
            r1.mediaSource = null;
        }
    }

    private void sendMessageInternal(PlayerMessage message) {
        if (message.getPositionMs() == C0539C.TIME_UNSET) {
            sendMessageToTarget(message);
        } else if (this.playbackInfo.timeline == null) {
            this.pendingMessages.add(new PendingMessageInfo(message));
        } else {
            PendingMessageInfo pendingMessageInfo = new PendingMessageInfo(message);
            if (resolvePendingMessagePosition(pendingMessageInfo)) {
                this.pendingMessages.add(pendingMessageInfo);
                Collections.sort(this.pendingMessages);
                return;
            }
            message.markAsProcessed(false);
        }
    }

    private void sendMessageToTarget(PlayerMessage message) {
        if (message.getHandler().getLooper() == this.handler.getLooper()) {
            deliverMessage(message);
            if (this.playbackInfo.playbackState == 3 || this.playbackInfo.playbackState == 2) {
                this.handler.sendEmptyMessage(2);
                return;
            }
            return;
        }
        this.handler.obtainMessage(15, message).sendToTarget();
    }

    private void sendMessageToTargetThread(final PlayerMessage message) {
        message.getHandler().post(new Runnable() {
            public void run() {
                ExoPlayerImplInternal.this.deliverMessage(message);
            }
        });
    }

    private void deliverMessage(PlayerMessage message) {
        try {
            message.getTarget().handleMessage(message.getType(), message.getPayload());
        } catch (ExoPlaybackException e) {
            this.eventHandler.obtainMessage(2, e).sendToTarget();
        } catch (Throwable th) {
            message.markAsProcessed(true);
        }
        message.markAsProcessed(true);
    }

    private void resolvePendingMessagePositions() {
        for (int i = this.pendingMessages.size() - 1; i >= 0; i--) {
            if (!resolvePendingMessagePosition((PendingMessageInfo) this.pendingMessages.get(i))) {
                ((PendingMessageInfo) this.pendingMessages.get(i)).message.markAsProcessed(false);
                this.pendingMessages.remove(i);
            }
        }
        Collections.sort(this.pendingMessages);
    }

    private boolean resolvePendingMessagePosition(PendingMessageInfo pendingMessageInfo) {
        if (pendingMessageInfo.resolvedPeriodUid == null) {
            Pair<Integer, Long> periodPosition = resolveSeekPosition(new SeekPosition(pendingMessageInfo.message.getTimeline(), pendingMessageInfo.message.getWindowIndex(), C0539C.msToUs(pendingMessageInfo.message.getPositionMs())), false);
            if (periodPosition == null) {
                return false;
            }
            pendingMessageInfo.setResolvedPosition(((Integer) periodPosition.first).intValue(), ((Long) periodPosition.second).longValue(), this.playbackInfo.timeline.getPeriod(((Integer) periodPosition.first).intValue(), this.period, true).uid);
        } else {
            int index = this.playbackInfo.timeline.getIndexOfPeriod(pendingMessageInfo.resolvedPeriodUid);
            if (index == -1) {
                return false;
            }
            pendingMessageInfo.resolvedPeriodIndex = index;
        }
        return true;
    }

    private void maybeTriggerPendingMessages(long oldPeriodPositionUs, long newPeriodPositionUs) {
        if (!this.pendingMessages.isEmpty()) {
            if (!this.playbackInfo.periodId.isAd()) {
                if (this.playbackInfo.startPositionUs == oldPeriodPositionUs) {
                    oldPeriodPositionUs--;
                }
                int currentPeriodIndex = this.playbackInfo.periodId.periodIndex;
                PendingMessageInfo previousInfo = this.nextPendingMessageIndex > 0 ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex - 1) : null;
                while (previousInfo != null && (previousInfo.resolvedPeriodIndex > currentPeriodIndex || (previousInfo.resolvedPeriodIndex == currentPeriodIndex && previousInfo.resolvedPeriodTimeUs > oldPeriodPositionUs))) {
                    this.nextPendingMessageIndex--;
                    previousInfo = this.nextPendingMessageIndex > 0 ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex - 1) : null;
                }
                PendingMessageInfo nextInfo = this.nextPendingMessageIndex < this.pendingMessages.size() ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex) : null;
                while (nextInfo != null && nextInfo.resolvedPeriodUid != null && (nextInfo.resolvedPeriodIndex < currentPeriodIndex || (nextInfo.resolvedPeriodIndex == currentPeriodIndex && nextInfo.resolvedPeriodTimeUs <= oldPeriodPositionUs))) {
                    this.nextPendingMessageIndex++;
                    nextInfo = this.nextPendingMessageIndex < this.pendingMessages.size() ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex) : null;
                }
                while (nextInfo != null && nextInfo.resolvedPeriodUid != null && nextInfo.resolvedPeriodIndex == currentPeriodIndex && nextInfo.resolvedPeriodTimeUs > oldPeriodPositionUs && nextInfo.resolvedPeriodTimeUs <= newPeriodPositionUs) {
                    sendMessageToTarget(nextInfo.message);
                    if (nextInfo.message.getDeleteAfterDelivery()) {
                        this.pendingMessages.remove(this.nextPendingMessageIndex);
                    } else {
                        this.nextPendingMessageIndex++;
                    }
                    nextInfo = this.nextPendingMessageIndex < this.pendingMessages.size() ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex) : null;
                }
            }
        }
    }

    private void ensureStopped(Renderer renderer) throws ExoPlaybackException {
        if (renderer.getState() == 2) {
            renderer.stop();
        }
    }

    private void disableRenderer(Renderer renderer) throws ExoPlaybackException {
        this.mediaClock.onRendererDisabled(renderer);
        ensureStopped(renderer);
        renderer.disable();
    }

    private void reselectTracksInternal() throws ExoPlaybackException {
        if (this.queue.hasPlayingPeriod()) {
            float playbackSpeed = r0.mediaClock.getPlaybackParameters().speed;
            MediaPeriodHolder periodHolder = r0.queue.getPlayingPeriod();
            MediaPeriodHolder readingPeriodHolder = r0.queue.getReadingPeriod();
            MediaPeriodHolder periodHolder2 = periodHolder;
            boolean selectionsChangedForReadPeriod = true;
            while (periodHolder2 != null) {
                if (!periodHolder2.prepared) {
                    break;
                } else if (periodHolder2.selectTracks(playbackSpeed)) {
                    if (selectionsChangedForReadPeriod) {
                        MediaPeriodHolder playingPeriodHolder = r0.queue.getPlayingPeriod();
                        boolean[] streamResetFlags = new boolean[r0.renderers.length];
                        long periodPositionUs = playingPeriodHolder.applyTrackSelection(r0.playbackInfo.positionUs, r0.queue.removeAfter(playingPeriodHolder), streamResetFlags);
                        updateLoadControlTrackSelection(playingPeriodHolder.trackSelectorResult);
                        MediaPeriodHolder periodHolder3;
                        if (r0.playbackInfo.playbackState == 4 || periodPositionUs == r0.playbackInfo.positionUs) {
                            periodHolder3 = periodHolder2;
                        } else {
                            periodHolder3 = periodHolder2;
                            r0.playbackInfo = r0.playbackInfo.fromNewPosition(r0.playbackInfo.periodId, periodPositionUs, r0.playbackInfo.contentPositionUs);
                            r0.playbackInfoUpdate.setPositionDiscontinuity(4);
                            resetRendererPosition(periodPositionUs);
                        }
                        periodHolder2 = new boolean[r0.renderers.length];
                        int enabledRendererCount = 0;
                        for (int i = 0; i < r0.renderers.length; i++) {
                            Renderer renderer = r0.renderers[i];
                            periodHolder2[i] = renderer.getState() != 0;
                            SampleStream sampleStream = playingPeriodHolder.sampleStreams[i];
                            if (sampleStream != null) {
                                enabledRendererCount++;
                            }
                            if (periodHolder2[i]) {
                                if (sampleStream != renderer.getStream()) {
                                    disableRenderer(renderer);
                                } else if (streamResetFlags[i]) {
                                    renderer.resetPosition(r0.rendererPositionUs);
                                }
                            }
                        }
                        r0.playbackInfo = r0.playbackInfo.copyWithTrackSelectorResult(playingPeriodHolder.trackSelectorResult);
                        enableRenderers(periodHolder2, enabledRendererCount);
                    } else {
                        r0.queue.removeAfter(periodHolder2);
                        if (periodHolder2.prepared) {
                            periodHolder2.applyTrackSelection(Math.max(periodHolder2.info.startPositionUs, periodHolder2.toPeriodTime(r0.rendererPositionUs)), false);
                            updateLoadControlTrackSelection(periodHolder2.trackSelectorResult);
                        }
                    }
                    if (r0.playbackInfo.playbackState != 4) {
                        maybeContinueLoading();
                        updatePlaybackPositions();
                        r0.handler.sendEmptyMessage(2);
                    }
                    return;
                } else {
                    if (periodHolder2 == readingPeriodHolder) {
                        selectionsChangedForReadPeriod = false;
                    }
                    periodHolder2 = periodHolder2.next;
                }
            }
        }
    }

    private void updateLoadControlTrackSelection(TrackSelectorResult trackSelectorResult) {
        this.loadControl.onTracksSelected(this.renderers, trackSelectorResult.groups, trackSelectorResult.selections);
    }

    private void updateTrackSelectionPlaybackSpeed(float playbackSpeed) {
        for (MediaPeriodHolder periodHolder = this.queue.getFrontPeriod(); periodHolder != null; periodHolder = periodHolder.next) {
            if (periodHolder.trackSelectorResult != null) {
                for (TrackSelection trackSelection : periodHolder.trackSelectorResult.selections.getAll()) {
                    if (trackSelection != null) {
                        trackSelection.onPlaybackSpeed(playbackSpeed);
                    }
                }
            }
        }
    }

    private boolean shouldTransitionToReadyState(boolean renderersReadyOrEnded) {
        if (this.enabledRenderers.length == 0) {
            return isTimelineReady();
        }
        boolean z = false;
        if (!renderersReadyOrEnded) {
            return false;
        }
        if (!this.playbackInfo.isLoading) {
            return true;
        }
        MediaPeriodHolder loadingHolder = this.queue.getLoadingPeriod();
        long bufferedPositionUs = loadingHolder.getBufferedPositionUs(loadingHolder.info.isFinal ^ 1);
        if (bufferedPositionUs != Long.MIN_VALUE) {
            if (!this.loadControl.shouldStartPlayback(bufferedPositionUs - loadingHolder.toPeriodTime(this.rendererPositionUs), this.mediaClock.getPlaybackParameters().speed, this.rebuffering)) {
                return z;
            }
        }
        z = true;
        return z;
    }

    private boolean isTimelineReady() {
        MediaPeriodHolder playingPeriodHolder = this.queue.getPlayingPeriod();
        long playingPeriodDurationUs = playingPeriodHolder.info.durationUs;
        if (playingPeriodDurationUs != C0539C.TIME_UNSET && this.playbackInfo.positionUs >= playingPeriodDurationUs) {
            if (playingPeriodHolder.next != null) {
                if (!playingPeriodHolder.next.prepared) {
                    if (playingPeriodHolder.next.info.id.isAd()) {
                    }
                }
            }
            return false;
        }
        return true;
    }

    private void maybeThrowPeriodPrepareError() throws IOException {
        MediaPeriodHolder loadingPeriodHolder = this.queue.getLoadingPeriod();
        MediaPeriodHolder readingPeriodHolder = this.queue.getReadingPeriod();
        if (!(loadingPeriodHolder == null || loadingPeriodHolder.prepared || (readingPeriodHolder != null && readingPeriodHolder.next != loadingPeriodHolder))) {
            Renderer[] rendererArr = this.enabledRenderers;
            int length = rendererArr.length;
            int i = 0;
            while (i < length) {
                if (rendererArr[i].hasReadStreamToEnd()) {
                    i++;
                } else {
                    return;
                }
            }
            loadingPeriodHolder.mediaPeriod.maybeThrowPrepareError();
        }
    }

    private void handleSourceInfoRefreshed(MediaSourceRefreshInfo sourceRefreshInfo) throws ExoPlaybackException {
        MediaSourceRefreshInfo mediaSourceRefreshInfo = sourceRefreshInfo;
        if (mediaSourceRefreshInfo.source == this.mediaSource) {
            Timeline oldTimeline = r0.playbackInfo.timeline;
            Timeline timeline = mediaSourceRefreshInfo.timeline;
            Object manifest = mediaSourceRefreshInfo.manifest;
            r0.queue.setTimeline(timeline);
            r0.playbackInfo = r0.playbackInfo.copyWithTimeline(timeline, manifest);
            resolvePendingMessagePositions();
            long j = C0539C.TIME_UNSET;
            Pair<Integer, Long> periodPosition;
            int periodIndex;
            if (oldTimeline == null) {
                r0.playbackInfoUpdate.incrementPendingOperationAcks(r0.pendingPrepareCount);
                r0.pendingPrepareCount = 0;
                long j2 = 0;
                long positionUs;
                MediaPeriodId periodId;
                PlaybackInfo playbackInfo;
                if (r0.pendingInitialSeekPosition != null) {
                    periodPosition = resolveSeekPosition(r0.pendingInitialSeekPosition, true);
                    r0.pendingInitialSeekPosition = null;
                    if (periodPosition == null) {
                        handleSourceInfoRefreshEndedPlayback();
                    } else {
                        periodIndex = ((Integer) periodPosition.first).intValue();
                        positionUs = ((Long) periodPosition.second).longValue();
                        periodId = r0.queue.resolveMediaPeriodIdForAds(periodIndex, positionUs);
                        playbackInfo = r0.playbackInfo;
                        if (!periodId.isAd()) {
                            j2 = positionUs;
                        }
                        r0.playbackInfo = playbackInfo.fromNewPosition(periodId, j2, positionUs);
                    }
                } else if (r0.playbackInfo.startPositionUs == C0539C.TIME_UNSET) {
                    if (timeline.isEmpty()) {
                        handleSourceInfoRefreshEndedPlayback();
                    } else {
                        periodPosition = getPeriodPosition(timeline, timeline.getFirstWindowIndex(r0.shuffleModeEnabled), C0539C.TIME_UNSET);
                        periodIndex = ((Integer) periodPosition.first).intValue();
                        positionUs = ((Long) periodPosition.second).longValue();
                        periodId = r0.queue.resolveMediaPeriodIdForAds(periodIndex, positionUs);
                        playbackInfo = r0.playbackInfo;
                        if (!periodId.isAd()) {
                            j2 = positionUs;
                        }
                        r0.playbackInfo = playbackInfo.fromNewPosition(periodId, j2, positionUs);
                    }
                }
                return;
            }
            int playingPeriodIndex = r0.playbackInfo.periodId.periodIndex;
            MediaPeriodHolder periodHolder = r0.queue.getFrontPeriod();
            if (periodHolder != null || playingPeriodIndex < oldTimeline.getPeriodCount()) {
                int periodIndex2 = timeline.getIndexOfPeriod(periodHolder == null ? oldTimeline.getPeriod(playingPeriodIndex, r0.period, true).uid : periodHolder.uid);
                if (periodIndex2 == -1) {
                    int newPeriodIndex = resolveSubsequentPeriod(playingPeriodIndex, oldTimeline, timeline);
                    if (newPeriodIndex == -1) {
                        handleSourceInfoRefreshEndedPlayback();
                        return;
                    }
                    periodPosition = getPeriodPosition(timeline, timeline.getPeriod(newPeriodIndex, r0.period).windowIndex, C0539C.TIME_UNSET);
                    periodIndex = ((Integer) periodPosition.first).intValue();
                    long newPositionUs = ((Long) periodPosition.second).longValue();
                    timeline.getPeriod(periodIndex, r0.period, true);
                    if (periodHolder != null) {
                        Object newPeriodUid = r0.period.uid;
                        periodHolder.info = periodHolder.info.copyWithPeriodIndex(-1);
                        while (periodHolder.next != null) {
                            periodHolder = periodHolder.next;
                            if (periodHolder.uid.equals(newPeriodUid)) {
                                periodHolder.info = r0.queue.getUpdatedMediaPeriodInfo(periodHolder.info, periodIndex);
                            } else {
                                periodHolder.info = periodHolder.info.copyWithPeriodIndex(-1);
                            }
                        }
                    }
                    MediaPeriodId periodId2 = new MediaPeriodId(periodIndex);
                    r0.playbackInfo = r0.playbackInfo.fromNewPosition(periodId2, seekToPeriodPosition(periodId2, newPositionUs), (long) C0539C.TIME_UNSET);
                    return;
                }
                if (periodIndex2 != playingPeriodIndex) {
                    r0.playbackInfo = r0.playbackInfo.copyWithPeriodIndex(periodIndex2);
                }
                if (r0.playbackInfo.periodId.isAd()) {
                    MediaPeriodId periodId3 = r0.queue.resolveMediaPeriodIdForAds(periodIndex2, r0.playbackInfo.contentPositionUs);
                    if (!(periodId3.isAd() && periodId3.adIndexInAdGroup == r0.playbackInfo.periodId.adIndexInAdGroup)) {
                        long newPositionUs2 = seekToPeriodPosition(periodId3, r0.playbackInfo.contentPositionUs);
                        if (periodId3.isAd()) {
                            j = r0.playbackInfo.contentPositionUs;
                        }
                        r0.playbackInfo = r0.playbackInfo.fromNewPosition(periodId3, newPositionUs2, j);
                        return;
                    }
                }
                if (periodHolder != null) {
                    MediaPeriodHolder periodHolder2 = updatePeriodInfo(periodHolder, periodIndex2);
                    int periodIndex3 = periodIndex2;
                    while (periodHolder2.next != null) {
                        MediaPeriodHolder previousPeriodHolder = periodHolder2;
                        MediaPeriodHolder periodHolder3 = periodHolder2.next;
                        periodIndex3 = timeline.getNextPeriodIndex(periodIndex3, r0.period, r0.window, r0.repeatMode, r0.shuffleModeEnabled);
                        if (periodIndex3 == -1 || !periodHolder3.uid.equals(timeline.getPeriod(periodIndex3, r0.period, true).uid)) {
                            if (r0.queue.removeAfter(previousPeriodHolder)) {
                                MediaPeriodId id = r0.queue.getPlayingPeriod().info.id;
                                r0.playbackInfo = r0.playbackInfo.fromNewPosition(id, seekToPeriodPosition(id, r0.playbackInfo.positionUs, true), r0.playbackInfo.contentPositionUs);
                            }
                        }
                        periodHolder2 = updatePeriodInfo(periodHolder3, periodIndex3);
                    }
                }
            }
        }
    }

    private MediaPeriodHolder updatePeriodInfo(MediaPeriodHolder periodHolder, int periodIndex) {
        while (true) {
            periodHolder.info = this.queue.getUpdatedMediaPeriodInfo(periodHolder.info, periodIndex);
            if (periodHolder.info.isLastInTimelinePeriod) {
                break;
            } else if (periodHolder.next == null) {
                break;
            } else {
                periodHolder = periodHolder.next;
            }
        }
        return periodHolder;
    }

    private void handleSourceInfoRefreshEndedPlayback() {
        setState(4);
        resetInternal(false, true, false);
    }

    private int resolveSubsequentPeriod(int oldPeriodIndex, Timeline oldTimeline, Timeline newTimeline) {
        int newPeriodIndex = -1;
        int maxIterations = oldTimeline.getPeriodCount();
        for (int i = 0; i < maxIterations && newPeriodIndex == -1; i++) {
            oldPeriodIndex = oldTimeline.getNextPeriodIndex(oldPeriodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            if (oldPeriodIndex == -1) {
                break;
            }
            newPeriodIndex = newTimeline.getIndexOfPeriod(oldTimeline.getPeriod(oldPeriodIndex, this.period, true).uid);
        }
        return newPeriodIndex;
    }

    private Pair<Integer, Long> resolveSeekPosition(SeekPosition seekPosition, boolean trySubsequentPeriods) {
        Timeline timeline = this.playbackInfo.timeline;
        Timeline seekTimeline = seekPosition.timeline;
        if (timeline == null) {
            return null;
        }
        if (seekTimeline.isEmpty()) {
            seekTimeline = timeline;
        }
        try {
            Pair<Integer, Long> periodPosition = seekTimeline.getPeriodPosition(this.window, this.period, seekPosition.windowIndex, seekPosition.windowPositionUs);
            if (timeline == seekTimeline) {
                return periodPosition;
            }
            int periodIndex = timeline.getIndexOfPeriod(seekTimeline.getPeriod(((Integer) periodPosition.first).intValue(), this.period, true).uid);
            if (periodIndex != -1) {
                return Pair.create(Integer.valueOf(periodIndex), periodPosition.second);
            }
            if (trySubsequentPeriods) {
                periodIndex = resolveSubsequentPeriod(((Integer) periodPosition.first).intValue(), seekTimeline, timeline);
                if (periodIndex != -1) {
                    return getPeriodPosition(timeline, timeline.getPeriod(periodIndex, this.period).windowIndex, C0539C.TIME_UNSET);
                }
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalSeekPositionException(timeline, seekPosition.windowIndex, seekPosition.windowPositionUs);
        }
    }

    private Pair<Integer, Long> getPeriodPosition(Timeline timeline, int windowIndex, long windowPositionUs) {
        return timeline.getPeriodPosition(this.window, this.period, windowIndex, windowPositionUs);
    }

    private void updatePeriods() throws ExoPlaybackException, IOException {
        if (this.mediaSource != null) {
            ExoPlayerImplInternal exoPlayerImplInternal;
            if (exoPlayerImplInternal.playbackInfo.timeline == null) {
                exoPlayerImplInternal.mediaSource.maybeThrowSourceInfoRefreshError();
                return;
            }
            MediaPeriodHolder playingPeriodHolder;
            MediaPeriodHolder readingPeriodHolder;
            MediaPeriodHolder playingPeriodHolder2;
            boolean advancedPlayingPeriod;
            int discontinuityReason;
            MediaPeriodHolder oldPlayingPeriodHolder;
            boolean z;
            MediaPeriodHolder mediaPeriodHolder;
            Renderer renderer;
            SampleStream sampleStream;
            TrackSelectorResult oldTrackSelectorResult;
            TrackSelectorResult newTrackSelectorResult;
            boolean initialDiscontinuity;
            int i;
            Renderer renderer2;
            TrackSelection newSelection;
            boolean newRendererEnabled;
            boolean isNoSampleRenderer;
            RendererConfiguration oldConfig;
            RendererConfiguration newConfig;
            boolean z2;
            Renderer renderer3;
            SampleStream sampleStream2;
            maybeUpdateLoadingPeriod();
            MediaPeriodHolder loadingPeriodHolder = exoPlayerImplInternal.queue.getLoadingPeriod();
            int i2 = 0;
            if (loadingPeriodHolder != null) {
                if (!loadingPeriodHolder.isFullyBuffered()) {
                    if (!exoPlayerImplInternal.playbackInfo.isLoading) {
                        maybeContinueLoading();
                    }
                    if (!exoPlayerImplInternal.queue.hasPlayingPeriod()) {
                        playingPeriodHolder = exoPlayerImplInternal.queue.getPlayingPeriod();
                        readingPeriodHolder = exoPlayerImplInternal.queue.getReadingPeriod();
                        playingPeriodHolder2 = playingPeriodHolder;
                        advancedPlayingPeriod = false;
                        while (exoPlayerImplInternal.playWhenReady && playingPeriodHolder2 != readingPeriodHolder && exoPlayerImplInternal.rendererPositionUs >= playingPeriodHolder2.next.rendererPositionOffsetUs) {
                            if (advancedPlayingPeriod) {
                                maybeNotifyPlaybackInfoChanged();
                            }
                            discontinuityReason = playingPeriodHolder2.info.isLastInTimelinePeriod ? 0 : 3;
                            oldPlayingPeriodHolder = playingPeriodHolder2;
                            playingPeriodHolder2 = exoPlayerImplInternal.queue.advancePlayingPeriod();
                            updatePlayingPeriodRenderers(oldPlayingPeriodHolder);
                            exoPlayerImplInternal.playbackInfo = exoPlayerImplInternal.playbackInfo.fromNewPosition(playingPeriodHolder2.info.id, playingPeriodHolder2.info.startPositionUs, playingPeriodHolder2.info.contentPositionUs);
                            exoPlayerImplInternal.playbackInfoUpdate.setPositionDiscontinuity(discontinuityReason);
                            updatePlaybackPositions();
                            advancedPlayingPeriod = true;
                        }
                        if (readingPeriodHolder.info.isFinal) {
                            if (readingPeriodHolder.next != null) {
                                z = advancedPlayingPeriod;
                            } else if (readingPeriodHolder.next.prepared) {
                                mediaPeriodHolder = loadingPeriodHolder;
                            } else {
                                discontinuityReason = 0;
                                while (discontinuityReason < exoPlayerImplInternal.renderers.length) {
                                    renderer = exoPlayerImplInternal.renderers[discontinuityReason];
                                    sampleStream = readingPeriodHolder.sampleStreams[discontinuityReason];
                                    if (renderer.getStream() != sampleStream) {
                                        if (sampleStream != null || renderer.hasReadStreamToEnd()) {
                                            discontinuityReason++;
                                        }
                                    }
                                    return;
                                }
                                oldTrackSelectorResult = readingPeriodHolder.trackSelectorResult;
                                readingPeriodHolder = exoPlayerImplInternal.queue.advanceReadingPeriod();
                                newTrackSelectorResult = readingPeriodHolder.trackSelectorResult;
                                initialDiscontinuity = readingPeriodHolder.mediaPeriod.readDiscontinuity() == C0539C.TIME_UNSET;
                                i = 0;
                                while (i < exoPlayerImplInternal.renderers.length) {
                                    renderer2 = exoPlayerImplInternal.renderers[i];
                                    if (oldTrackSelectorResult.renderersEnabled[i]) {
                                        if (initialDiscontinuity) {
                                            if (renderer2.isCurrentStreamFinal()) {
                                                newSelection = newTrackSelectorResult.selections.get(i);
                                                newRendererEnabled = newTrackSelectorResult.renderersEnabled[i];
                                                isNoSampleRenderer = exoPlayerImplInternal.rendererCapabilities[i].getTrackType() != 5;
                                                oldConfig = oldTrackSelectorResult.rendererConfigurations[i];
                                                newConfig = newTrackSelectorResult.rendererConfigurations[i];
                                                if (newRendererEnabled || !newConfig.equals(oldConfig) || isNoSampleRenderer) {
                                                    mediaPeriodHolder = loadingPeriodHolder;
                                                    z2 = isNoSampleRenderer;
                                                    z = advancedPlayingPeriod;
                                                    renderer2.setCurrentStreamFinal();
                                                } else {
                                                    mediaPeriodHolder = loadingPeriodHolder;
                                                    z = advancedPlayingPeriod;
                                                    renderer2.replaceStream(getFormats(newSelection), readingPeriodHolder.sampleStreams[i], readingPeriodHolder.getRendererOffset());
                                                }
                                            } else {
                                                mediaPeriodHolder = loadingPeriodHolder;
                                                z = advancedPlayingPeriod;
                                            }
                                            i++;
                                            loadingPeriodHolder = mediaPeriodHolder;
                                            advancedPlayingPeriod = z;
                                            exoPlayerImplInternal = this;
                                        } else {
                                            renderer2.setCurrentStreamFinal();
                                        }
                                    }
                                    mediaPeriodHolder = loadingPeriodHolder;
                                    z = advancedPlayingPeriod;
                                    i++;
                                    loadingPeriodHolder = mediaPeriodHolder;
                                    advancedPlayingPeriod = z;
                                    exoPlayerImplInternal = this;
                                }
                                z = advancedPlayingPeriod;
                                return;
                            }
                            return;
                        }
                        while (i2 < exoPlayerImplInternal.renderers.length) {
                            renderer3 = exoPlayerImplInternal.renderers[i2];
                            sampleStream2 = readingPeriodHolder.sampleStreams[i2];
                            if (sampleStream2 != null && renderer3.getStream() == sampleStream2 && renderer3.hasReadStreamToEnd()) {
                                renderer3.setCurrentStreamFinal();
                            }
                            i2++;
                        }
                    }
                }
            }
            setIsLoading(false);
            if (!exoPlayerImplInternal.queue.hasPlayingPeriod()) {
                playingPeriodHolder = exoPlayerImplInternal.queue.getPlayingPeriod();
                readingPeriodHolder = exoPlayerImplInternal.queue.getReadingPeriod();
                playingPeriodHolder2 = playingPeriodHolder;
                advancedPlayingPeriod = false;
                while (exoPlayerImplInternal.playWhenReady) {
                    if (advancedPlayingPeriod) {
                        maybeNotifyPlaybackInfoChanged();
                    }
                    if (playingPeriodHolder2.info.isLastInTimelinePeriod) {
                    }
                    oldPlayingPeriodHolder = playingPeriodHolder2;
                    playingPeriodHolder2 = exoPlayerImplInternal.queue.advancePlayingPeriod();
                    updatePlayingPeriodRenderers(oldPlayingPeriodHolder);
                    exoPlayerImplInternal.playbackInfo = exoPlayerImplInternal.playbackInfo.fromNewPosition(playingPeriodHolder2.info.id, playingPeriodHolder2.info.startPositionUs, playingPeriodHolder2.info.contentPositionUs);
                    exoPlayerImplInternal.playbackInfoUpdate.setPositionDiscontinuity(discontinuityReason);
                    updatePlaybackPositions();
                    advancedPlayingPeriod = true;
                }
                if (readingPeriodHolder.info.isFinal) {
                    if (readingPeriodHolder.next != null) {
                        z = advancedPlayingPeriod;
                    } else if (readingPeriodHolder.next.prepared) {
                        discontinuityReason = 0;
                        while (discontinuityReason < exoPlayerImplInternal.renderers.length) {
                            renderer = exoPlayerImplInternal.renderers[discontinuityReason];
                            sampleStream = readingPeriodHolder.sampleStreams[discontinuityReason];
                            if (renderer.getStream() != sampleStream) {
                                if (sampleStream != null) {
                                }
                                discontinuityReason++;
                            }
                            return;
                        }
                        oldTrackSelectorResult = readingPeriodHolder.trackSelectorResult;
                        readingPeriodHolder = exoPlayerImplInternal.queue.advanceReadingPeriod();
                        newTrackSelectorResult = readingPeriodHolder.trackSelectorResult;
                        if (readingPeriodHolder.mediaPeriod.readDiscontinuity() == C0539C.TIME_UNSET) {
                        }
                        i = 0;
                        while (i < exoPlayerImplInternal.renderers.length) {
                            renderer2 = exoPlayerImplInternal.renderers[i];
                            if (oldTrackSelectorResult.renderersEnabled[i]) {
                                if (initialDiscontinuity) {
                                    if (renderer2.isCurrentStreamFinal()) {
                                        mediaPeriodHolder = loadingPeriodHolder;
                                        z = advancedPlayingPeriod;
                                    } else {
                                        newSelection = newTrackSelectorResult.selections.get(i);
                                        newRendererEnabled = newTrackSelectorResult.renderersEnabled[i];
                                        if (exoPlayerImplInternal.rendererCapabilities[i].getTrackType() != 5) {
                                        }
                                        oldConfig = oldTrackSelectorResult.rendererConfigurations[i];
                                        newConfig = newTrackSelectorResult.rendererConfigurations[i];
                                        if (newRendererEnabled) {
                                        }
                                        mediaPeriodHolder = loadingPeriodHolder;
                                        z2 = isNoSampleRenderer;
                                        z = advancedPlayingPeriod;
                                        renderer2.setCurrentStreamFinal();
                                    }
                                    i++;
                                    loadingPeriodHolder = mediaPeriodHolder;
                                    advancedPlayingPeriod = z;
                                    exoPlayerImplInternal = this;
                                } else {
                                    renderer2.setCurrentStreamFinal();
                                }
                            }
                            mediaPeriodHolder = loadingPeriodHolder;
                            z = advancedPlayingPeriod;
                            i++;
                            loadingPeriodHolder = mediaPeriodHolder;
                            advancedPlayingPeriod = z;
                            exoPlayerImplInternal = this;
                        }
                        z = advancedPlayingPeriod;
                        return;
                    } else {
                        mediaPeriodHolder = loadingPeriodHolder;
                    }
                    return;
                }
                while (i2 < exoPlayerImplInternal.renderers.length) {
                    renderer3 = exoPlayerImplInternal.renderers[i2];
                    sampleStream2 = readingPeriodHolder.sampleStreams[i2];
                    renderer3.setCurrentStreamFinal();
                    i2++;
                }
            }
        }
    }

    private void maybeUpdateLoadingPeriod() throws IOException {
        this.queue.reevaluateBuffer(this.rendererPositionUs);
        if (this.queue.shouldLoadNextMediaPeriod()) {
            MediaPeriodInfo info = this.queue.getNextMediaPeriodInfo(this.rendererPositionUs, this.playbackInfo);
            if (info == null) {
                this.mediaSource.maybeThrowSourceInfoRefreshError();
                return;
            }
            this.queue.enqueueNextMediaPeriod(this.rendererCapabilities, 60000000, this.trackSelector, this.loadControl.getAllocator(), this.mediaSource, this.playbackInfo.timeline.getPeriod(info.id.periodIndex, this.period, true).uid, info).prepare(this, info.startPositionUs);
            setIsLoading(true);
        }
    }

    private void handlePeriodPrepared(MediaPeriod mediaPeriod) throws ExoPlaybackException {
        if (this.queue.isLoading(mediaPeriod)) {
            updateLoadControlTrackSelection(this.queue.handleLoadingPeriodPrepared(this.mediaClock.getPlaybackParameters().speed));
            if (!this.queue.hasPlayingPeriod()) {
                resetRendererPosition(this.queue.advancePlayingPeriod().info.startPositionUs);
                updatePlayingPeriodRenderers(null);
            }
            maybeContinueLoading();
        }
    }

    private void handleContinueLoadingRequested(MediaPeriod mediaPeriod) {
        if (this.queue.isLoading(mediaPeriod)) {
            this.queue.reevaluateBuffer(this.rendererPositionUs);
            maybeContinueLoading();
        }
    }

    private void maybeContinueLoading() {
        MediaPeriodHolder loadingPeriodHolder = this.queue.getLoadingPeriod();
        long nextLoadPositionUs = loadingPeriodHolder.getNextLoadPositionUs();
        if (nextLoadPositionUs == Long.MIN_VALUE) {
            setIsLoading(false);
            return;
        }
        boolean continueLoading = this.loadControl.shouldContinueLoading(nextLoadPositionUs - loadingPeriodHolder.toPeriodTime(this.rendererPositionUs), this.mediaClock.getPlaybackParameters().speed);
        setIsLoading(continueLoading);
        if (continueLoading) {
            loadingPeriodHolder.continueLoading(this.rendererPositionUs);
        }
    }

    private void updatePlayingPeriodRenderers(MediaPeriodHolder oldPlayingPeriodHolder) throws ExoPlaybackException {
        MediaPeriodHolder newPlayingPeriodHolder = this.queue.getPlayingPeriod();
        if (newPlayingPeriodHolder != null) {
            if (oldPlayingPeriodHolder != newPlayingPeriodHolder) {
                boolean[] rendererWasEnabledFlags = new boolean[this.renderers.length];
                int enabledRendererCount = 0;
                int i = 0;
                while (i < this.renderers.length) {
                    Renderer renderer = this.renderers[i];
                    rendererWasEnabledFlags[i] = renderer.getState() != 0;
                    if (newPlayingPeriodHolder.trackSelectorResult.renderersEnabled[i]) {
                        enabledRendererCount++;
                    }
                    if (rendererWasEnabledFlags[i] && (!newPlayingPeriodHolder.trackSelectorResult.renderersEnabled[i] || (renderer.isCurrentStreamFinal() && renderer.getStream() == oldPlayingPeriodHolder.sampleStreams[i]))) {
                        disableRenderer(renderer);
                    }
                    i++;
                }
                this.playbackInfo = this.playbackInfo.copyWithTrackSelectorResult(newPlayingPeriodHolder.trackSelectorResult);
                enableRenderers(rendererWasEnabledFlags, enabledRendererCount);
            }
        }
    }

    private void enableRenderers(boolean[] rendererWasEnabledFlags, int totalEnabledRendererCount) throws ExoPlaybackException {
        this.enabledRenderers = new Renderer[totalEnabledRendererCount];
        int enabledRendererCount = 0;
        MediaPeriodHolder playingPeriodHolder = this.queue.getPlayingPeriod();
        for (int i = 0; i < this.renderers.length; i++) {
            if (playingPeriodHolder.trackSelectorResult.renderersEnabled[i]) {
                int enabledRendererCount2 = enabledRendererCount + 1;
                enableRenderer(i, rendererWasEnabledFlags[i], enabledRendererCount);
                enabledRendererCount = enabledRendererCount2;
            }
        }
    }

    private void enableRenderer(int rendererIndex, boolean wasRendererEnabled, int enabledRendererIndex) throws ExoPlaybackException {
        int i = rendererIndex;
        MediaPeriodHolder playingPeriodHolder = this.queue.getPlayingPeriod();
        Renderer renderer = this.renderers[i];
        this.enabledRenderers[enabledRendererIndex] = renderer;
        if (renderer.getState() == 0) {
            RendererConfiguration rendererConfiguration = playingPeriodHolder.trackSelectorResult.rendererConfigurations[i];
            Format[] formats = getFormats(playingPeriodHolder.trackSelectorResult.selections.get(i));
            boolean z = r0.playWhenReady && r0.playbackInfo.playbackState == 3;
            boolean playing = z;
            boolean joining = !wasRendererEnabled && playing;
            renderer.enable(rendererConfiguration, formats, playingPeriodHolder.sampleStreams[i], r0.rendererPositionUs, joining, playingPeriodHolder.getRendererOffset());
            r0.mediaClock.onRendererEnabled(renderer);
            if (playing) {
                renderer.start();
            }
        }
    }

    private boolean rendererWaitingForNextStream(Renderer renderer) {
        MediaPeriodHolder readingPeriodHolder = this.queue.getReadingPeriod();
        return readingPeriodHolder.next != null && readingPeriodHolder.next.prepared && renderer.hasReadStreamToEnd();
    }

    private static Format[] getFormats(TrackSelection newSelection) {
        int i = 0;
        int length = newSelection != null ? newSelection.length() : 0;
        Format[] formats = new Format[length];
        while (i < length) {
            formats[i] = newSelection.getFormat(i);
            i++;
        }
        return formats;
    }
}
