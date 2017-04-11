package org.telegram.messenger.exoplayer2;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import org.telegram.messenger.exoplayer2.ExoPlayer.EventListener;
import org.telegram.messenger.exoplayer2.ExoPlayer.ExoPlayerMessage;
import org.telegram.messenger.exoplayer2.ExoPlayerImplInternal.PlaybackInfo;
import org.telegram.messenger.exoplayer2.ExoPlayerImplInternal.SourceInfo;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExoPlayerImpl implements ExoPlayer {
    private static final String TAG = "ExoPlayerImpl";
    private final TrackSelectionArray emptyTrackSelections;
    private final Handler eventHandler;
    private final ExoPlayerImplInternal internalPlayer;
    private boolean isLoading;
    private final CopyOnWriteArraySet<EventListener> listeners;
    private Object manifest;
    private int maskingWindowIndex;
    private long maskingWindowPositionMs;
    private int pendingSeekAcks;
    private final Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private int playbackState;
    private final Renderer[] renderers;
    private Timeline timeline;
    private TrackGroupArray trackGroups;
    private TrackSelectionArray trackSelections;
    private final TrackSelector trackSelector;
    private boolean tracksSelected;
    private final Window window;

    @SuppressLint({"HandlerLeak"})
    public ExoPlayerImpl(Renderer[] renderers, TrackSelector trackSelector, LoadControl loadControl) {
        Log.i(TAG, "Init 2.3.1 [" + Util.DEVICE_DEBUG_INFO + "]");
        Assertions.checkState(renderers.length > 0);
        this.renderers = (Renderer[]) Assertions.checkNotNull(renderers);
        this.trackSelector = (TrackSelector) Assertions.checkNotNull(trackSelector);
        this.playWhenReady = false;
        this.playbackState = 1;
        this.listeners = new CopyOnWriteArraySet();
        this.emptyTrackSelections = new TrackSelectionArray(new TrackSelection[renderers.length]);
        this.timeline = Timeline.EMPTY;
        this.window = new Window();
        this.period = new Period();
        this.trackGroups = TrackGroupArray.EMPTY;
        this.trackSelections = this.emptyTrackSelections;
        this.eventHandler = new Handler() {
            public void handleMessage(Message msg) {
                ExoPlayerImpl.this.handleEvent(msg);
            }
        };
        this.playbackInfo = new PlaybackInfo(0, 0);
        this.internalPlayer = new ExoPlayerImplInternal(renderers, trackSelector, loadControl, this.playWhenReady, this.eventHandler, this.playbackInfo, this);
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

    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetState) {
        if (resetState) {
            Iterator it;
            if (!(this.timeline.isEmpty() && this.manifest == null)) {
                this.timeline = Timeline.EMPTY;
                this.manifest = null;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onTimelineChanged(this.timeline, this.manifest);
                }
            }
            if (this.tracksSelected) {
                this.tracksSelected = false;
                this.trackGroups = TrackGroupArray.EMPTY;
                this.trackSelections = this.emptyTrackSelections;
                this.trackSelector.onSelectionActivated(null);
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onTracksChanged(this.trackGroups, this.trackSelections);
                }
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
        seekTo(windowIndex, C.TIME_UNSET);
    }

    public void seekTo(long positionMs) {
        seekTo(getCurrentWindowIndex(), positionMs);
    }

    public void seekTo(int windowIndex, long positionMs) {
        if (windowIndex < 0 || (!this.timeline.isEmpty() && windowIndex >= this.timeline.getWindowCount())) {
            throw new IllegalSeekPositionException(this.timeline, windowIndex, positionMs);
        }
        this.pendingSeekAcks++;
        this.maskingWindowIndex = windowIndex;
        if (positionMs == C.TIME_UNSET) {
            this.maskingWindowPositionMs = 0;
            this.internalPlayer.seekTo(this.timeline, windowIndex, C.TIME_UNSET);
            return;
        }
        this.maskingWindowPositionMs = positionMs;
        this.internalPlayer.seekTo(this.timeline, windowIndex, C.msToUs(positionMs));
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((EventListener) it.next()).onPositionDiscontinuity();
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
        if (this.timeline.isEmpty() || this.pendingSeekAcks > 0) {
            return this.maskingWindowIndex;
        }
        return this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period).windowIndex;
    }

    public long getDuration() {
        if (this.timeline.isEmpty()) {
            return C.TIME_UNSET;
        }
        return this.timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
    }

    public long getCurrentPosition() {
        if (this.timeline.isEmpty() || this.pendingSeekAcks > 0) {
            return this.maskingWindowPositionMs;
        }
        this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period);
        return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.positionUs);
    }

    public long getBufferedPosition() {
        if (this.timeline.isEmpty() || this.pendingSeekAcks > 0) {
            return this.maskingWindowPositionMs;
        }
        this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period);
        return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.bufferedPositionUs);
    }

    public int getBufferedPercentage() {
        long j = 100;
        if (this.timeline.isEmpty()) {
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

    public boolean isCurrentWindowDynamic() {
        if (this.timeline.isEmpty()) {
            return false;
        }
        return this.timeline.getWindow(getCurrentWindowIndex(), this.window).isDynamic;
    }

    public boolean isCurrentWindowSeekable() {
        if (this.timeline.isEmpty()) {
            return false;
        }
        return this.timeline.getWindow(getCurrentWindowIndex(), this.window).isSeekable;
    }

    public int getRendererCount() {
        return this.renderers.length;
    }

    public int getRendererType(int index) {
        return this.renderers[index].getTrackType();
    }

    public TrackGroupArray getCurrentTrackGroups() {
        return this.trackGroups;
    }

    public TrackSelectionArray getCurrentTrackSelections() {
        return this.trackSelections;
    }

    public Timeline getCurrentTimeline() {
        return this.timeline;
    }

    public Object getCurrentManifest() {
        return this.manifest;
    }

    void handleEvent(Message msg) {
        boolean z = true;
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
                if (msg.arg1 == 0) {
                    z = false;
                }
                this.isLoading = z;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onLoadingChanged(this.isLoading);
                }
                return;
            case 3:
                TrackSelectorResult trackSelectorResult = msg.obj;
                this.tracksSelected = true;
                this.trackGroups = trackSelectorResult.groups;
                this.trackSelections = trackSelectorResult.selections;
                this.trackSelector.onSelectionActivated(trackSelectorResult.info);
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onTracksChanged(this.trackGroups, this.trackSelections);
                }
                return;
            case 4:
                int i = this.pendingSeekAcks - 1;
                this.pendingSeekAcks = i;
                if (i == 0) {
                    this.playbackInfo = (PlaybackInfo) msg.obj;
                    if (msg.arg1 != 0) {
                        it = this.listeners.iterator();
                        while (it.hasNext()) {
                            ((EventListener) it.next()).onPositionDiscontinuity();
                        }
                        return;
                    }
                    return;
                }
                return;
            case 5:
                if (this.pendingSeekAcks == 0) {
                    this.playbackInfo = (PlaybackInfo) msg.obj;
                    it = this.listeners.iterator();
                    while (it.hasNext()) {
                        ((EventListener) it.next()).onPositionDiscontinuity();
                    }
                    return;
                }
                return;
            case 6:
                SourceInfo sourceInfo = msg.obj;
                this.timeline = sourceInfo.timeline;
                this.manifest = sourceInfo.manifest;
                this.playbackInfo = sourceInfo.playbackInfo;
                this.pendingSeekAcks -= sourceInfo.seekAcks;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onTimelineChanged(this.timeline, this.manifest);
                }
                return;
            case 7:
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
