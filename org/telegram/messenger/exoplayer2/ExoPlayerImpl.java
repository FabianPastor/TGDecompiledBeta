package org.telegram.messenger.exoplayer2;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import org.telegram.messenger.exoplayer2.ExoPlayer.EventListener;
import org.telegram.messenger.exoplayer2.ExoPlayer.ExoPlayerMessage;
import org.telegram.messenger.exoplayer2.ExoPlayerImplInternal.PlaybackInfo;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class ExoPlayerImpl implements ExoPlayer {
    private static final String TAG = "ExoPlayerImpl";
    private final Handler eventHandler;
    private final ExoPlayerImplInternal<?> internalPlayer;
    private boolean isLoading;
    private final CopyOnWriteArraySet<EventListener> listeners;
    private Object manifest;
    private int maskingWindowIndex;
    private long maskingWindowPositionMs;
    private boolean pendingInitialSeek;
    private int pendingSeekAcks;
    private final Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private int playbackState;
    private Timeline timeline;
    private final Window window;

    @SuppressLint({"HandlerLeak"})
    public ExoPlayerImpl(Renderer[] renderers, TrackSelector<?> trackSelector, LoadControl loadControl) {
        Log.i(TAG, "Init 2.0.4");
        Assertions.checkNotNull(renderers);
        Assertions.checkState(renderers.length > 0);
        this.playWhenReady = false;
        this.playbackState = 1;
        this.listeners = new CopyOnWriteArraySet();
        this.window = new Window();
        this.period = new Period();
        this.eventHandler = new Handler() {
            public void handleMessage(Message msg) {
                ExoPlayerImpl.this.handleEvent(msg);
            }
        };
        this.playbackInfo = new PlaybackInfo(0, 0);
        this.internalPlayer = new ExoPlayerImplInternal(renderers, trackSelector, loadControl, this.playWhenReady, this.eventHandler, this.playbackInfo);
    }

    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        this.listeners.remove(listener);
    }

    public int getPlaybackState() {
        return this.playbackState;
    }

    public void prepare(MediaSource mediaSource) {
        prepare(mediaSource, true, true);
    }

    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetTimeline) {
        if (resetTimeline && !(this.timeline == null && this.manifest == null)) {
            this.timeline = null;
            this.manifest = null;
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onTimelineChanged(null, null);
            }
        }
        this.internalPlayer.prepare(mediaSource, resetPosition);
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        if (this.playWhenReady != playWhenReady) {
            this.playWhenReady = playWhenReady;
            this.internalPlayer.setPlayWhenReady(playWhenReady);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onPlayerStateChanged(playWhenReady, this.playbackState);
            }
        }
    }

    public boolean getPlayWhenReady() {
        return this.playWhenReady;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public void seekToDefaultPosition() {
        seekToDefaultPosition(getCurrentWindowIndex());
    }

    public void seekToDefaultPosition(int windowIndex) {
        if (this.timeline == null) {
            this.maskingWindowIndex = windowIndex;
            this.maskingWindowPositionMs = C.TIME_UNSET;
            this.pendingInitialSeek = true;
            return;
        }
        Assertions.checkIndex(windowIndex, 0, this.timeline.getWindowCount());
        this.pendingSeekAcks++;
        this.maskingWindowIndex = windowIndex;
        this.maskingWindowPositionMs = 0;
        this.internalPlayer.seekTo(this.timeline.getWindow(windowIndex, this.window).firstPeriodIndex, C.TIME_UNSET);
    }

    public void seekTo(long positionMs) {
        seekTo(getCurrentWindowIndex(), positionMs);
    }

    public void seekTo(int windowIndex, long positionMs) {
        if (positionMs == C.TIME_UNSET) {
            seekToDefaultPosition(windowIndex);
        } else if (this.timeline == null) {
            this.maskingWindowIndex = windowIndex;
            this.maskingWindowPositionMs = positionMs;
            this.pendingInitialSeek = true;
        } else {
            Assertions.checkIndex(windowIndex, 0, this.timeline.getWindowCount());
            this.pendingSeekAcks++;
            this.maskingWindowIndex = windowIndex;
            this.maskingWindowPositionMs = positionMs;
            this.timeline.getWindow(windowIndex, this.window);
            int periodIndex = this.window.firstPeriodIndex;
            long periodPositionMs = this.window.getPositionInFirstPeriodMs() + positionMs;
            long periodDurationMs = this.timeline.getPeriod(periodIndex, this.period).getDurationMs();
            while (periodDurationMs != C.TIME_UNSET && periodPositionMs >= periodDurationMs && periodIndex < this.window.lastPeriodIndex) {
                periodPositionMs -= periodDurationMs;
                periodIndex++;
                periodDurationMs = this.timeline.getPeriod(periodIndex, this.period).getDurationMs();
            }
            this.internalPlayer.seekTo(periodIndex, C.msToUs(periodPositionMs));
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onPositionDiscontinuity();
            }
        }
    }

    public void stop() {
        this.internalPlayer.stop();
    }

    public void release() {
        this.internalPlayer.release();
        this.eventHandler.removeCallbacksAndMessages(null);
    }

    public void sendMessages(ExoPlayerMessage... messages) {
        this.internalPlayer.sendMessages(messages);
    }

    public void blockingSendMessages(ExoPlayerMessage... messages) {
        this.internalPlayer.blockingSendMessages(messages);
    }

    public int getCurrentPeriodIndex() {
        return this.playbackInfo.periodIndex;
    }

    public int getCurrentWindowIndex() {
        if (this.timeline == null || this.pendingSeekAcks > 0) {
            return this.maskingWindowIndex;
        }
        return this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period).windowIndex;
    }

    public long getDuration() {
        if (this.timeline == null) {
            return C.TIME_UNSET;
        }
        return this.timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
    }

    public long getCurrentPosition() {
        if (this.timeline == null || this.pendingSeekAcks > 0) {
            return this.maskingWindowPositionMs;
        }
        this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period);
        return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.positionUs);
    }

    public long getBufferedPosition() {
        if (this.timeline == null || this.pendingSeekAcks > 0) {
            return this.maskingWindowPositionMs;
        }
        this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period);
        return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.bufferedPositionUs);
    }

    public int getBufferedPercentage() {
        long j = 100;
        if (this.timeline == null) {
            return 0;
        }
        int i;
        long bufferedPosition = getBufferedPosition();
        long duration = getDuration();
        if (bufferedPosition == C.TIME_UNSET || duration == C.TIME_UNSET) {
            i = 0;
        } else {
            if (duration != 0) {
                j = (100 * bufferedPosition) / duration;
            }
            i = (int) j;
        }
        return i;
    }

    public Timeline getCurrentTimeline() {
        return this.timeline;
    }

    public Object getCurrentManifest() {
        return this.manifest;
    }

    void handleEvent(Message msg) {
        Iterator it;
        switch (msg.what) {
            case 1:
                this.playbackState = msg.arg1;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onPlayerStateChanged(this.playWhenReady, this.playbackState);
                }
                return;
            case 2:
                boolean z;
                if (msg.arg1 != 0) {
                    z = true;
                } else {
                    z = false;
                }
                this.isLoading = z;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onLoadingChanged(this.isLoading);
                }
                return;
            case 3:
                int i = this.pendingSeekAcks - 1;
                this.pendingSeekAcks = i;
                if (i == 0) {
                    this.playbackInfo = (PlaybackInfo) msg.obj;
                    it = this.listeners.iterator();
                    while (it.hasNext()) {
                        ((EventListener) it.next()).onPositionDiscontinuity();
                    }
                    return;
                }
                return;
            case 4:
                if (this.pendingSeekAcks == 0) {
                    this.playbackInfo = (PlaybackInfo) msg.obj;
                    it = this.listeners.iterator();
                    while (it.hasNext()) {
                        ((EventListener) it.next()).onPositionDiscontinuity();
                    }
                    return;
                }
                return;
            case 5:
                Pair<Timeline, Object> timelineAndManifest = msg.obj;
                this.timeline = (Timeline) timelineAndManifest.first;
                this.manifest = timelineAndManifest.second;
                if (this.pendingInitialSeek) {
                    this.pendingInitialSeek = false;
                    seekTo(this.maskingWindowIndex, this.maskingWindowPositionMs);
                }
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onTimelineChanged(this.timeline, this.manifest);
                }
                return;
            case 6:
                ExoPlaybackException exception = msg.obj;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onPlayerError(exception);
                }
                return;
            default:
                return;
        }
    }
}
