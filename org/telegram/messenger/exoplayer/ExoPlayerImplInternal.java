package org.telegram.messenger.exoplayer;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer.ExoPlayer.ExoPlayerComponent;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.PriorityHandlerThread;
import org.telegram.messenger.exoplayer.util.TraceUtil;
import org.telegram.messenger.exoplayer.util.Util;

final class ExoPlayerImplInternal implements Callback {
    private static final int IDLE_INTERVAL_MS = 1000;
    private static final int MSG_CUSTOM = 9;
    private static final int MSG_DO_SOME_WORK = 7;
    public static final int MSG_ERROR = 4;
    private static final int MSG_INCREMENTAL_PREPARE = 2;
    private static final int MSG_PREPARE = 1;
    public static final int MSG_PREPARED = 1;
    private static final int MSG_RELEASE = 5;
    private static final int MSG_SEEK_TO = 6;
    private static final int MSG_SET_PLAY_WHEN_READY = 3;
    public static final int MSG_SET_PLAY_WHEN_READY_ACK = 3;
    private static final int MSG_SET_RENDERER_SELECTED_TRACK = 8;
    public static final int MSG_STATE_CHANGED = 2;
    private static final int MSG_STOP = 4;
    private static final int PREPARE_INTERVAL_MS = 10;
    private static final int RENDERING_INTERVAL_MS = 10;
    private static final String TAG = "ExoPlayerImplInternal";
    private volatile long bufferedPositionUs;
    private int customMessagesProcessed = 0;
    private int customMessagesSent = 0;
    private volatile long durationUs;
    private long elapsedRealtimeUs;
    private final List<TrackRenderer> enabledRenderers;
    private final Handler eventHandler;
    private final Handler handler;
    private final HandlerThread internalPlaybackThread;
    private long lastSeekPositionMs;
    private final long minBufferUs;
    private final long minRebufferUs;
    private final AtomicInteger pendingSeekCount;
    private boolean playWhenReady;
    private volatile long positionUs;
    private boolean rebuffering;
    private boolean released;
    private MediaClock rendererMediaClock;
    private TrackRenderer rendererMediaClockSource;
    private TrackRenderer[] renderers;
    private final int[] selectedTrackIndices;
    private final StandaloneMediaClock standaloneMediaClock;
    private int state;
    private final MediaFormat[][] trackFormats;

    public ExoPlayerImplInternal(Handler eventHandler, boolean playWhenReady, int[] selectedTrackIndices, int minBufferMs, int minRebufferMs) {
        this.eventHandler = eventHandler;
        this.playWhenReady = playWhenReady;
        this.minBufferUs = ((long) minBufferMs) * 1000;
        this.minRebufferUs = ((long) minRebufferMs) * 1000;
        this.selectedTrackIndices = Arrays.copyOf(selectedTrackIndices, selectedTrackIndices.length);
        this.state = 1;
        this.durationUs = -1;
        this.bufferedPositionUs = -1;
        this.standaloneMediaClock = new StandaloneMediaClock();
        this.pendingSeekCount = new AtomicInteger();
        this.enabledRenderers = new ArrayList(selectedTrackIndices.length);
        this.trackFormats = new MediaFormat[selectedTrackIndices.length][];
        this.internalPlaybackThread = new PriorityHandlerThread("ExoPlayerImplInternal:Handler", -16);
        this.internalPlaybackThread.start();
        this.handler = new Handler(this.internalPlaybackThread.getLooper(), this);
    }

    public Looper getPlaybackLooper() {
        return this.internalPlaybackThread.getLooper();
    }

    public long getCurrentPosition() {
        return this.pendingSeekCount.get() > 0 ? this.lastSeekPositionMs : this.positionUs / 1000;
    }

    public long getBufferedPosition() {
        return this.bufferedPositionUs == -1 ? -1 : this.bufferedPositionUs / 1000;
    }

    public long getDuration() {
        return this.durationUs == -1 ? -1 : this.durationUs / 1000;
    }

    public void prepare(TrackRenderer... renderers) {
        this.handler.obtainMessage(1, renderers).sendToTarget();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        int i;
        Handler handler = this.handler;
        if (playWhenReady) {
            i = 1;
        } else {
            i = 0;
        }
        handler.obtainMessage(3, i, 0).sendToTarget();
    }

