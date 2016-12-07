package org.telegram.messenger.exoplayer2;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer.ExoPlayerMessage;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelections;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MediaClock;
import org.telegram.messenger.exoplayer2.util.PriorityHandlerThread;
import org.telegram.messenger.exoplayer2.util.StandaloneMediaClock;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExoPlayerImplInternal<T> implements Callback, MediaPeriod.Callback, InvalidationListener, Listener {
    private static final int IDLE_INTERVAL_MS = 1000;
    private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
    private static final int MSG_CUSTOM = 10;
    private static final int MSG_DO_SOME_WORK = 2;
    public static final int MSG_ERROR = 6;
    public static final int MSG_LOADING_CHANGED = 2;
    private static final int MSG_PERIOD_PREPARED = 7;
    public static final int MSG_POSITION_DISCONTINUITY = 4;
    private static final int MSG_PREPARE = 0;
    private static final int MSG_REFRESH_SOURCE_INFO = 6;
    private static final int MSG_RELEASE = 5;
    public static final int MSG_SEEK_ACK = 3;
    private static final int MSG_SEEK_TO = 3;
    private static final int MSG_SET_PLAY_WHEN_READY = 1;
    private static final int MSG_SOURCE_CONTINUE_LOADING_REQUESTED = 8;
    public static final int MSG_SOURCE_INFO_REFRESHED = 5;
    public static final int MSG_STATE_CHANGED = 1;
    private static final int MSG_STOP = 4;
    private static final int MSG_TRACK_SELECTION_INVALIDATED = 9;
    private static final int PREPARING_SOURCE_INTERVAL_MS = 10;
    private static final int RENDERING_INTERVAL_MS = 10;
    private static final String TAG = "ExoPlayerImplInternal";
    private int bufferAheadPeriodCount;
    private int customMessagesProcessed;
    private int customMessagesSent;
    private long elapsedRealtimeUs;
    private Renderer[] enabledRenderers;
    private final Handler eventHandler;
    private final Handler handler;
    private final HandlerThread internalPlaybackThread;
    private boolean isLoading;
    private boolean isTimelineEnded;
    private boolean isTimelineReady;
    private final LoadControl loadControl;
    private MediaPeriodHolder<T> loadingPeriodHolder;
    private MediaSource mediaSource;
    private final Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private MediaPeriodHolder<T> playingPeriodHolder;
    private MediaPeriodHolder<T> readingPeriodHolder;
    private boolean rebuffering;
    private boolean released;
    private final RendererCapabilities[] rendererCapabilities;
    private MediaClock rendererMediaClock;
    private Renderer rendererMediaClockSource;
    private long rendererPositionUs;
    private final Renderer[] renderers;
    private final StandaloneMediaClock standaloneMediaClock;
    private int state = 1;
    private Timeline timeline;
    private final TrackSelector<T> trackSelector;
    private final Window window;

    private static final class MediaPeriodHolder<T> {
        public boolean hasEnabledTracks;
        public int index;
        public boolean isLast;
        public final boolean[] mayRetainStreamFlags;
        public final MediaPeriod mediaPeriod;
        private final MediaSource mediaSource;
        public boolean needsContinueLoading;
        public MediaPeriodHolder<T> next;
        private TrackSelections<T> periodTrackSelections;
        public boolean prepared;
        private final RendererCapabilities[] rendererCapabilities;
        public long rendererPositionOffsetUs;
        private final Renderer[] renderers;
        public final SampleStream[] sampleStreams;
        public long startPositionUs;
        private TrackSelections<T> trackSelections;
        private final TrackSelector<T> trackSelector;
        public final Object uid;

        public MediaPeriodHolder(Renderer[] renderers, RendererCapabilities[] rendererCapabilities, TrackSelector<T> trackSelector, MediaSource mediaSource, MediaPeriod mediaPeriod, Object uid, long positionUs) {
            this.renderers = renderers;
            this.rendererCapabilities = rendererCapabilities;
            this.trackSelector = trackSelector;
            this.mediaSource = mediaSource;
            this.mediaPeriod = mediaPeriod;
            this.uid = Assertions.checkNotNull(uid);
            this.sampleStreams = new SampleStream[renderers.length];
            this.mayRetainStreamFlags = new boolean[renderers.length];
            this.startPositionUs = positionUs;
        }

        public void setNext(MediaPeriodHolder<T> next) {
            this.next = next;
        }

        public void setIndex(Timeline timeline, Window window, int periodIndex) {
            this.index = periodIndex;
            boolean z = this.index == timeline.getPeriodCount() + -1 && !window.isDynamic;
            this.isLast = z;
        }

        public boolean isFullyBuffered() {
            return this.prepared && (!this.hasEnabledTracks || this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE);
        }

        public void handlePrepared(long positionUs, LoadControl loadControl) throws ExoPlaybackException {
            this.prepared = true;
            selectTracks();
            this.startPositionUs = updatePeriodTrackSelection(positionUs, loadControl, false);
        }

        public boolean selectTracks() throws ExoPlaybackException {
            TrackSelections<T> newTrackSelections = this.trackSelector.selectTracks(this.rendererCapabilities, this.mediaPeriod.getTrackGroups());
            if (newTrackSelections.equals(this.periodTrackSelections)) {
                return false;
            }
            this.trackSelections = newTrackSelections;
            return true;
        }

        public long updatePeriodTrackSelection(long positionUs, LoadControl loadControl, boolean forceRecreateStreams) throws ExoPlaybackException {
            return updatePeriodTrackSelection(positionUs, loadControl, forceRecreateStreams, new boolean[this.renderers.length]);
        }

        public long updatePeriodTrackSelection(long positionUs, LoadControl loadControl, boolean forceRecreateStreams, boolean[] streamResetFlags) throws ExoPlaybackException {
            int i;
            for (i = 0; i < this.trackSelections.length; i++) {
                boolean z;
                boolean[] zArr = this.mayRetainStreamFlags;
                if (!forceRecreateStreams) {
                    Object obj;
                    if (this.periodTrackSelections == null) {
                        obj = null;
                    } else {
                        obj = this.periodTrackSelections.get(i);
                    }
                    if (Util.areEqual(obj, this.trackSelections.get(i))) {
                        z = true;
                        zArr[i] = z;
                    }
                }
                z = false;
                zArr[i] = z;
            }
            positionUs = this.mediaPeriod.selectTracks(this.trackSelections.getAll(), this.mayRetainStreamFlags, this.sampleStreams, streamResetFlags, positionUs);
            this.periodTrackSelections = this.trackSelections;
            this.hasEnabledTracks = false;
            for (i = 0; i < this.sampleStreams.length; i++) {
                if (this.sampleStreams[i] != null) {
                    if (this.trackSelections.get(i) != null) {
                        z = true;
                    } else {
                        z = false;
                    }
                    Assertions.checkState(z);
                    this.hasEnabledTracks = true;
                } else {
                    Assertions.checkState(this.trackSelections.get(i) == null);
                }
            }
            loadControl.onTracksSelected(this.renderers, this.mediaPeriod.getTrackGroups(), this.trackSelections);
            return positionUs;
        }

        public void release() {
            try {
                this.mediaSource.releasePeriod(this.mediaPeriod);
            } catch (RuntimeException e) {
                Log.e(ExoPlayerImplInternal.TAG, "Period release failed.", e);
            }
        }
    }

    public static final class PlaybackInfo {
        public volatile long bufferedPositionUs;
        public final int periodIndex;
        public volatile long positionUs;
        public final long startPositionUs;

        public PlaybackInfo(int periodIndex, long startPositionUs) {
            this.periodIndex = periodIndex;
            this.startPositionUs = startPositionUs;
            this.positionUs = startPositionUs;
            this.bufferedPositionUs = startPositionUs;
        }
    }

    public ExoPlayerImplInternal(Renderer[] renderers, TrackSelector<T> trackSelector, LoadControl loadControl, boolean playWhenReady, Handler eventHandler, PlaybackInfo playbackInfo) {
        this.renderers = renderers;
        this.trackSelector = trackSelector;
        this.loadControl = loadControl;
        this.playWhenReady = playWhenReady;
        this.eventHandler = eventHandler;
        this.playbackInfo = playbackInfo;
        this.rendererCapabilities = new RendererCapabilities[renderers.length];
        for (int i = 0; i < renderers.length; i++) {
            renderers[i].setIndex(i);
            this.rendererCapabilities[i] = renderers[i].getCapabilities();
        }
        this.standaloneMediaClock = new StandaloneMediaClock();
        this.enabledRenderers = new Renderer[0];
        this.window = new Window();
        this.period = new Period();
        trackSelector.init(this);
        this.internalPlaybackThread = new PriorityHandlerThread("ExoPlayerImplInternal:Handler", -16);
        this.internalPlaybackThread.start();
        this.handler = new Handler(this.internalPlaybackThread.getLooper(), this);
    }

    public void prepare(MediaSource mediaSource, boolean resetPosition) {
        int i;
        Handler handler = this.handler;
        if (resetPosition) {
            i = 1;
        } else {
            i = 0;
        }
        handler.obtainMessage(0, i, 0, mediaSource).sendToTarget();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        int i;
        Handler handler = this.handler;
        if (playWhenReady) {
            i = 1;
        } else {
            i = 0;
        }
        handler.obtainMessage(1, i, 0).sendToTarget();
    }

    public void seekTo(int periodIndex, long positionUs) {
        this.handler.obtainMessage(3, periodIndex, 0, Long.valueOf(positionUs)).sendToTarget();
    }

    public void stop() {
        this.handler.sendEmptyMessage(4);
    }

    public void sendMessages(ExoPlayerMessage... messages) {
        if (this.released) {
            Log.w(TAG, "Ignoring messages sent after release.");
            return;
        }
        this.customMessagesSent++;
        this.handler.obtainMessage(10, messages).sendToTarget();
    }

    public synchronized void blockingSendMessages(ExoPlayerMessage... messages) {
        if (this.released) {
            Log.w(TAG, "Ignoring messages sent after release.");
        } else {
            int messageNumber = this.customMessagesSent;
            this.customMessagesSent = messageNumber + 1;
            this.handler.obtainMessage(10, messages).sendToTarget();
            while (this.customMessagesProcessed <= messageNumber) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public synchronized void release() {
        if (!this.released) {
            this.handler.sendEmptyMessage(5);
            while (!this.released) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            this.internalPlaybackThread.quit();
        }
    }

    public void onSourceInfoRefreshed(Timeline timeline, Object manifest) {
        this.handler.obtainMessage(6, Pair.create(timeline, manifest)).sendToTarget();
    }

    public void onPrepared(MediaPeriod source) {
        this.handler.obtainMessage(7, source).sendToTarget();
    }

    public void onContinueLoadingRequested(MediaPeriod source) {
        this.handler.obtainMessage(8, source).sendToTarget();
    }

    public void onTrackSelectionsInvalidated() {
        this.handler.sendEmptyMessage(9);
    }

    public boolean handleMessage(Message msg) {
        boolean z = false;
        try {
            switch (msg.what) {
                case 0:
                    MediaSource mediaSource = (MediaSource) msg.obj;
                    if (msg.arg1 != 0) {
                        z = true;
                    }
                    prepareInternal(mediaSource, z);
                    return true;
                case 1:
                    if (msg.arg1 != 0) {
                        z = true;
                    }
                    setPlayWhenReadyInternal(z);
                    return true;
                case 2:
                    doSomeWork();
                    return true;
                case 3:
                    seekToInternal(msg.arg1, ((Long) msg.obj).longValue());
                    return true;
                case 4:
                    stopInternal();
                    return true;
                case 5:
                    releaseInternal();
                    return true;
                case 6:
                    handleSourceInfoRefreshed((Pair) msg.obj);
                    return true;
                case 7:
                    handlePeriodPrepared((MediaPeriod) msg.obj);
                    return true;
                case 8:
                    handleContinueLoadingRequested((MediaPeriod) msg.obj);
                    return true;
                case 9:
                    reselectTracksInternal();
                    return true;
                case 10:
                    sendMessagesInternal((ExoPlayerMessage[]) msg.obj);
                    return true;
                default:
                    return false;
            }
        } catch (ExoPlaybackException e) {
            Log.e(TAG, "Renderer error.", e);
            this.eventHandler.obtainMessage(6, e).sendToTarget();
            stopInternal();
            return true;
        } catch (IOException e2) {
            Log.e(TAG, "Source error.", e2);
            this.eventHandler.obtainMessage(6, ExoPlaybackException.createForSource(e2)).sendToTarget();
            stopInternal();
            return true;
        } catch (RuntimeException e3) {
            Log.e(TAG, "Internal runtime error.", e3);
            this.eventHandler.obtainMessage(6, ExoPlaybackException.createForUnexpected(e3)).sendToTarget();
            stopInternal();
            return true;
        }
    }

    private void setState(int state) {
        if (this.state != state) {
            this.state = state;
            this.eventHandler.obtainMessage(1, state, 0).sendToTarget();
        }
    }

    private void setIsLoading(boolean isLoading) {
        if (this.isLoading != isLoading) {
            int i;
            this.isLoading = isLoading;
            Handler handler = this.eventHandler;
            if (isLoading) {
                i = 1;
            } else {
                i = 0;
            }
            handler.obtainMessage(2, i, 0).sendToTarget();
        }
    }

    private void prepareInternal(MediaSource mediaSource, boolean resetPosition) throws ExoPlaybackException {
        resetInternal();
        this.loadControl.onPrepared();
        if (resetPosition) {
            this.playbackInfo = new PlaybackInfo(0, C.TIME_UNSET);
        }
        this.mediaSource = mediaSource;
        mediaSource.prepareSource(this);
        setState(2);
        this.handler.sendEmptyMessage(2);
    }

    private void setPlayWhenReadyInternal(boolean playWhenReady) throws ExoPlaybackException {
        this.rebuffering = false;
        this.playWhenReady = playWhenReady;
        if (!playWhenReady) {
            stopRenderers();
            updatePlaybackPositions();
        } else if (this.state == 3) {
            startRenderers();
            this.handler.sendEmptyMessage(2);
        } else if (this.state == 2) {
            this.handler.sendEmptyMessage(2);
        }
    }

    private void startRenderers() throws ExoPlaybackException {
        int i = 0;
        this.rebuffering = false;
        this.standaloneMediaClock.start();
        Renderer[] rendererArr = this.enabledRenderers;
        int length = rendererArr.length;
        while (i < length) {
            rendererArr[i].start();
            i++;
        }
    }

    private void stopRenderers() throws ExoPlaybackException {
        this.standaloneMediaClock.stop();
        for (Renderer renderer : this.enabledRenderers) {
            ensureStopped(renderer);
        }
    }

    private void updatePlaybackPositions() throws ExoPlaybackException {
        if (this.playingPeriodHolder != null) {
            long bufferedPositionUs;
            long periodPositionUs = this.playingPeriodHolder.mediaPeriod.readDiscontinuity();
            if (periodPositionUs != C.TIME_UNSET) {
                resetRendererPosition(periodPositionUs);
            } else {
                if (this.rendererMediaClockSource == null || this.rendererMediaClockSource.isEnded()) {
                    this.rendererPositionUs = this.standaloneMediaClock.getPositionUs();
                } else {
                    this.rendererPositionUs = this.rendererMediaClock.getPositionUs();
                    this.standaloneMediaClock.setPositionUs(this.rendererPositionUs);
                }
                periodPositionUs = this.rendererPositionUs - this.playingPeriodHolder.rendererPositionOffsetUs;
            }
            this.playbackInfo.positionUs = periodPositionUs;
            this.elapsedRealtimeUs = SystemClock.elapsedRealtime() * 1000;
            if (this.enabledRenderers.length == 0) {
                bufferedPositionUs = Long.MIN_VALUE;
            } else {
                bufferedPositionUs = this.playingPeriodHolder.mediaPeriod.getBufferedPositionUs();
            }
            PlaybackInfo playbackInfo = this.playbackInfo;
            if (bufferedPositionUs == Long.MIN_VALUE) {
                bufferedPositionUs = this.timeline.getPeriod(this.playingPeriodHolder.index, this.period).getDurationUs();
            }
            playbackInfo.bufferedPositionUs = bufferedPositionUs;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doSomeWork() throws ExoPlaybackException, IOException {
        long operationStartTimeMs = SystemClock.elapsedRealtime();
        updatePeriods();
        if (this.playingPeriodHolder == null) {
            maybeThrowPeriodPrepareError();
            scheduleNextWork(operationStartTimeMs, 10);
            return;
        }
        TraceUtil.beginSection("doSomeWork");
        updatePlaybackPositions();
        boolean allRenderersEnded = true;
        boolean allRenderersReadyOrEnded = true;
        for (Renderer renderer : this.enabledRenderers) {
            renderer.render(this.rendererPositionUs, this.elapsedRealtimeUs);
            allRenderersEnded = allRenderersEnded && renderer.isEnded();
            boolean rendererReadyOrEnded = renderer.isReady() || renderer.isEnded();
            if (!rendererReadyOrEnded) {
                renderer.maybeThrowStreamError();
            }
            if (allRenderersReadyOrEnded && rendererReadyOrEnded) {
                allRenderersReadyOrEnded = true;
            } else {
                allRenderersReadyOrEnded = false;
            }
        }
        if (!allRenderersReadyOrEnded) {
            maybeThrowPeriodPrepareError();
        }
        long playingPeriodDurationUs = this.timeline.getPeriod(this.playingPeriodHolder.index, this.period).getDurationUs();
        if (allRenderersEnded && ((playingPeriodDurationUs == C.TIME_UNSET || playingPeriodDurationUs <= this.playbackInfo.positionUs) && this.isTimelineEnded)) {
            setState(4);
            stopRenderers();
        } else if (this.state == 2) {
            if (this.enabledRenderers.length > 0) {
                if (allRenderersReadyOrEnded) {
                }
            } else if (this.isTimelineReady) {
            }
            setState(3);
            if (this.playWhenReady) {
                startRenderers();
            }
        } else if (this.state == 3) {
            if (this.enabledRenderers.length <= 0) {
                if (!this.isTimelineReady) {
                }
            }
            this.rebuffering = this.playWhenReady;
            setState(2);
            stopRenderers();
        }
        if (this.state == 2) {
            for (Renderer renderer2 : this.enabledRenderers) {
                renderer2.maybeThrowStreamError();
            }
        }
        if ((this.playWhenReady && this.state == 3) || this.state == 2) {
            scheduleNextWork(operationStartTimeMs, 10);
        } else if (this.enabledRenderers.length != 0) {
            scheduleNextWork(operationStartTimeMs, 1000);
        } else {
            this.handler.removeMessages(2);
        }
        TraceUtil.endSection();
    }

    private void scheduleNextWork(long thisOperationStartTimeMs, long intervalMs) {
        this.handler.removeMessages(2);
        long nextOperationDelayMs = (thisOperationStartTimeMs + intervalMs) - SystemClock.elapsedRealtime();
        if (nextOperationDelayMs <= 0) {
            this.handler.sendEmptyMessage(2);
        } else {
            this.handler.sendEmptyMessageDelayed(2, nextOperationDelayMs);
        }
    }

    private void seekToInternal(int periodIndex, long periodPositionUs) throws ExoPlaybackException {
        if (periodPositionUs == C.TIME_UNSET) {
            try {
                if (this.timeline != null && periodIndex < this.timeline.getPeriodCount()) {
                    Pair<Integer, Long> defaultPosition = getDefaultPosition(periodIndex);
                    periodIndex = ((Integer) defaultPosition.first).intValue();
                    periodPositionUs = ((Long) defaultPosition.second).longValue();
                }
            } catch (Throwable th) {
                this.playbackInfo = new PlaybackInfo(periodIndex, periodPositionUs);
                this.eventHandler.obtainMessage(3, this.playbackInfo).sendToTarget();
            }
        }
        if (periodIndex == this.playbackInfo.periodIndex && ((periodPositionUs == C.TIME_UNSET && this.playbackInfo.positionUs == C.TIME_UNSET) || periodPositionUs / 1000 == this.playbackInfo.positionUs / 1000)) {
            this.playbackInfo = new PlaybackInfo(periodIndex, periodPositionUs);
            this.eventHandler.obtainMessage(3, this.playbackInfo).sendToTarget();
            return;
        }
        this.playbackInfo = new PlaybackInfo(periodIndex, seekToPeriodPosition(periodIndex, periodPositionUs));
        this.eventHandler.obtainMessage(3, this.playbackInfo).sendToTarget();
    }

    private long seekToPeriodPosition(int periodIndex, long periodPositionUs) throws ExoPlaybackException {
        if (this.mediaSource == null) {
            if (periodPositionUs != C.TIME_UNSET) {
                resetRendererPosition(periodPositionUs);
            }
            return periodPositionUs;
        }
        stopRenderers();
        this.rebuffering = false;
        setState(2);
        if (periodPositionUs == C.TIME_UNSET || (this.readingPeriodHolder != this.playingPeriodHolder && (periodIndex == this.playingPeriodHolder.index || periodIndex == this.readingPeriodHolder.index))) {
            periodIndex = -1;
        }
        MediaPeriodHolder<T> newPlayingPeriodHolder = null;
        if (this.playingPeriodHolder != null) {
            MediaPeriodHolder<T> periodHolder = this.playingPeriodHolder;
            while (periodHolder != null) {
                if (periodHolder.index == periodIndex && periodHolder.prepared) {
                    newPlayingPeriodHolder = periodHolder;
                } else {
                    periodHolder.release();
                }
                periodHolder = periodHolder.next;
            }
        } else if (this.loadingPeriodHolder != null) {
            this.loadingPeriodHolder.release();
        }
        if (newPlayingPeriodHolder != this.playingPeriodHolder) {
            for (Renderer renderer : this.enabledRenderers) {
                renderer.disable();
            }
            this.enabledRenderers = new Renderer[0];
            this.rendererMediaClock = null;
            this.rendererMediaClockSource = null;
        }
        this.bufferAheadPeriodCount = 0;
        if (newPlayingPeriodHolder != null) {
            newPlayingPeriodHolder.next = null;
            setPlayingPeriodHolder(newPlayingPeriodHolder);
            updateTimelineState();
            this.readingPeriodHolder = this.playingPeriodHolder;
            this.loadingPeriodHolder = this.playingPeriodHolder;
            if (this.playingPeriodHolder.hasEnabledTracks) {
                periodPositionUs = this.playingPeriodHolder.mediaPeriod.seekToUs(periodPositionUs);
            }
            resetRendererPosition(periodPositionUs);
            maybeContinueLoading();
        } else {
            this.playingPeriodHolder = null;
            this.readingPeriodHolder = null;
            this.loadingPeriodHolder = null;
            if (periodPositionUs != C.TIME_UNSET) {
                resetRendererPosition(periodPositionUs);
            }
        }
        updatePlaybackPositions();
        this.handler.sendEmptyMessage(2);
        return periodPositionUs;
    }

    private void resetRendererPosition(long periodPositionUs) throws ExoPlaybackException {
        this.rendererPositionUs = (this.playingPeriodHolder == null ? 0 : this.playingPeriodHolder.rendererPositionOffsetUs) + periodPositionUs;
        this.standaloneMediaClock.setPositionUs(this.rendererPositionUs);
        for (Renderer renderer : this.enabledRenderers) {
            renderer.resetPosition(this.rendererPositionUs);
        }
    }

    private void stopInternal() {
        resetInternal();
        this.loadControl.onStopped();
        setState(1);
    }

    private void releaseInternal() {
        resetInternal();
        this.loadControl.onReleased();
        setState(1);
        synchronized (this) {
            this.released = true;
            notifyAll();
        }
    }

    private void resetInternal() {
        Exception e;
        this.handler.removeMessages(2);
        this.rebuffering = false;
        this.standaloneMediaClock.stop();
        this.rendererMediaClock = null;
        this.rendererMediaClockSource = null;
        for (Renderer renderer : this.enabledRenderers) {
            try {
                ensureStopped(renderer);
                renderer.disable();
            } catch (ExoPlaybackException e2) {
                e = e2;
            } catch (RuntimeException e3) {
                e = e3;
            }
        }
        this.enabledRenderers = new Renderer[0];
        releasePeriodHoldersFrom(this.playingPeriodHolder != null ? this.playingPeriodHolder : this.loadingPeriodHolder);
        if (this.mediaSource != null) {
            this.mediaSource.releaseSource();
            this.mediaSource = null;
        }
        this.isTimelineReady = false;
        this.isTimelineEnded = false;
        this.playingPeriodHolder = null;
        this.readingPeriodHolder = null;
        this.loadingPeriodHolder = null;
        this.timeline = null;
        this.bufferAheadPeriodCount = 0;
        setIsLoading(false);
        return;
        Log.e(TAG, "Stop failed.", e);
    }

    private void sendMessagesInternal(ExoPlayerMessage[] messages) throws ExoPlaybackException {
        try {
            for (ExoPlayerMessage message : messages) {
                message.target.handleMessage(message.messageType, message.message);
            }
            if (this.mediaSource != null) {
                this.handler.sendEmptyMessage(2);
            }
            synchronized (this) {
                this.customMessagesProcessed++;
                notifyAll();
            }
        } catch (Throwable th) {
            synchronized (this) {
                this.customMessagesProcessed++;
                notifyAll();
            }
        }
    }

    private void ensureStopped(Renderer renderer) throws ExoPlaybackException {
        if (renderer.getState() == 2) {
            renderer.stop();
        }
    }

    private void reselectTracksInternal() throws ExoPlaybackException {
        if (this.playingPeriodHolder != null) {
            MediaPeriodHolder<T> periodHolder = this.playingPeriodHolder;
            boolean selectionsChangedForReadPeriod = true;
            while (periodHolder != null && periodHolder.prepared) {
                if (periodHolder.selectTracks()) {
                    if (selectionsChangedForReadPeriod) {
                        boolean recreateStreams = this.readingPeriodHolder != this.playingPeriodHolder;
                        releasePeriodHoldersFrom(this.playingPeriodHolder.next);
                        this.playingPeriodHolder.next = null;
                        this.readingPeriodHolder = this.playingPeriodHolder;
                        this.loadingPeriodHolder = this.playingPeriodHolder;
                        this.bufferAheadPeriodCount = 0;
                        boolean[] streamResetFlags = new boolean[this.renderers.length];
                        long periodPositionUs = this.playingPeriodHolder.updatePeriodTrackSelection(this.playbackInfo.positionUs, this.loadControl, recreateStreams, streamResetFlags);
                        if (periodPositionUs != this.playbackInfo.positionUs) {
                            this.playbackInfo.positionUs = periodPositionUs;
                            resetRendererPosition(periodPositionUs);
                        }
                        int enabledRendererCount = 0;
                        boolean[] rendererWasEnabledFlags = new boolean[this.renderers.length];
                        for (int i = 0; i < this.renderers.length; i++) {
                            Renderer renderer = this.renderers[i];
                            rendererWasEnabledFlags[i] = renderer.getState() != 0;
                            SampleStream sampleStream = this.playingPeriodHolder.sampleStreams[i];
                            if (sampleStream != null) {
                                enabledRendererCount++;
                            }
                            if (rendererWasEnabledFlags[i]) {
                                if (sampleStream != renderer.getStream()) {
                                    if (renderer == this.rendererMediaClockSource) {
                                        if (sampleStream == null) {
                                            this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
                                        }
                                        this.rendererMediaClock = null;
                                        this.rendererMediaClockSource = null;
                                    }
                                    ensureStopped(renderer);
                                    renderer.disable();
                                } else if (streamResetFlags[i]) {
                                    renderer.resetPosition(this.playbackInfo.positionUs);
                                }
                            }
                        }
                        this.trackSelector.onSelectionActivated(this.playingPeriodHolder.trackSelections);
                        enableRenderers(rendererWasEnabledFlags, enabledRendererCount);
                    } else {
                        this.loadingPeriodHolder = periodHolder;
                        periodHolder = this.loadingPeriodHolder.next;
                        while (periodHolder != null) {
                            periodHolder.release();
                            periodHolder = periodHolder.next;
                            this.bufferAheadPeriodCount--;
                        }
                        this.loadingPeriodHolder.next = null;
                        this.loadingPeriodHolder.updatePeriodTrackSelection(Math.max(0, this.rendererPositionUs - this.loadingPeriodHolder.rendererPositionOffsetUs), this.loadControl, false);
                    }
                    maybeContinueLoading();
                    updatePlaybackPositions();
                    this.handler.sendEmptyMessage(2);
                    return;
                }
                if (periodHolder == this.readingPeriodHolder) {
                    selectionsChangedForReadPeriod = false;
                }
                periodHolder = periodHolder.next;
            }
        }
    }

    private boolean haveSufficientBuffer(boolean rebuffering) {
        if (this.loadingPeriodHolder == null) {
            return false;
        }
        long loadingPeriodBufferedPositionUs;
        long loadingPeriodPositionUs = this.rendererPositionUs - this.loadingPeriodHolder.rendererPositionOffsetUs;
        if (this.loadingPeriodHolder.prepared) {
            loadingPeriodBufferedPositionUs = this.loadingPeriodHolder.mediaPeriod.getBufferedPositionUs();
        } else {
            loadingPeriodBufferedPositionUs = 0;
        }
        if (loadingPeriodBufferedPositionUs == Long.MIN_VALUE) {
            if (this.loadingPeriodHolder.isLast) {
                return true;
            }
            loadingPeriodBufferedPositionUs = this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
        }
        return this.loadControl.shouldStartPlayback(loadingPeriodBufferedPositionUs - loadingPeriodPositionUs, rebuffering);
    }

    private void maybeThrowPeriodPrepareError() throws IOException {
        if (this.loadingPeriodHolder != null && !this.loadingPeriodHolder.prepared) {
            if (this.readingPeriodHolder == null || this.readingPeriodHolder.next == this.loadingPeriodHolder) {
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
                this.loadingPeriodHolder.mediaPeriod.maybeThrowPrepareError();
            }
        }
    }

    private void handleSourceInfoRefreshed(Pair<Timeline, Object> timelineAndManifest) throws ExoPlaybackException, IOException {
        this.eventHandler.obtainMessage(5, timelineAndManifest).sendToTarget();
        Timeline oldTimeline = this.timeline;
        this.timeline = (Timeline) timelineAndManifest.first;
        int index;
        if (this.playingPeriodHolder != null) {
            index = this.timeline.getIndexOfPeriod(this.playingPeriodHolder.uid);
            if (index == -1) {
                attemptRestart(this.timeline, oldTimeline, this.playingPeriodHolder.index);
                return;
            }
            this.timeline.getPeriod(index, this.period, true);
            this.playingPeriodHolder.setIndex(this.timeline, this.timeline.getWindow(this.period.windowIndex, this.window), index);
            MediaPeriodHolder<T> previousPeriodHolder = this.playingPeriodHolder;
            boolean seenReadingPeriod = false;
            this.bufferAheadPeriodCount = 0;
            while (previousPeriodHolder.next != null) {
                MediaPeriodHolder<T> periodHolder = previousPeriodHolder.next;
                index++;
                this.timeline.getPeriod(index, this.period, true);
                if (periodHolder.uid.equals(this.period.uid)) {
                    this.bufferAheadPeriodCount++;
                    periodHolder.setIndex(this.timeline, this.timeline.getWindow(this.timeline.getPeriod(index, this.period).windowIndex, this.window), index);
                    if (periodHolder == this.readingPeriodHolder) {
                        seenReadingPeriod = true;
                    }
                    previousPeriodHolder = periodHolder;
                } else if (seenReadingPeriod) {
                    this.loadingPeriodHolder = previousPeriodHolder;
                    this.loadingPeriodHolder.next = null;
                    releasePeriodHoldersFrom(periodHolder);
                } else {
                    index = this.playingPeriodHolder.index;
                    releasePeriodHoldersFrom(this.playingPeriodHolder);
                    this.playingPeriodHolder = null;
                    this.readingPeriodHolder = null;
                    this.loadingPeriodHolder = null;
                    long newPositionUs = seekToPeriodPosition(index, this.playbackInfo.positionUs);
                    if (newPositionUs != this.playbackInfo.positionUs) {
                        this.playbackInfo = new PlaybackInfo(index, newPositionUs);
                        this.eventHandler.obtainMessage(4, this.playbackInfo).sendToTarget();
                        return;
                    }
                    return;
                }
            }
        } else if (this.loadingPeriodHolder != null) {
            index = this.timeline.getIndexOfPeriod(this.loadingPeriodHolder.uid);
            if (index == -1) {
                attemptRestart(this.timeline, oldTimeline, this.loadingPeriodHolder.index);
                return;
            }
            this.loadingPeriodHolder.setIndex(this.timeline, this.timeline.getWindow(this.timeline.getPeriod(index, this.period).windowIndex, this.window), index);
        }
        if (oldTimeline != null) {
            int newPlayingIndex = this.playingPeriodHolder != null ? this.playingPeriodHolder.index : this.loadingPeriodHolder != null ? this.loadingPeriodHolder.index : -1;
            if (newPlayingIndex != -1 && newPlayingIndex != this.playbackInfo.periodIndex) {
                this.playbackInfo = new PlaybackInfo(newPlayingIndex, this.playbackInfo.positionUs);
                updatePlaybackPositions();
                this.eventHandler.obtainMessage(4, this.playbackInfo).sendToTarget();
            }
        }
    }

    private void attemptRestart(Timeline newTimeline, Timeline oldTimeline, int oldPeriodIndex) throws ExoPlaybackException {
        int newPeriodIndex = -1;
        while (newPeriodIndex == -1 && oldPeriodIndex < oldTimeline.getPeriodCount() - 1) {
            oldPeriodIndex++;
            newPeriodIndex = newTimeline.getIndexOfPeriod(oldTimeline.getPeriod(oldPeriodIndex, this.period, true).uid);
        }
        if (newPeriodIndex == -1) {
            stopInternal();
            return;
        }
        releasePeriodHoldersFrom(this.playingPeriodHolder != null ? this.playingPeriodHolder : this.loadingPeriodHolder);
        this.bufferAheadPeriodCount = 0;
        this.playingPeriodHolder = null;
        this.readingPeriodHolder = null;
        this.loadingPeriodHolder = null;
        Pair<Integer, Long> defaultPosition = getDefaultPosition(newPeriodIndex);
        this.playbackInfo = new PlaybackInfo(((Integer) defaultPosition.first).intValue(), ((Long) defaultPosition.second).longValue());
        this.eventHandler.obtainMessage(4, this.playbackInfo).sendToTarget();
    }

    private Pair<Integer, Long> getDefaultPosition(int periodIndex) {
        this.timeline.getPeriod(periodIndex, this.period);
        this.timeline.getWindow(this.period.windowIndex, this.window);
        periodIndex = this.window.firstPeriodIndex;
        long periodPositionUs = this.window.getPositionInFirstPeriodUs() + this.window.getDefaultPositionUs();
        this.timeline.getPeriod(periodIndex, this.period);
        while (periodIndex < this.window.lastPeriodIndex && periodPositionUs > this.period.getDurationMs()) {
            periodPositionUs -= this.period.getDurationUs();
            int periodIndex2 = periodIndex + 1;
            this.timeline.getPeriod(periodIndex, this.period);
            periodIndex = periodIndex2;
        }
        return Pair.create(Integer.valueOf(periodIndex), Long.valueOf(periodPositionUs));
    }

    private void updatePeriods() throws ExoPlaybackException, IOException {
        if (this.timeline == null) {
            this.mediaSource.maybeThrowSourceInfoRefreshError();
            return;
        }
        if (this.loadingPeriodHolder == null || (this.loadingPeriodHolder.isFullyBuffered() && !this.loadingPeriodHolder.isLast && this.bufferAheadPeriodCount < MAXIMUM_BUFFER_AHEAD_PERIODS)) {
            int newLoadingPeriodIndex = this.loadingPeriodHolder == null ? this.playbackInfo.periodIndex : this.loadingPeriodHolder.index + 1;
            if (newLoadingPeriodIndex >= this.timeline.getPeriodCount()) {
                this.mediaSource.maybeThrowSourceInfoRefreshError();
            } else {
                int windowIndex = this.timeline.getPeriod(newLoadingPeriodIndex, this.period).windowIndex;
                long periodStartPositionUs = this.loadingPeriodHolder == null ? this.playbackInfo.positionUs : newLoadingPeriodIndex == this.timeline.getWindow(windowIndex, this.window).firstPeriodIndex ? C.TIME_UNSET : 0;
                if (periodStartPositionUs == C.TIME_UNSET) {
                    Pair<Integer, Long> defaultPosition = getDefaultPosition(newLoadingPeriodIndex);
                    newLoadingPeriodIndex = ((Integer) defaultPosition.first).intValue();
                    periodStartPositionUs = ((Long) defaultPosition.second).longValue();
                }
                Object newPeriodUid = this.timeline.getPeriod(newLoadingPeriodIndex, this.period, true).uid;
                MediaPeriod newMediaPeriod = this.mediaSource.createPeriod(newLoadingPeriodIndex, this.loadControl.getAllocator(), periodStartPositionUs);
                newMediaPeriod.prepare(this);
                MediaPeriodHolder<T> newPeriodHolder = new MediaPeriodHolder(this.renderers, this.rendererCapabilities, this.trackSelector, this.mediaSource, newMediaPeriod, newPeriodUid, periodStartPositionUs);
                this.timeline.getWindow(windowIndex, this.window);
                newPeriodHolder.setIndex(this.timeline, this.window, newLoadingPeriodIndex);
                if (this.loadingPeriodHolder != null) {
                    this.loadingPeriodHolder.setNext(newPeriodHolder);
                    newPeriodHolder.rendererPositionOffsetUs = this.loadingPeriodHolder.rendererPositionOffsetUs + this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
                }
                this.bufferAheadPeriodCount++;
                this.loadingPeriodHolder = newPeriodHolder;
                setIsLoading(true);
            }
        }
        if (this.loadingPeriodHolder == null || this.loadingPeriodHolder.isFullyBuffered()) {
            setIsLoading(false);
        } else if (this.loadingPeriodHolder != null && this.loadingPeriodHolder.needsContinueLoading) {
            maybeContinueLoading();
        }
        if (this.playingPeriodHolder != null) {
            while (this.playingPeriodHolder != this.readingPeriodHolder && this.playingPeriodHolder.next != null && this.rendererPositionUs >= this.playingPeriodHolder.next.rendererPositionOffsetUs) {
                this.playingPeriodHolder.release();
                setPlayingPeriodHolder(this.playingPeriodHolder.next);
                this.bufferAheadPeriodCount--;
                this.playbackInfo = new PlaybackInfo(this.playingPeriodHolder.index, this.playingPeriodHolder.startPositionUs);
                updatePlaybackPositions();
                this.eventHandler.obtainMessage(4, this.playbackInfo).sendToTarget();
            }
            updateTimelineState();
            int i;
            int length;
            Renderer renderer;
            if (this.readingPeriodHolder.isLast) {
                for (Renderer renderer2 : this.enabledRenderers) {
                    renderer2.setCurrentStreamIsFinal();
                }
                return;
            }
            Renderer[] rendererArr = this.enabledRenderers;
            length = rendererArr.length;
            i = 0;
            while (i < length) {
                if (rendererArr[i].hasReadStreamToEnd()) {
                    i++;
                } else {
                    return;
                }
            }
            if (this.readingPeriodHolder.next != null && this.readingPeriodHolder.next.prepared) {
                TrackSelections<T> oldTrackSelections = this.readingPeriodHolder.trackSelections;
                this.readingPeriodHolder = this.readingPeriodHolder.next;
                TrackSelections<T> newTrackSelections = this.readingPeriodHolder.trackSelections;
                for (int i2 = 0; i2 < this.renderers.length; i2++) {
                    renderer2 = this.renderers[i2];
                    TrackSelection oldSelection = oldTrackSelections.get(i2);
                    TrackSelection newSelection = newTrackSelections.get(i2);
                    if (oldSelection != null) {
                        if (newSelection != null) {
                            Format[] formats = new Format[newSelection.length()];
                            for (int j = 0; j < formats.length; j++) {
                                formats[j] = newSelection.getFormat(j);
                            }
                            renderer2.replaceStream(formats, this.readingPeriodHolder.sampleStreams[i2], this.readingPeriodHolder.rendererPositionOffsetUs);
                        } else {
                            renderer2.setCurrentStreamIsFinal();
                        }
                    }
                }
            }
        }
    }

    private void handlePeriodPrepared(MediaPeriod period) throws ExoPlaybackException {
        if (this.loadingPeriodHolder != null && this.loadingPeriodHolder.mediaPeriod == period) {
            this.loadingPeriodHolder.handlePrepared(this.loadingPeriodHolder.startPositionUs, this.loadControl);
            if (this.playingPeriodHolder == null) {
                this.readingPeriodHolder = this.loadingPeriodHolder;
                setPlayingPeriodHolder(this.readingPeriodHolder);
                if (this.playbackInfo.startPositionUs == C.TIME_UNSET) {
                    this.playbackInfo = new PlaybackInfo(this.playingPeriodHolder.index, this.playingPeriodHolder.startPositionUs);
                    resetRendererPosition(this.playbackInfo.startPositionUs);
                    updatePlaybackPositions();
                    this.eventHandler.obtainMessage(4, this.playbackInfo).sendToTarget();
                }
                updateTimelineState();
            }
            maybeContinueLoading();
        }
    }

    private void handleContinueLoadingRequested(MediaPeriod period) {
        if (this.loadingPeriodHolder != null && this.loadingPeriodHolder.mediaPeriod == period) {
            maybeContinueLoading();
        }
    }

    private void maybeContinueLoading() {
        long nextLoadPositionUs = this.loadingPeriodHolder.mediaPeriod.getNextLoadPositionUs();
        if (nextLoadPositionUs != Long.MIN_VALUE) {
            long loadingPeriodPositionUs = this.rendererPositionUs - this.loadingPeriodHolder.rendererPositionOffsetUs;
            boolean continueLoading = this.loadControl.shouldContinueLoading(nextLoadPositionUs - loadingPeriodPositionUs);
            setIsLoading(continueLoading);
            if (continueLoading) {
                this.loadingPeriodHolder.needsContinueLoading = false;
                this.loadingPeriodHolder.mediaPeriod.continueLoading(loadingPeriodPositionUs);
                return;
            }
            this.loadingPeriodHolder.needsContinueLoading = true;
            return;
        }
        setIsLoading(false);
    }

    private void releasePeriodHoldersFrom(MediaPeriodHolder<T> periodHolder) {
        while (periodHolder != null) {
            periodHolder.release();
            periodHolder = periodHolder.next;
        }
    }

    private void setPlayingPeriodHolder(MediaPeriodHolder<T> periodHolder) throws ExoPlaybackException {
        int enabledRendererCount = 0;
        boolean[] rendererWasEnabledFlags = new boolean[this.renderers.length];
        for (int i = 0; i < this.renderers.length; i++) {
            Renderer renderer = this.renderers[i];
            rendererWasEnabledFlags[i] = renderer.getState() != 0;
            if (periodHolder.trackSelections.get(i) != null) {
                enabledRendererCount++;
            } else if (rendererWasEnabledFlags[i]) {
                if (renderer == this.rendererMediaClockSource) {
                    this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
                    this.rendererMediaClock = null;
                    this.rendererMediaClockSource = null;
                }
                ensureStopped(renderer);
                renderer.disable();
            }
        }
        this.trackSelector.onSelectionActivated(periodHolder.trackSelections);
        this.playingPeriodHolder = periodHolder;
        enableRenderers(rendererWasEnabledFlags, enabledRendererCount);
    }

    private void updateTimelineState() {
        long playingPeriodDurationUs = this.timeline.getPeriod(this.playingPeriodHolder.index, this.period).getDurationUs();
        boolean z = playingPeriodDurationUs == C.TIME_UNSET || this.playbackInfo.positionUs < playingPeriodDurationUs || (this.playingPeriodHolder.next != null && this.playingPeriodHolder.next.prepared);
        this.isTimelineReady = z;
        this.isTimelineEnded = this.playingPeriodHolder.isLast;
    }

    private void enableRenderers(boolean[] rendererWasEnabledFlags, int enabledRendererCount) throws ExoPlaybackException {
        this.enabledRenderers = new Renderer[enabledRendererCount];
        enabledRendererCount = 0;
        for (int i = 0; i < this.renderers.length; i++) {
            Renderer renderer = this.renderers[i];
            TrackSelection newSelection = this.playingPeriodHolder.trackSelections.get(i);
            if (newSelection != null) {
                int enabledRendererCount2 = enabledRendererCount + 1;
                this.enabledRenderers[enabledRendererCount] = renderer;
                if (renderer.getState() == 0) {
                    boolean playing = this.playWhenReady && this.state == 3;
                    boolean joining = !rendererWasEnabledFlags[i] && playing;
                    Format[] formats = new Format[newSelection.length()];
                    for (int j = 0; j < formats.length; j++) {
                        formats[j] = newSelection.getFormat(j);
                    }
                    renderer.enable(formats, this.playingPeriodHolder.sampleStreams[i], this.rendererPositionUs, joining, this.playingPeriodHolder.rendererPositionOffsetUs);
                    MediaClock mediaClock = renderer.getMediaClock();
                    if (mediaClock != null) {
                        if (this.rendererMediaClock != null) {
                            throw ExoPlaybackException.createForUnexpected(new IllegalStateException("Multiple renderer media clocks enabled."));
                        }
                        this.rendererMediaClock = mediaClock;
                        this.rendererMediaClockSource = renderer;
                    }
                    if (playing) {
                        renderer.start();
                    }
                }
                enabledRendererCount = enabledRendererCount2;
            }
        }
    }
}
