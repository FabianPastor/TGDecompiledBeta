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
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MediaClock;
import org.telegram.messenger.exoplayer2.util.PriorityHandlerThread;
import org.telegram.messenger.exoplayer2.util.StandaloneMediaClock;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExoPlayerImplInternal implements Callback, MediaPeriod.Callback, InvalidationListener, Listener {
    private static final int IDLE_INTERVAL_MS = 1000;
    private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
    private static final int MSG_CUSTOM = 10;
    private static final int MSG_DO_SOME_WORK = 2;
    public static final int MSG_ERROR = 7;
    public static final int MSG_LOADING_CHANGED = 2;
    private static final int MSG_PERIOD_PREPARED = 7;
    public static final int MSG_POSITION_DISCONTINUITY = 5;
    private static final int MSG_PREPARE = 0;
    private static final int MSG_REFRESH_SOURCE_INFO = 6;
    private static final int MSG_RELEASE = 5;
    public static final int MSG_SEEK_ACK = 4;
    private static final int MSG_SEEK_TO = 3;
    private static final int MSG_SET_PLAY_WHEN_READY = 1;
    private static final int MSG_SOURCE_CONTINUE_LOADING_REQUESTED = 8;
    public static final int MSG_SOURCE_INFO_REFRESHED = 6;
    public static final int MSG_STATE_CHANGED = 1;
    private static final int MSG_STOP = 4;
    public static final int MSG_TRACKS_CHANGED = 3;
    private static final int MSG_TRACK_SELECTION_INVALIDATED = 9;
    private static final int PREPARING_SOURCE_INTERVAL_MS = 10;
    private static final int RENDERING_INTERVAL_MS = 10;
    private static final String TAG = "ExoPlayerImplInternal";
    private int customMessagesProcessed;
    private int customMessagesSent;
    private long elapsedRealtimeUs;
    private Renderer[] enabledRenderers;
    private final Handler eventHandler;
    private final Handler handler;
    private final HandlerThread internalPlaybackThread;
    private boolean isLoading;
    private final LoadControl loadControl;
    private MediaPeriodHolder loadingPeriodHolder;
    private MediaSource mediaSource;
    private int pendingInitialSeekCount;
    private SeekPosition pendingSeekPosition;
    private final Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private MediaPeriodHolder playingPeriodHolder;
    private MediaPeriodHolder readingPeriodHolder;
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
    private final TrackSelector trackSelector;
    private final Window window;

    private static final class MediaPeriodHolder {
        public boolean hasEnabledTracks;
        public int index;
        public boolean isLast;
        private final LoadControl loadControl;
        public final boolean[] mayRetainStreamFlags;
        public final MediaPeriod mediaPeriod;
        private final MediaSource mediaSource;
        public boolean needsContinueLoading;
        public MediaPeriodHolder next;
        private TrackSelectionArray periodTrackSelections;
        public boolean prepared;
        private final RendererCapabilities[] rendererCapabilities;
        public final long rendererPositionOffsetUs;
        private final Renderer[] renderers;
        public final SampleStream[] sampleStreams;
        public long startPositionUs;
        private TrackGroupArray trackGroups;
        private TrackSelectionArray trackSelections;
        private Object trackSelectionsInfo;
        private final TrackSelector trackSelector;
        public final Object uid;

        public MediaPeriodHolder(Renderer[] renderers, RendererCapabilities[] rendererCapabilities, long rendererPositionOffsetUs, TrackSelector trackSelector, LoadControl loadControl, MediaSource mediaSource, Object periodUid, int periodIndex, boolean isLastPeriod, long startPositionUs) {
            this.renderers = renderers;
            this.rendererCapabilities = rendererCapabilities;
            this.rendererPositionOffsetUs = rendererPositionOffsetUs;
            this.trackSelector = trackSelector;
            this.loadControl = loadControl;
            this.mediaSource = mediaSource;
            this.uid = Assertions.checkNotNull(periodUid);
            this.index = periodIndex;
            this.isLast = isLastPeriod;
            this.startPositionUs = startPositionUs;
            this.sampleStreams = new SampleStream[renderers.length];
            this.mayRetainStreamFlags = new boolean[renderers.length];
            this.mediaPeriod = mediaSource.createPeriod(periodIndex, loadControl.getAllocator(), startPositionUs);
        }

        public long toRendererTime(long periodTimeUs) {
            return getRendererOffset() + periodTimeUs;
        }

        public long toPeriodTime(long rendererTimeUs) {
            return rendererTimeUs - getRendererOffset();
        }

        public long getRendererOffset() {
            return this.rendererPositionOffsetUs - this.startPositionUs;
        }

        public void setIndex(int index, boolean isLast) {
            this.index = index;
            this.isLast = isLast;
        }

        public boolean isFullyBuffered() {
            return this.prepared && (!this.hasEnabledTracks || this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE);
        }

        public void handlePrepared() throws ExoPlaybackException {
            this.prepared = true;
            this.trackGroups = this.mediaPeriod.getTrackGroups();
            selectTracks();
            this.startPositionUs = updatePeriodTrackSelection(this.startPositionUs, false);
        }

        public boolean selectTracks() throws ExoPlaybackException {
            Pair<TrackSelectionArray, Object> selectorResult = this.trackSelector.selectTracks(this.rendererCapabilities, this.trackGroups);
            TrackSelectionArray newTrackSelections = selectorResult.first;
            if (newTrackSelections.equals(this.periodTrackSelections)) {
                return false;
            }
            this.trackSelections = newTrackSelections;
            this.trackSelectionsInfo = selectorResult.second;
            return true;
        }

        public long updatePeriodTrackSelection(long positionUs, boolean forceRecreateStreams) {
            return updatePeriodTrackSelection(positionUs, forceRecreateStreams, new boolean[this.renderers.length]);
        }

        public long updatePeriodTrackSelection(long positionUs, boolean forceRecreateStreams, boolean[] streamResetFlags) {
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
            this.loadControl.onTracksSelected(this.renderers, this.trackGroups, this.trackSelections);
            return positionUs;
        }

        public TrackInfo getTrackInfo() {
            return new TrackInfo(this.trackGroups, this.trackSelections, this.trackSelectionsInfo);
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

        public PlaybackInfo copyWithPeriodIndex(int periodIndex) {
            PlaybackInfo playbackInfo = new PlaybackInfo(periodIndex, this.startPositionUs);
            playbackInfo.positionUs = this.positionUs;
            playbackInfo.bufferedPositionUs = this.bufferedPositionUs;
            return playbackInfo;
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

    public static final class SourceInfo {
        public final Object manifest;
        public final PlaybackInfo playbackInfo;
        public final int seekAcks;
        public final Timeline timeline;

        public SourceInfo(Timeline timeline, Object manifest, PlaybackInfo playbackInfo, int seekAcks) {
            this.timeline = timeline;
            this.manifest = manifest;
            this.playbackInfo = playbackInfo;
            this.seekAcks = seekAcks;
        }
    }

    public static final class TrackInfo {
        public final TrackGroupArray groups;
        public final Object info;
        public final TrackSelectionArray selections;

        public TrackInfo(TrackGroupArray groups, TrackSelectionArray selections, Object info) {
            this.groups = groups;
            this.selections = selections;
            this.info = info;
        }
    }

    public ExoPlayerImplInternal(Renderer[] renderers, TrackSelector trackSelector, LoadControl loadControl, boolean playWhenReady, Handler eventHandler, PlaybackInfo playbackInfo) {
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

    public void seekTo(Timeline timeline, int windowIndex, long positionUs) {
        this.handler.obtainMessage(3, new SeekPosition(timeline, windowIndex, positionUs)).sendToTarget();
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
                    seekToInternal((SeekPosition) msg.obj);
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
            this.eventHandler.obtainMessage(7, e).sendToTarget();
            stopInternal();
            return true;
        } catch (IOException e2) {
            Log.e(TAG, "Source error.", e2);
            this.eventHandler.obtainMessage(7, ExoPlaybackException.createForSource(e2)).sendToTarget();
            stopInternal();
            return true;
        } catch (RuntimeException e3) {
            Log.e(TAG, "Internal runtime error.", e3);
            this.eventHandler.obtainMessage(7, ExoPlaybackException.createForUnexpected(e3)).sendToTarget();
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

    private void prepareInternal(MediaSource mediaSource, boolean resetPosition) {
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
                periodPositionUs = this.playingPeriodHolder.toPeriodTime(this.rendererPositionUs);
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
        if (allRenderersEnded && ((playingPeriodDurationUs == C.TIME_UNSET || playingPeriodDurationUs <= this.playbackInfo.positionUs) && this.playingPeriodHolder.isLast)) {
            setState(4);
            stopRenderers();
        } else if (this.state == 2) {
            boolean isNewlyReady;
            if (this.enabledRenderers.length > 0) {
                if (allRenderersReadyOrEnded) {
                    if (haveSufficientBuffer(this.rebuffering)) {
                        isNewlyReady = true;
                    }
                }
                isNewlyReady = false;
            } else {
                isNewlyReady = isTimelineReady(playingPeriodDurationUs);
            }
            if (isNewlyReady) {
                setState(3);
                if (this.playWhenReady) {
                    startRenderers();
                }
            }
        } else if (this.state == 3) {
            boolean isStillReady;
            if (this.enabledRenderers.length > 0) {
                isStillReady = allRenderersReadyOrEnded;
            } else {
                isStillReady = isTimelineReady(playingPeriodDurationUs);
            }
            if (!isStillReady) {
                this.rebuffering = this.playWhenReady;
                setState(2);
                stopRenderers();
            }
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

    private void seekToInternal(SeekPosition seekPosition) throws ExoPlaybackException {
        if (this.timeline == null) {
            this.pendingInitialSeekCount++;
            this.pendingSeekPosition = seekPosition;
            return;
        }
        Pair<Integer, Long> periodPosition = resolveSeekPosition(seekPosition);
        if (periodPosition == null) {
            stopInternal();
            return;
        }
        int periodIndex = ((Integer) periodPosition.first).intValue();
        long periodPositionUs = ((Long) periodPosition.second).longValue();
        try {
            if (periodIndex != this.playbackInfo.periodIndex || periodPositionUs / 1000 != this.playbackInfo.positionUs / 1000) {
                this.playbackInfo = new PlaybackInfo(periodIndex, seekToPeriodPosition(periodIndex, periodPositionUs));
                this.eventHandler.obtainMessage(4, this.playbackInfo).sendToTarget();
            }
        } finally {
            this.playbackInfo = new PlaybackInfo(periodIndex, periodPositionUs);
            this.eventHandler.obtainMessage(4, this.playbackInfo).sendToTarget();
        }
    }

    private long seekToPeriodPosition(int periodIndex, long periodPositionUs) throws ExoPlaybackException {
        stopRenderers();
        this.rebuffering = false;
        setState(2);
        MediaPeriodHolder newPlayingPeriodHolder = null;
        if (this.playingPeriodHolder != null) {
            MediaPeriodHolder periodHolder = this.playingPeriodHolder;
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
        if (!(this.playingPeriodHolder == newPlayingPeriodHolder && this.playingPeriodHolder == this.readingPeriodHolder)) {
            for (Renderer renderer : this.enabledRenderers) {
                renderer.disable();
            }
            this.enabledRenderers = new Renderer[0];
            this.rendererMediaClock = null;
            this.rendererMediaClockSource = null;
        }
        if (newPlayingPeriodHolder != null) {
            newPlayingPeriodHolder.next = null;
            this.loadingPeriodHolder = newPlayingPeriodHolder;
            this.readingPeriodHolder = newPlayingPeriodHolder;
            setPlayingPeriodHolder(newPlayingPeriodHolder);
            if (this.playingPeriodHolder.hasEnabledTracks) {
                periodPositionUs = this.playingPeriodHolder.mediaPeriod.seekToUs(periodPositionUs);
            }
            resetRendererPosition(periodPositionUs);
            maybeContinueLoading();
        } else {
            this.loadingPeriodHolder = null;
            this.readingPeriodHolder = null;
            this.playingPeriodHolder = null;
            resetRendererPosition(periodPositionUs);
        }
        this.handler.sendEmptyMessage(2);
        return periodPositionUs;
    }

    private void resetRendererPosition(long periodPositionUs) throws ExoPlaybackException {
        if (this.playingPeriodHolder != null) {
            periodPositionUs = this.playingPeriodHolder.toRendererTime(periodPositionUs);
        }
        this.rendererPositionUs = periodPositionUs;
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
        this.loadingPeriodHolder = null;
        this.readingPeriodHolder = null;
        this.playingPeriodHolder = null;
        this.timeline = null;
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
            MediaPeriodHolder periodHolder = this.playingPeriodHolder;
            boolean selectionsChangedForReadPeriod = true;
            while (periodHolder != null && periodHolder.prepared) {
                if (periodHolder.selectTracks()) {
                    if (selectionsChangedForReadPeriod) {
                        boolean recreateStreams = this.readingPeriodHolder != this.playingPeriodHolder;
                        releasePeriodHoldersFrom(this.playingPeriodHolder.next);
                        this.playingPeriodHolder.next = null;
                        this.loadingPeriodHolder = this.playingPeriodHolder;
                        this.readingPeriodHolder = this.playingPeriodHolder;
                        boolean[] streamResetFlags = new boolean[this.renderers.length];
                        long periodPositionUs = this.playingPeriodHolder.updatePeriodTrackSelection(this.playbackInfo.positionUs, recreateStreams, streamResetFlags);
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
                                    renderer.resetPosition(this.rendererPositionUs);
                                }
                            }
                        }
                        this.eventHandler.obtainMessage(3, periodHolder.getTrackInfo()).sendToTarget();
                        enableRenderers(rendererWasEnabledFlags, enabledRendererCount);
                    } else {
                        this.loadingPeriodHolder = periodHolder;
                        for (periodHolder = this.loadingPeriodHolder.next; periodHolder != null; periodHolder = periodHolder.next) {
                            periodHolder.release();
                        }
                        this.loadingPeriodHolder.next = null;
                        if (this.loadingPeriodHolder.prepared) {
                            this.loadingPeriodHolder.updatePeriodTrackSelection(Math.max(this.loadingPeriodHolder.startPositionUs, this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs)), false);
                        }
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

    private boolean isTimelineReady(long playingPeriodDurationUs) {
        return playingPeriodDurationUs == C.TIME_UNSET || this.playbackInfo.positionUs < playingPeriodDurationUs || (this.playingPeriodHolder.next != null && this.playingPeriodHolder.next.prepared);
    }

    private boolean haveSufficientBuffer(boolean rebuffering) {
        if (this.loadingPeriodHolder == null) {
            return false;
        }
        long loadingPeriodBufferedPositionUs;
        if (this.loadingPeriodHolder.prepared) {
            loadingPeriodBufferedPositionUs = this.loadingPeriodHolder.mediaPeriod.getBufferedPositionUs();
        } else {
            loadingPeriodBufferedPositionUs = this.loadingPeriodHolder.startPositionUs;
        }
        if (loadingPeriodBufferedPositionUs == Long.MIN_VALUE) {
            if (this.loadingPeriodHolder.isLast) {
                return true;
            }
            loadingPeriodBufferedPositionUs = this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
        }
        return this.loadControl.shouldStartPlayback(loadingPeriodBufferedPositionUs - this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs), rebuffering);
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

    private void handleSourceInfoRefreshed(Pair<Timeline, Object> timelineAndManifest) throws ExoPlaybackException {
        Timeline oldTimeline = this.timeline;
        this.timeline = (Timeline) timelineAndManifest.first;
        Object manifest = timelineAndManifest.second;
        int processedInitialSeekCount = 0;
        if (oldTimeline == null) {
            if (this.pendingInitialSeekCount > 0) {
                Pair<Integer, Long> periodPosition = resolveSeekPosition(this.pendingSeekPosition);
                if (periodPosition == null) {
                    notifySourceInfoRefresh(manifest, 0);
                    stopInternal();
                    return;
                }
                this.playbackInfo = new PlaybackInfo(((Integer) periodPosition.first).intValue(), ((Long) periodPosition.second).longValue());
                processedInitialSeekCount = this.pendingInitialSeekCount;
                this.pendingInitialSeekCount = 0;
                this.pendingSeekPosition = null;
            } else if (this.playbackInfo.startPositionUs == C.TIME_UNSET) {
                Pair<Integer, Long> defaultPosition = getPeriodPosition(0, C.TIME_UNSET);
                this.playbackInfo = new PlaybackInfo(((Integer) defaultPosition.first).intValue(), ((Long) defaultPosition.second).longValue());
            }
        }
        MediaPeriodHolder periodHolder = this.playingPeriodHolder != null ? this.playingPeriodHolder : this.loadingPeriodHolder;
        if (periodHolder == null) {
            notifySourceInfoRefresh(manifest, processedInitialSeekCount);
            return;
        }
        int periodIndex = this.timeline.getIndexOfPeriod(periodHolder.uid);
        if (periodIndex == -1) {
            int newPeriodIndex = resolveSubsequentPeriod(periodHolder.index, oldTimeline, this.timeline);
            if (newPeriodIndex == -1) {
                notifySourceInfoRefresh(manifest, processedInitialSeekCount);
                stopInternal();
                return;
            }
            defaultPosition = getPeriodPosition(this.timeline.getPeriod(newPeriodIndex, this.period).windowIndex, C.TIME_UNSET);
            newPeriodIndex = ((Integer) defaultPosition.first).intValue();
            long newPositionUs = ((Long) defaultPosition.second).longValue();
            this.timeline.getPeriod(newPeriodIndex, this.period, true);
            Object newPeriodUid = this.period.uid;
            periodHolder.index = -1;
            while (periodHolder.next != null) {
                periodHolder = periodHolder.next;
                periodHolder.index = periodHolder.uid.equals(newPeriodUid) ? newPeriodIndex : -1;
            }
            this.playbackInfo = new PlaybackInfo(newPeriodIndex, seekToPeriodPosition(newPeriodIndex, newPositionUs));
            notifySourceInfoRefresh(manifest, processedInitialSeekCount);
            return;
        }
        this.timeline.getPeriod(periodIndex, this.period);
        boolean isLastPeriod = periodIndex == this.timeline.getPeriodCount() + -1 && !this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic;
        periodHolder.setIndex(periodIndex, isLastPeriod);
        boolean seenReadingPeriod = periodHolder == this.readingPeriodHolder;
        if (periodIndex != this.playbackInfo.periodIndex) {
            this.playbackInfo = this.playbackInfo.copyWithPeriodIndex(periodIndex);
        }
        while (periodHolder.next != null) {
            MediaPeriodHolder previousPeriodHolder = periodHolder;
            periodHolder = periodHolder.next;
            periodIndex++;
            this.timeline.getPeriod(periodIndex, this.period, true);
            isLastPeriod = periodIndex == this.timeline.getPeriodCount() + -1 && !this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic;
            if (periodHolder.uid.equals(this.period.uid)) {
                int i;
                periodHolder.setIndex(periodIndex, isLastPeriod);
                if (periodHolder == this.readingPeriodHolder) {
                    i = 1;
                } else {
                    i = 0;
                }
                seenReadingPeriod |= i;
            } else {
                if (seenReadingPeriod) {
                    this.loadingPeriodHolder = previousPeriodHolder;
                    this.loadingPeriodHolder.next = null;
                    releasePeriodHoldersFrom(periodHolder);
                } else {
                    periodIndex = this.playingPeriodHolder.index;
                    this.playbackInfo = new PlaybackInfo(periodIndex, seekToPeriodPosition(periodIndex, this.playbackInfo.positionUs));
                }
                notifySourceInfoRefresh(manifest, processedInitialSeekCount);
            }
        }
        notifySourceInfoRefresh(manifest, processedInitialSeekCount);
    }

    private void notifySourceInfoRefresh(Object manifest, int processedInitialSeekCount) {
        this.eventHandler.obtainMessage(6, new SourceInfo(this.timeline, manifest, this.playbackInfo, processedInitialSeekCount)).sendToTarget();
    }

    private int resolveSubsequentPeriod(int oldPeriodIndex, Timeline oldTimeline, Timeline newTimeline) {
        int newPeriodIndex = -1;
        while (newPeriodIndex == -1 && oldPeriodIndex < oldTimeline.getPeriodCount() - 1) {
            oldPeriodIndex++;
            newPeriodIndex = newTimeline.getIndexOfPeriod(oldTimeline.getPeriod(oldPeriodIndex, this.period, true).uid);
        }
        return newPeriodIndex;
    }

    private Pair<Integer, Long> resolveSeekPosition(SeekPosition seekPosition) {
        Timeline seekTimeline = seekPosition.timeline;
        if (seekTimeline.isEmpty()) {
            seekTimeline = this.timeline;
            Assertions.checkIndex(seekPosition.windowIndex, 0, this.timeline.getWindowCount());
        }
        Pair<Integer, Long> periodPosition = getPeriodPosition(seekTimeline, seekPosition.windowIndex, seekPosition.windowPositionUs);
        if (this.timeline == seekTimeline) {
            return periodPosition;
        }
        int periodIndex = this.timeline.getIndexOfPeriod(seekTimeline.getPeriod(((Integer) periodPosition.first).intValue(), this.period, true).uid);
        if (periodIndex != -1) {
            return Pair.create(Integer.valueOf(periodIndex), periodPosition.second);
        }
        periodIndex = resolveSubsequentPeriod(((Integer) periodPosition.first).intValue(), seekTimeline, this.timeline);
        if (periodIndex != -1) {
            return getPeriodPosition(this.timeline.getPeriod(periodIndex, this.period).windowIndex, C.TIME_UNSET);
        }
        return null;
    }

    private Pair<Integer, Long> getPeriodPosition(int windowIndex, long windowPositionUs) {
        return getPeriodPosition(this.timeline, windowIndex, windowPositionUs);
    }

    private Pair<Integer, Long> getPeriodPosition(Timeline timeline, int windowIndex, long windowPositionUs) {
        return getPeriodPosition(timeline, windowIndex, windowPositionUs, 0);
    }

    private Pair<Integer, Long> getPeriodPosition(Timeline timeline, int windowIndex, long windowPositionUs, long defaultPositionProjectionUs) {
        timeline.getWindow(windowIndex, this.window, false, defaultPositionProjectionUs);
        if (windowPositionUs == C.TIME_UNSET) {
            windowPositionUs = this.window.getDefaultPositionUs();
            if (windowPositionUs == C.TIME_UNSET) {
                return null;
            }
        }
        int periodIndex = this.window.firstPeriodIndex;
        long periodPositionUs = this.window.getPositionInFirstPeriodUs() + windowPositionUs;
        long periodDurationUs = timeline.getPeriod(periodIndex, this.period).getDurationUs();
        while (periodDurationUs != C.TIME_UNSET && periodPositionUs >= periodDurationUs && periodIndex < this.window.lastPeriodIndex) {
            periodPositionUs -= periodDurationUs;
            periodIndex++;
            periodDurationUs = timeline.getPeriod(periodIndex, this.period).getDurationUs();
        }
        return Pair.create(Integer.valueOf(periodIndex), Long.valueOf(periodPositionUs));
    }

    private void updatePeriods() throws ExoPlaybackException, IOException {
        if (this.timeline == null) {
            this.mediaSource.maybeThrowSourceInfoRefreshError();
            return;
        }
        if (this.loadingPeriodHolder == null || (this.loadingPeriodHolder.isFullyBuffered() && !this.loadingPeriodHolder.isLast && (this.playingPeriodHolder == null || this.loadingPeriodHolder.index - this.playingPeriodHolder.index < MAXIMUM_BUFFER_AHEAD_PERIODS))) {
            int newLoadingPeriodIndex = this.loadingPeriodHolder == null ? this.playbackInfo.periodIndex : this.loadingPeriodHolder.index + 1;
            if (newLoadingPeriodIndex >= this.timeline.getPeriodCount()) {
                this.mediaSource.maybeThrowSourceInfoRefreshError();
            } else {
                long periodStartPositionUs;
                int windowIndex = this.timeline.getPeriod(newLoadingPeriodIndex, this.period).windowIndex;
                boolean isFirstPeriodInWindow = newLoadingPeriodIndex == this.timeline.getWindow(windowIndex, this.window).firstPeriodIndex;
                if (this.loadingPeriodHolder == null) {
                    periodStartPositionUs = this.playbackInfo.startPositionUs;
                } else if (isFirstPeriodInWindow) {
                    Pair<Integer, Long> defaultPosition = getPeriodPosition(this.timeline, windowIndex, C.TIME_UNSET, Math.max(0, (this.loadingPeriodHolder.getRendererOffset() + this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs()) - this.rendererPositionUs));
                    if (defaultPosition == null) {
                        newLoadingPeriodIndex = -1;
                        periodStartPositionUs = C.TIME_UNSET;
                    } else {
                        newLoadingPeriodIndex = ((Integer) defaultPosition.first).intValue();
                        periodStartPositionUs = ((Long) defaultPosition.second).longValue();
                    }
                } else {
                    periodStartPositionUs = 0;
                }
                if (newLoadingPeriodIndex != -1) {
                    long rendererPositionOffsetUs;
                    if (this.loadingPeriodHolder == null) {
                        rendererPositionOffsetUs = periodStartPositionUs;
                    } else {
                        rendererPositionOffsetUs = this.loadingPeriodHolder.getRendererOffset() + this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
                    }
                    this.timeline.getPeriod(newLoadingPeriodIndex, this.period, true);
                    boolean isLastPeriod = newLoadingPeriodIndex == this.timeline.getPeriodCount() + -1 && !this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic;
                    MediaPeriodHolder newPeriodHolder = new MediaPeriodHolder(this.renderers, this.rendererCapabilities, rendererPositionOffsetUs, this.trackSelector, this.loadControl, this.mediaSource, this.period.uid, newLoadingPeriodIndex, isLastPeriod, periodStartPositionUs);
                    if (this.loadingPeriodHolder != null) {
                        this.loadingPeriodHolder.next = newPeriodHolder;
                    }
                    this.loadingPeriodHolder = newPeriodHolder;
                    this.loadingPeriodHolder.mediaPeriod.prepare(this);
                    setIsLoading(true);
                }
            }
        }
        if (this.loadingPeriodHolder == null || this.loadingPeriodHolder.isFullyBuffered()) {
            setIsLoading(false);
        } else if (this.loadingPeriodHolder != null && this.loadingPeriodHolder.needsContinueLoading) {
            maybeContinueLoading();
        }
        if (this.playingPeriodHolder != null) {
            while (this.playingPeriodHolder != this.readingPeriodHolder && this.rendererPositionUs >= this.playingPeriodHolder.next.rendererPositionOffsetUs) {
                this.playingPeriodHolder.release();
                setPlayingPeriodHolder(this.playingPeriodHolder.next);
                this.playbackInfo = new PlaybackInfo(this.playingPeriodHolder.index, this.playingPeriodHolder.startPositionUs);
                updatePlaybackPositions();
                this.eventHandler.obtainMessage(5, this.playbackInfo).sendToTarget();
            }
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
                TrackSelectionArray oldTrackSelections = this.readingPeriodHolder.trackSelections;
                this.readingPeriodHolder = this.readingPeriodHolder.next;
                TrackSelectionArray newTrackSelections = this.readingPeriodHolder.trackSelections;
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
                            renderer2.replaceStream(formats, this.readingPeriodHolder.sampleStreams[i2], this.readingPeriodHolder.getRendererOffset());
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
            this.loadingPeriodHolder.handlePrepared();
            if (this.playingPeriodHolder == null) {
                this.readingPeriodHolder = this.loadingPeriodHolder;
                resetRendererPosition(this.readingPeriodHolder.startPositionUs);
                setPlayingPeriodHolder(this.readingPeriodHolder);
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
        if (nextLoadPositionUs == Long.MIN_VALUE) {
            setIsLoading(false);
            return;
        }
        long loadingPeriodPositionUs = this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs);
        boolean continueLoading = this.loadControl.shouldContinueLoading(nextLoadPositionUs - loadingPeriodPositionUs);
        setIsLoading(continueLoading);
        if (continueLoading) {
            this.loadingPeriodHolder.needsContinueLoading = false;
            this.loadingPeriodHolder.mediaPeriod.continueLoading(loadingPeriodPositionUs);
            return;
        }
        this.loadingPeriodHolder.needsContinueLoading = true;
    }

    private void releasePeriodHoldersFrom(MediaPeriodHolder periodHolder) {
        while (periodHolder != null) {
            periodHolder.release();
            periodHolder = periodHolder.next;
        }
    }

    private void setPlayingPeriodHolder(MediaPeriodHolder periodHolder) throws ExoPlaybackException {
        this.playingPeriodHolder = periodHolder;
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
        this.eventHandler.obtainMessage(3, periodHolder.getTrackInfo()).sendToTarget();
        enableRenderers(rendererWasEnabledFlags, enabledRendererCount);
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
                    renderer.enable(formats, this.playingPeriodHolder.sampleStreams[i], this.rendererPositionUs, joining, this.playingPeriodHolder.getRendererOffset());
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
