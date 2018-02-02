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
import org.telegram.messenger.exoplayer2.ExoPlayer.ExoPlayerMessage;
import org.telegram.messenger.exoplayer2.Player.EventListener;
import org.telegram.messenger.exoplayer2.Player.TextComponent;
import org.telegram.messenger.exoplayer2.Player.VideoComponent;
import org.telegram.messenger.exoplayer2.PlayerMessage.Target;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
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

final class ExoPlayerImpl implements ExoPlayer {
    private static final String TAG = "ExoPlayerImpl";
    private final TrackSelectorResult emptyTrackSelectorResult;
    private final Handler eventHandler;
    private boolean hasPendingPrepare;
    private boolean hasPendingSeek;
    private final ExoPlayerImplInternal internalPlayer;
    private final Handler internalPlayerHandler;
    private final CopyOnWriteArraySet<EventListener> listeners;
    private int maskingPeriodIndex;
    private int maskingWindowIndex;
    private long maskingWindowPositionMs;
    private int pendingOperationAcks;
    private final Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private PlaybackParameters playbackParameters;
    private final Renderer[] renderers;
    private int repeatMode;
    private boolean shuffleModeEnabled;
    private final TrackSelector trackSelector;
    private final Window window;

    @SuppressLint({"HandlerLeak"})
    public ExoPlayerImpl(Renderer[] renderers, TrackSelector trackSelector, LoadControl loadControl, Clock clock) {
        Log.i(TAG, "Init " + Integer.toHexString(System.identityHashCode(this)) + " [" + ExoPlayerLibraryInfo.VERSION_SLASHY + "] [" + Util.DEVICE_DEBUG_INFO + "]");
        Assertions.checkState(renderers.length > 0);
        this.renderers = (Renderer[]) Assertions.checkNotNull(renderers);
        this.trackSelector = (TrackSelector) Assertions.checkNotNull(trackSelector);
        this.playWhenReady = false;
        this.repeatMode = 0;
        this.shuffleModeEnabled = false;
        this.listeners = new CopyOnWriteArraySet();
        this.emptyTrackSelectorResult = new TrackSelectorResult(TrackGroupArray.EMPTY, new boolean[renderers.length], new TrackSelectionArray(new TrackSelection[renderers.length]), null, new RendererConfiguration[renderers.length]);
        this.window = new Window();
        this.period = new Period();
        this.playbackParameters = PlaybackParameters.DEFAULT;
        this.eventHandler = new Handler(Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                ExoPlayerImpl.this.handleEvent(msg);
            }
        };
        this.playbackInfo = new PlaybackInfo(Timeline.EMPTY, 0, this.emptyTrackSelectorResult);
        this.internalPlayer = new ExoPlayerImplInternal(renderers, trackSelector, this.emptyTrackSelectorResult, loadControl, this.playWhenReady, this.repeatMode, this.shuffleModeEnabled, this.eventHandler, this, clock);
        this.internalPlayerHandler = new Handler(this.internalPlayer.getPlaybackLooper());
    }

    public VideoComponent getVideoComponent() {
        return null;
    }

    public TextComponent getTextComponent() {
        return null;
    }

    public Looper getPlaybackLooper() {
        return this.internalPlayer.getPlaybackLooper();
    }

    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        this.listeners.remove(listener);
    }

    public int getPlaybackState() {
        return this.playbackInfo.playbackState;
    }

    public void prepare(MediaSource mediaSource) {
        prepare(mediaSource, true, true);
    }

    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetState) {
        PlaybackInfo playbackInfo = getResetPlaybackInfo(resetPosition, resetState, 2);
        this.hasPendingPrepare = true;
        this.pendingOperationAcks++;
        this.internalPlayer.prepare(mediaSource, resetPosition);
        updatePlaybackInfo(playbackInfo, false, 4, 1, false);
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        if (this.playWhenReady != playWhenReady) {
            this.playWhenReady = playWhenReady;
            this.internalPlayer.setPlayWhenReady(playWhenReady);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onPlayerStateChanged(playWhenReady, this.playbackInfo.playbackState);
            }
        }
    }

    public boolean getPlayWhenReady() {
        return this.playWhenReady;
    }

    public void setRepeatMode(int repeatMode) {
        if (this.repeatMode != repeatMode) {
            this.repeatMode = repeatMode;
            this.internalPlayer.setRepeatMode(repeatMode);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onRepeatModeChanged(repeatMode);
            }
        }
    }

    public int getRepeatMode() {
        return this.repeatMode;
    }

    public void setShuffleModeEnabled(boolean shuffleModeEnabled) {
        if (this.shuffleModeEnabled != shuffleModeEnabled) {
            this.shuffleModeEnabled = shuffleModeEnabled;
            this.internalPlayer.setShuffleModeEnabled(shuffleModeEnabled);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onShuffleModeEnabledChanged(shuffleModeEnabled);
            }
        }
    }

    public boolean getShuffleModeEnabled() {
        return this.shuffleModeEnabled;
    }

    public boolean isLoading() {
        return this.playbackInfo.isLoading;
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
        Timeline timeline = this.playbackInfo.timeline;
        if (windowIndex < 0 || (!timeline.isEmpty() && windowIndex >= timeline.getWindowCount())) {
            throw new IllegalSeekPositionException(timeline, windowIndex, positionMs);
        }
        this.hasPendingSeek = true;
        this.pendingOperationAcks++;
        if (isPlayingAd()) {
            Log.w(TAG, "seekTo ignored because an ad is playing");
            this.eventHandler.obtainMessage(0, 1, -1, this.playbackInfo).sendToTarget();
            return;
        }
        this.maskingWindowIndex = windowIndex;
        if (timeline.isEmpty()) {
            long j;
            if (positionMs == C.TIME_UNSET) {
                j = 0;
            } else {
                j = positionMs;
            }
            this.maskingWindowPositionMs = j;
            this.maskingPeriodIndex = 0;
        } else {
            long windowPositionUs = positionMs == C.TIME_UNSET ? timeline.getWindow(windowIndex, this.window).getDefaultPositionUs() : C.msToUs(positionMs);
            Pair<Integer, Long> periodIndexAndPositon = timeline.getPeriodPosition(this.window, this.period, windowIndex, windowPositionUs);
            this.maskingWindowPositionMs = C.usToMs(windowPositionUs);
            this.maskingPeriodIndex = ((Integer) periodIndexAndPositon.first).intValue();
        }
        this.internalPlayer.seekTo(timeline, windowIndex, C.msToUs(positionMs));
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((EventListener) it.next()).onPositionDiscontinuity(1);
        }
    }

    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        if (playbackParameters == null) {
            playbackParameters = PlaybackParameters.DEFAULT;
        }
        this.internalPlayer.setPlaybackParameters(playbackParameters);
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.playbackParameters;
    }

    public void setSeekParameters(SeekParameters seekParameters) {
        if (seekParameters == null) {
            seekParameters = SeekParameters.DEFAULT;
        }
        this.internalPlayer.setSeekParameters(seekParameters);
    }

    public void stop() {
        stop(false);
    }

    public void stop(boolean reset) {
        PlaybackInfo playbackInfo = getResetPlaybackInfo(reset, reset, 1);
        this.pendingOperationAcks++;
        this.internalPlayer.stop(reset);
        updatePlaybackInfo(playbackInfo, false, 4, 1, false);
    }

    public void release() {
        Log.i(TAG, "Release " + Integer.toHexString(System.identityHashCode(this)) + " [" + ExoPlayerLibraryInfo.VERSION_SLASHY + "] [" + Util.DEVICE_DEBUG_INFO + "] [" + ExoPlayerLibraryInfo.registeredModules() + "]");
        this.internalPlayer.release();
        this.eventHandler.removeCallbacksAndMessages(null);
    }

    public void sendMessages(ExoPlayerMessage... messages) {
        for (ExoPlayerMessage message : messages) {
            createMessage(message.target).setType(message.messageType).setPayload(message.message).send();
        }
    }

    public PlayerMessage createMessage(Target target) {
        return new PlayerMessage(this.internalPlayer, target, this.playbackInfo.timeline, getCurrentWindowIndex(), this.internalPlayerHandler);
    }

    public void blockingSendMessages(ExoPlayerMessage... messages) {
        List<PlayerMessage> playerMessages = new ArrayList();
        for (ExoPlayerMessage message : messages) {
            playerMessages.add(createMessage(message.target).setType(message.messageType).setPayload(message.message).send());
        }
        boolean wasInterrupted = false;
        for (PlayerMessage message2 : playerMessages) {
            boolean blockMessage = true;
            while (blockMessage) {
                try {
                    message2.blockUntilDelivered();
                    blockMessage = false;
                } catch (InterruptedException e) {
                    wasInterrupted = true;
                }
            }
        }
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public int getCurrentPeriodIndex() {
        if (shouldMaskPosition()) {
            return this.maskingPeriodIndex;
        }
        return this.playbackInfo.periodId.periodIndex;
    }

    public int getCurrentWindowIndex() {
        if (shouldMaskPosition()) {
            return this.maskingWindowIndex;
        }
        return this.playbackInfo.timeline.getPeriod(this.playbackInfo.periodId.periodIndex, this.period).windowIndex;
    }

    public int getNextWindowIndex() {
        Timeline timeline = this.playbackInfo.timeline;
        if (timeline.isEmpty()) {
            return -1;
        }
        return timeline.getNextWindowIndex(getCurrentWindowIndex(), this.repeatMode, this.shuffleModeEnabled);
    }

    public int getPreviousWindowIndex() {
        Timeline timeline = this.playbackInfo.timeline;
        if (timeline.isEmpty()) {
            return -1;
        }
        return timeline.getPreviousWindowIndex(getCurrentWindowIndex(), this.repeatMode, this.shuffleModeEnabled);
    }

    public long getDuration() {
        Timeline timeline = this.playbackInfo.timeline;
        if (timeline.isEmpty()) {
            return C.TIME_UNSET;
        }
        if (!isPlayingAd()) {
            return timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
        }
        MediaPeriodId periodId = this.playbackInfo.periodId;
        timeline.getPeriod(periodId.periodIndex, this.period);
        return C.usToMs(this.period.getAdDurationUs(periodId.adGroupIndex, periodId.adIndexInAdGroup));
    }

    public long getCurrentPosition() {
        if (shouldMaskPosition()) {
            return this.maskingWindowPositionMs;
        }
        return playbackInfoPositionUsToWindowPositionMs(this.playbackInfo.positionUs);
    }

    public long getBufferedPosition() {
        if (shouldMaskPosition()) {
            return this.maskingWindowPositionMs;
        }
        return playbackInfoPositionUsToWindowPositionMs(this.playbackInfo.bufferedPositionUs);
    }

    public int getBufferedPercentage() {
        long position = getBufferedPosition();
        long duration = getDuration();
        if (position == C.TIME_UNSET || duration == C.TIME_UNSET) {
            return 0;
        }
        if (duration != 0) {
            return Util.constrainValue((int) ((100 * position) / duration), 0, 100);
        }
        return 100;
    }

    public boolean isCurrentWindowDynamic() {
        Timeline timeline = this.playbackInfo.timeline;
        return !timeline.isEmpty() && timeline.getWindow(getCurrentWindowIndex(), this.window).isDynamic;
    }

    public boolean isCurrentWindowSeekable() {
        Timeline timeline = this.playbackInfo.timeline;
        return !timeline.isEmpty() && timeline.getWindow(getCurrentWindowIndex(), this.window).isSeekable;
    }

    public boolean isPlayingAd() {
        return !shouldMaskPosition() && this.playbackInfo.periodId.isAd();
    }

    public int getCurrentAdGroupIndex() {
        return isPlayingAd() ? this.playbackInfo.periodId.adGroupIndex : -1;
    }

    public int getCurrentAdIndexInAdGroup() {
        return isPlayingAd() ? this.playbackInfo.periodId.adIndexInAdGroup : -1;
    }

    public long getContentPosition() {
        if (!isPlayingAd()) {
            return getCurrentPosition();
        }
        this.playbackInfo.timeline.getPeriod(this.playbackInfo.periodId.periodIndex, this.period);
        return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.contentPositionUs);
    }

    public int getRendererCount() {
        return this.renderers.length;
    }

    public int getRendererType(int index) {
        return this.renderers[index].getTrackType();
    }

    public TrackGroupArray getCurrentTrackGroups() {
        return this.playbackInfo.trackSelectorResult.groups;
    }

    public TrackSelectionArray getCurrentTrackSelections() {
        return this.playbackInfo.trackSelectorResult.selections;
    }

    public Timeline getCurrentTimeline() {
        return this.playbackInfo.timeline;
    }

    public Object getCurrentManifest() {
        return this.playbackInfo.manifest;
    }

    void handleEvent(Message msg) {
        Iterator it;
        switch (msg.what) {
            case 0:
                boolean z;
                PlaybackInfo playbackInfo = (PlaybackInfo) msg.obj;
                int i = msg.arg1;
                if (msg.arg2 != -1) {
                    z = true;
                } else {
                    z = false;
                }
                handlePlaybackInfo(playbackInfo, i, z, msg.arg2);
                return;
            case 1:
                PlaybackParameters playbackParameters = msg.obj;
                if (!this.playbackParameters.equals(playbackParameters)) {
                    this.playbackParameters = playbackParameters;
                    it = this.listeners.iterator();
                    while (it.hasNext()) {
                        ((EventListener) it.next()).onPlaybackParametersChanged(playbackParameters);
                    }
                    return;
                }
                return;
            case 2:
                ExoPlaybackException exception = msg.obj;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onPlayerError(exception);
                }
                return;
            default:
                throw new IllegalStateException();
        }
    }

    private void handlePlaybackInfo(PlaybackInfo playbackInfo, int operationAcks, boolean positionDiscontinuity, int positionDiscontinuityReason) {
        this.pendingOperationAcks -= operationAcks;
        if (this.pendingOperationAcks == 0) {
            if (playbackInfo.timeline == null) {
                playbackInfo = playbackInfo.copyWithTimeline(Timeline.EMPTY, playbackInfo.manifest);
            }
            if (playbackInfo.startPositionUs == C.TIME_UNSET) {
                playbackInfo = playbackInfo.fromNewPosition(playbackInfo.periodId, 0, playbackInfo.contentPositionUs);
            }
            if ((!this.playbackInfo.timeline.isEmpty() || this.hasPendingPrepare) && playbackInfo.timeline.isEmpty()) {
                this.maskingPeriodIndex = 0;
                this.maskingWindowIndex = 0;
                this.maskingWindowPositionMs = 0;
            }
            int timelineChangeReason = this.hasPendingPrepare ? 0 : 2;
            boolean seekProcessed = this.hasPendingSeek;
            this.hasPendingPrepare = false;
            this.hasPendingSeek = false;
            updatePlaybackInfo(playbackInfo, positionDiscontinuity, positionDiscontinuityReason, timelineChangeReason, seekProcessed);
        }
    }

    private PlaybackInfo getResetPlaybackInfo(boolean resetPosition, boolean resetState, int playbackState) {
        if (resetPosition) {
            this.maskingWindowIndex = 0;
            this.maskingPeriodIndex = 0;
            this.maskingWindowPositionMs = 0;
        } else {
            this.maskingWindowIndex = getCurrentWindowIndex();
            this.maskingPeriodIndex = getCurrentPeriodIndex();
            this.maskingWindowPositionMs = getCurrentPosition();
        }
        return new PlaybackInfo(resetState ? Timeline.EMPTY : this.playbackInfo.timeline, resetState ? null : this.playbackInfo.manifest, this.playbackInfo.periodId, this.playbackInfo.startPositionUs, this.playbackInfo.contentPositionUs, playbackState, false, resetState ? this.emptyTrackSelectorResult : this.playbackInfo.trackSelectorResult);
    }

    private void updatePlaybackInfo(PlaybackInfo newPlaybackInfo, boolean positionDiscontinuity, int positionDiscontinuityReason, int timelineChangeReason, boolean seekProcessed) {
        boolean timelineOrManifestChanged;
        boolean playbackStateChanged;
        boolean isLoadingChanged;
        boolean trackSelectorResultChanged;
        Iterator it;
        if (this.playbackInfo.timeline == newPlaybackInfo.timeline && this.playbackInfo.manifest == newPlaybackInfo.manifest) {
            timelineOrManifestChanged = false;
        } else {
            timelineOrManifestChanged = true;
        }
        if (this.playbackInfo.playbackState != newPlaybackInfo.playbackState) {
            playbackStateChanged = true;
        } else {
            playbackStateChanged = false;
        }
        if (this.playbackInfo.isLoading != newPlaybackInfo.isLoading) {
            isLoadingChanged = true;
        } else {
            isLoadingChanged = false;
        }
        if (this.playbackInfo.trackSelectorResult != newPlaybackInfo.trackSelectorResult) {
            trackSelectorResultChanged = true;
        } else {
            trackSelectorResultChanged = false;
        }
        this.playbackInfo = newPlaybackInfo;
        if (timelineOrManifestChanged || timelineChangeReason == 0) {
            it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onTimelineChanged(this.playbackInfo.timeline, this.playbackInfo.manifest, timelineChangeReason);
            }
        }
        if (positionDiscontinuity) {
            it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onPositionDiscontinuity(positionDiscontinuityReason);
            }
        }
        if (trackSelectorResultChanged) {
            this.trackSelector.onSelectionActivated(this.playbackInfo.trackSelectorResult.info);
            it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onTracksChanged(this.playbackInfo.trackSelectorResult.groups, this.playbackInfo.trackSelectorResult.selections);
            }
        }
        if (isLoadingChanged) {
            it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onLoadingChanged(this.playbackInfo.isLoading);
            }
        }
        if (playbackStateChanged) {
            it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onPlayerStateChanged(this.playWhenReady, this.playbackInfo.playbackState);
            }
        }
        if (seekProcessed) {
            it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onSeekProcessed();
            }
        }
    }

    private long playbackInfoPositionUsToWindowPositionMs(long positionUs) {
        long positionMs = C.usToMs(positionUs);
        if (this.playbackInfo.periodId.isAd()) {
            return positionMs;
        }
        this.playbackInfo.timeline.getPeriod(this.playbackInfo.periodId.periodIndex, this.period);
        return positionMs + this.period.getPositionInWindowMs();
    }

    private boolean shouldMaskPosition() {
        return this.playbackInfo.timeline.isEmpty() || this.pendingOperationAcks > 0;
    }
}