    public void seekTo(long positionMs) {
        this.lastSeekPositionMs = positionMs;
        this.pendingSeekCount.incrementAndGet();
        this.handler.obtainMessage(6, Util.getTopInt(positionMs), Util.getBottomInt(positionMs)).sendToTarget();
    }

    public void stop() {
        this.handler.sendEmptyMessage(4);
    }

    public void setRendererSelectedTrack(int rendererIndex, int trackIndex) {
        this.handler.obtainMessage(8, rendererIndex, trackIndex).sendToTarget();
    }

    public void sendMessage(ExoPlayerComponent target, int messageType, Object message) {
        this.customMessagesSent++;
        this.handler.obtainMessage(9, messageType, 0, Pair.create(target, message)).sendToTarget();
    }

    public synchronized void blockingSendMessage(ExoPlayerComponent target, int messageType, Object message) {
        if (this.released) {
            Log.w(TAG, "Sent message(" + messageType + ") after release. Message ignored.");
        } else {
            int messageNumber = this.customMessagesSent;
            this.customMessagesSent = messageNumber + 1;
            this.handler.obtainMessage(9, messageType, 0, Pair.create(target, message)).sendToTarget();
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

    public boolean handleMessage(Message msg) {
        boolean z = false;
        try {
            switch (msg.what) {
                case 1:
                    prepareInternal((TrackRenderer[]) msg.obj);
                    return true;
                case 2:
                    incrementalPrepareInternal();
                    return true;
                case 3:
                    if (msg.arg1 != 0) {
                        z = true;
                    }
                    setPlayWhenReadyInternal(z);
                    return true;
                case 4:
                    stopInternal();
                    return true;
                case 5:
                    releaseInternal();
                    return true;
                case 6:
                    seekToInternal(Util.getLong(msg.arg1, msg.arg2));
                    return true;
                case 7:
                    doSomeWork();
                    return true;
                case 8:
                    setRendererSelectedTrackInternal(msg.arg1, msg.arg2);
                    return true;
                case 9:
                    sendMessageInternal(msg.arg1, msg.obj);
                    return true;
                default:
                    return false;
            }
        } catch (ExoPlaybackException e) {
            Log.e(TAG, "Internal track renderer error.", e);
            this.eventHandler.obtainMessage(4, e).sendToTarget();
            stopInternal();
            return true;
        } catch (Throwable e2) {
            Log.e(TAG, "Internal runtime error.", e2);
            this.eventHandler.obtainMessage(4, new ExoPlaybackException(e2, true)).sendToTarget();
            stopInternal();
            return true;
        }
    }

    private void setState(int state) {
        if (this.state != state) {
            this.state = state;
            this.eventHandler.obtainMessage(2, state, 0).sendToTarget();
        }
    }

    private void prepareInternal(TrackRenderer[] renderers) throws ExoPlaybackException {
        resetInternal();
        this.renderers = renderers;
        Arrays.fill(this.trackFormats, null);
        setState(2);
        incrementalPrepareInternal();
    }

    private void incrementalPrepareInternal() throws ExoPlaybackException {
        int rendererIndex;
        TrackRenderer renderer;
        long operationStartTimeMs = SystemClock.elapsedRealtime();
        boolean prepared = true;
        for (TrackRenderer renderer2 : this.renderers) {
            if (renderer2.getState() == 0 && renderer2.prepare(this.positionUs) == 0) {
                renderer2.maybeThrowError();
                prepared = false;
            }
        }
        if (prepared) {
            long durationUs = 0;
            boolean allRenderersEnded = true;
            boolean allRenderersReadyOrEnded = true;
            for (rendererIndex = 0; rendererIndex < this.renderers.length; rendererIndex++) {
                int trackIndex;
                renderer2 = this.renderers[rendererIndex];
                int rendererTrackCount = renderer2.getTrackCount();
                MediaFormat[] rendererTrackFormats = new MediaFormat[rendererTrackCount];
                for (trackIndex = 0; trackIndex < rendererTrackCount; trackIndex++) {
                    rendererTrackFormats[trackIndex] = renderer2.getFormat(trackIndex);
                }
                this.trackFormats[rendererIndex] = rendererTrackFormats;
                if (rendererTrackCount > 0) {
                    if (durationUs != -1) {
                        long trackDurationUs = renderer2.getDurationUs();
                        if (trackDurationUs == -1) {
                            durationUs = -1;
                        } else if (trackDurationUs != -2) {
                            durationUs = Math.max(durationUs, trackDurationUs);
                        }
                    }
                    trackIndex = this.selectedTrackIndices[rendererIndex];
                    if (trackIndex >= 0 && trackIndex < rendererTrackFormats.length) {
                        enableRenderer(renderer2, trackIndex, false);
                        allRenderersEnded = allRenderersEnded && renderer2.isEnded();
                        if (allRenderersReadyOrEnded && rendererReadyOrEnded(renderer2)) {
                            allRenderersReadyOrEnded = true;
                        } else {
                            allRenderersReadyOrEnded = false;
                        }
                    }
                }
            }
            this.durationUs = durationUs;
            if (!allRenderersEnded || (durationUs != -1 && durationUs > this.positionUs)) {
                this.state = allRenderersReadyOrEnded ? 4 : 3;
            } else {
                this.state = 5;
            }
            this.eventHandler.obtainMessage(1, this.state, 0, this.trackFormats).sendToTarget();
            if (this.playWhenReady && this.state == 4) {
                startRenderers();
            }
            this.handler.sendEmptyMessage(7);
            return;
        }
        scheduleNextOperation(2, operationStartTimeMs, 10);
    }

    private void enableRenderer(TrackRenderer renderer, int trackIndex, boolean joining) throws ExoPlaybackException {
        renderer.enable(trackIndex, this.positionUs, joining);
        this.enabledRenderers.add(renderer);
        MediaClock mediaClock = renderer.getMediaClock();
        if (mediaClock != null) {
            Assertions.checkState(this.rendererMediaClock == null);
            this.rendererMediaClock = mediaClock;
            this.rendererMediaClockSource = renderer;
        }
    }

    private boolean rendererReadyOrEnded(TrackRenderer renderer) {
        boolean z = false;
        if (renderer.isEnded()) {
            return true;
        }
        if (!renderer.isReady()) {
            return false;
        }
        if (this.state == 4) {
            return true;
        }
        long rendererDurationUs = renderer.getDurationUs();
        long rendererBufferedPositionUs = renderer.getBufferedPositionUs();
        long minBufferDurationUs = this.rebuffering ? this.minRebufferUs : this.minBufferUs;
        if (minBufferDurationUs <= 0 || rendererBufferedPositionUs == -1 || rendererBufferedPositionUs == -3 || rendererBufferedPositionUs >= this.positionUs + minBufferDurationUs || !(rendererDurationUs == -1 || rendererDurationUs == -2 || rendererBufferedPositionUs < rendererDurationUs)) {
            z = true;
        }
        return z;
    }

    private void setPlayWhenReadyInternal(boolean playWhenReady) throws ExoPlaybackException {
        try {
            this.rebuffering = false;
            this.playWhenReady = playWhenReady;
            if (!playWhenReady) {
                stopRenderers();
                updatePositionUs();
            } else if (this.state == 4) {
                startRenderers();
                this.handler.sendEmptyMessage(7);
            } else if (this.state == 3) {
                this.handler.sendEmptyMessage(7);
            }
            this.eventHandler.obtainMessage(3).sendToTarget();
        } catch (Throwable th) {
            this.eventHandler.obtainMessage(3).sendToTarget();
        }
    }

    private void startRenderers() throws ExoPlaybackException {
        this.rebuffering = false;
        this.standaloneMediaClock.start();
        for (int i = 0; i < this.enabledRenderers.size(); i++) {
            ((TrackRenderer) this.enabledRenderers.get(i)).start();
        }
    }

    private void stopRenderers() throws ExoPlaybackException {
        this.standaloneMediaClock.stop();
        for (int i = 0; i < this.enabledRenderers.size(); i++) {
            ensureStopped((TrackRenderer) this.enabledRenderers.get(i));
        }
    }

    private void updatePositionUs() {
        if (this.rendererMediaClock == null || !this.enabledRenderers.contains(this.rendererMediaClockSource) || this.rendererMediaClockSource.isEnded()) {
            this.positionUs = this.standaloneMediaClock.getPositionUs();
        } else {
            this.positionUs = this.rendererMediaClock.getPositionUs();
            this.standaloneMediaClock.setPositionUs(this.positionUs);
        }
        this.elapsedRealtimeUs = SystemClock.elapsedRealtime() * 1000;
    }

    private void doSomeWork() throws ExoPlaybackException {
        TraceUtil.beginSection("doSomeWork");
        long operationStartTimeMs = SystemClock.elapsedRealtime();
        long bufferedPositionUs = this.durationUs != -1 ? this.durationUs : Long.MAX_VALUE;
        boolean allRenderersEnded = true;
        boolean allRenderersReadyOrEnded = true;
        updatePositionUs();
        for (int i = 0; i < this.enabledRenderers.size(); i++) {
            TrackRenderer renderer = (TrackRenderer) this.enabledRenderers.get(i);
            renderer.doSomeWork(this.positionUs, this.elapsedRealtimeUs);
            allRenderersEnded = allRenderersEnded && renderer.isEnded();
            boolean rendererReadyOrEnded = rendererReadyOrEnded(renderer);
            if (!rendererReadyOrEnded) {
                renderer.maybeThrowError();
            }
            allRenderersReadyOrEnded = allRenderersReadyOrEnded && rendererReadyOrEnded;
            if (bufferedPositionUs != -1) {
                long rendererDurationUs = renderer.getDurationUs();
                long rendererBufferedPositionUs = renderer.getBufferedPositionUs();
                if (rendererBufferedPositionUs == -1) {
                    bufferedPositionUs = -1;
                } else if (rendererBufferedPositionUs != -3 && (rendererDurationUs == -1 || rendererDurationUs == -2 || rendererBufferedPositionUs < rendererDurationUs)) {
                    bufferedPositionUs = Math.min(bufferedPositionUs, rendererBufferedPositionUs);
                }
            }
        }
        this.bufferedPositionUs = bufferedPositionUs;
        if (allRenderersEnded && (this.durationUs == -1 || this.durationUs <= this.positionUs)) {
            setState(5);
            stopRenderers();
        } else if (this.state == 3 && allRenderersReadyOrEnded) {
            setState(4);
            if (this.playWhenReady) {
                startRenderers();
            }
        } else if (this.state == 4 && !allRenderersReadyOrEnded) {
            this.rebuffering = this.playWhenReady;
            setState(3);
            stopRenderers();
        }
        this.handler.removeMessages(7);
        if ((this.playWhenReady && this.state == 4) || this.state == 3) {
            scheduleNextOperation(7, operationStartTimeMs, 10);
        } else if (!this.enabledRenderers.isEmpty()) {
            scheduleNextOperation(7, operationStartTimeMs, 1000);
        }
        TraceUtil.endSection();
    }

    private void scheduleNextOperation(int operationType, long thisOperationStartTimeMs, long intervalMs) {
        long nextOperationDelayMs = (thisOperationStartTimeMs + intervalMs) - SystemClock.elapsedRealtime();
        if (nextOperationDelayMs <= 0) {
            this.handler.sendEmptyMessage(operationType);
        } else {
            this.handler.sendEmptyMessageDelayed(operationType, nextOperationDelayMs);
        }
    }

    private void seekToInternal(long positionMs) throws ExoPlaybackException {
        try {
            if (positionMs != this.positionUs / 1000) {
                this.rebuffering = false;
                this.positionUs = positionMs * 1000;
                this.standaloneMediaClock.stop();
                this.standaloneMediaClock.setPositionUs(this.positionUs);
                if (this.state == 1 || this.state == 2) {
                    this.pendingSeekCount.decrementAndGet();
                    return;
                }
                for (int i = 0; i < this.enabledRenderers.size(); i++) {
                    TrackRenderer renderer = (TrackRenderer) this.enabledRenderers.get(i);
                    ensureStopped(renderer);
                    renderer.seekTo(this.positionUs);
                }
                setState(3);
                this.handler.sendEmptyMessage(7);
                this.pendingSeekCount.decrementAndGet();
            }
        } finally {
            this.pendingSeekCount.decrementAndGet();
        }
    }

    private void stopInternal() {
        resetInternal();
        setState(1);
    }

    private void releaseInternal() {
        resetInternal();
        setState(1);
        synchronized (this) {
            this.released = true;
            notifyAll();
        }
    }

    private void resetInternal() {
        this.handler.removeMessages(7);
        this.handler.removeMessages(2);
        this.rebuffering = false;
        this.standaloneMediaClock.stop();
        if (this.renderers != null) {
            for (TrackRenderer renderer : this.renderers) {
                stopAndDisable(renderer);
                release(renderer);
            }
            this.renderers = null;
            this.rendererMediaClock = null;
            this.rendererMediaClockSource = null;
            this.enabledRenderers.clear();
        }
    }

    private void stopAndDisable(TrackRenderer renderer) {
        try {
            ensureDisabled(renderer);
        } catch (ExoPlaybackException e) {
            Log.e(TAG, "Stop failed.", e);
        } catch (RuntimeException e2) {
            Log.e(TAG, "Stop failed.", e2);
        }
    }

    private void release(TrackRenderer renderer) {
        try {
            renderer.release();
        } catch (ExoPlaybackException e) {
            Log.e(TAG, "Release failed.", e);
        } catch (RuntimeException e2) {
            Log.e(TAG, "Release failed.", e2);
        }
    }

    private <T> void sendMessageInternal(int what, Object obj) throws ExoPlaybackException {
        try {
            Pair<ExoPlayerComponent, Object> targetAndMessage = (Pair) obj;
            ((ExoPlayerComponent) targetAndMessage.first).handleMessage(what, targetAndMessage.second);
            if (!(this.state == 1 || this.state == 2)) {
                this.handler.sendEmptyMessage(7);
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

    private void setRendererSelectedTrackInternal(int rendererIndex, int trackIndex) throws ExoPlaybackException {
        boolean joining = true;
        if (this.selectedTrackIndices[rendererIndex] != trackIndex) {
            this.selectedTrackIndices[rendererIndex] = trackIndex;
            if (this.state != 1 && this.state != 2) {
                TrackRenderer renderer = this.renderers[rendererIndex];
                int rendererState = renderer.getState();
                if (rendererState != 0 && rendererState != -1 && renderer.getTrackCount() != 0) {
                    boolean isEnabled;
                    boolean shouldEnable;
                    if (rendererState == 2 || rendererState == 3) {
                        isEnabled = true;
                    } else {
                        isEnabled = false;
                    }
                    if (trackIndex < 0 || trackIndex >= this.trackFormats[rendererIndex].length) {
                        shouldEnable = false;
                    } else {
                        shouldEnable = true;
                    }
                    if (isEnabled) {
                        if (!shouldEnable && renderer == this.rendererMediaClockSource) {
                            this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
                        }
                        ensureDisabled(renderer);
                        this.enabledRenderers.remove(renderer);
                    }
                    if (shouldEnable) {
                        boolean playing;
                        if (this.playWhenReady && this.state == 4) {
                            playing = true;
                        } else {
                            playing = false;
                        }
                        if (isEnabled || !playing) {
                            joining = false;
                        }
                        enableRenderer(renderer, trackIndex, joining);
                        if (playing) {
                            renderer.start();
                        }
                        this.handler.sendEmptyMessage(7);
                    }
                }
            }
        }
    }

    private void ensureStopped(TrackRenderer renderer) throws ExoPlaybackException {
        if (renderer.getState() == 3) {
            renderer.stop();
        }
    }

    private void ensureDisabled(TrackRenderer renderer) throws ExoPlaybackException {
        ensureStopped(renderer);
        if (renderer.getState() == 2) {
            renderer.disable();
            if (renderer == this.rendererMediaClockSource) {
                this.rendererMediaClock = null;
                this.rendererMediaClockSource = null;
            }
        }
    }
}
