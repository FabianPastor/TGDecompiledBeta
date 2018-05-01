package org.telegram.messenger.exoplayer2;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import java.util.Iterator;
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

    public TextComponent getTextComponent() {
        return null;
    }

    public VideoComponent getVideoComponent() {
        return null;
    }

    @SuppressLint({"HandlerLeak"})
    public ExoPlayerImpl(Renderer[] rendererArr, TrackSelector trackSelector, LoadControl loadControl, Clock clock) {
        Object obj = rendererArr;
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Init ");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(" [");
        stringBuilder.append(ExoPlayerLibraryInfo.VERSION_SLASHY);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("]");
        Log.i(str, stringBuilder.toString());
        Assertions.checkState(obj.length > 0);
        r11.renderers = (Renderer[]) Assertions.checkNotNull(obj);
        r11.trackSelector = (TrackSelector) Assertions.checkNotNull(trackSelector);
        r11.playWhenReady = false;
        r11.repeatMode = 0;
        r11.shuffleModeEnabled = false;
        r11.listeners = new CopyOnWriteArraySet();
        r11.emptyTrackSelectorResult = new TrackSelectorResult(TrackGroupArray.EMPTY, new boolean[obj.length], new TrackSelectionArray(new TrackSelection[obj.length]), null, new RendererConfiguration[obj.length]);
        r11.window = new Window();
        r11.period = new Period();
        r11.playbackParameters = PlaybackParameters.DEFAULT;
        r11.eventHandler = new Handler(Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                ExoPlayerImpl.this.handleEvent(message);
            }
        };
        r11.playbackInfo = new PlaybackInfo(Timeline.EMPTY, 0, r11.emptyTrackSelectorResult);
        r11.internalPlayer = new ExoPlayerImplInternal(obj, trackSelector, r11.emptyTrackSelectorResult, loadControl, r11.playWhenReady, r11.repeatMode, r11.shuffleModeEnabled, r11.eventHandler, r11, clock);
        r11.internalPlayerHandler = new Handler(r11.internalPlayer.getPlaybackLooper());
    }

    public Looper getPlaybackLooper() {
        return this.internalPlayer.getPlaybackLooper();
    }

    public void addListener(EventListener eventListener) {
        this.listeners.add(eventListener);
    }

    public void removeListener(EventListener eventListener) {
        this.listeners.remove(eventListener);
    }

    public int getPlaybackState() {
        return this.playbackInfo.playbackState;
    }

    public void prepare(MediaSource mediaSource) {
        prepare(mediaSource, true, true);
    }

    public void prepare(MediaSource mediaSource, boolean z, boolean z2) {
        PlaybackInfo resetPlaybackInfo = getResetPlaybackInfo(z, z2, 2);
        this.hasPendingPrepare = true;
        this.pendingOperationAcks++;
        this.internalPlayer.prepare(mediaSource, z);
        updatePlaybackInfo(resetPlaybackInfo, false, 4, 1, false);
    }

    public void setPlayWhenReady(boolean z) {
        if (this.playWhenReady != z) {
            this.playWhenReady = z;
            this.internalPlayer.setPlayWhenReady(z);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onPlayerStateChanged(z, this.playbackInfo.playbackState);
            }
        }
    }

    public boolean getPlayWhenReady() {
        return this.playWhenReady;
    }

    public void setRepeatMode(int i) {
        if (this.repeatMode != i) {
            this.repeatMode = i;
            this.internalPlayer.setRepeatMode(i);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onRepeatModeChanged(i);
            }
        }
    }

    public int getRepeatMode() {
        return this.repeatMode;
    }

    public void setShuffleModeEnabled(boolean z) {
        if (this.shuffleModeEnabled != z) {
            this.shuffleModeEnabled = z;
            this.internalPlayer.setShuffleModeEnabled(z);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((EventListener) it.next()).onShuffleModeEnabledChanged(z);
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

    public void seekToDefaultPosition(int i) {
        seekTo(i, C0542C.TIME_UNSET);
    }

    public void seekTo(long j) {
        seekTo(getCurrentWindowIndex(), j);
    }

    public void seekTo(int i, long j) {
        Timeline timeline = this.playbackInfo.timeline;
        if (i >= 0) {
            if (timeline.isEmpty() || i < timeline.getWindowCount()) {
                this.hasPendingSeek = true;
                this.pendingOperationAcks++;
                if (isPlayingAd()) {
                    Log.w(TAG, "seekTo ignored because an ad is playing");
                    this.eventHandler.obtainMessage(0, 1, -1, this.playbackInfo).sendToTarget();
                    return;
                }
                this.maskingWindowIndex = i;
                if (timeline.isEmpty()) {
                    this.maskingWindowPositionMs = j == C0542C.TIME_UNSET ? 0 : j;
                    this.maskingPeriodIndex = 0;
                } else {
                    long defaultPositionUs = j == C0542C.TIME_UNSET ? timeline.getWindow(i, this.window).getDefaultPositionUs() : C0542C.msToUs(j);
                    Pair periodPosition = timeline.getPeriodPosition(this.window, this.period, i, defaultPositionUs);
                    this.maskingWindowPositionMs = C0542C.usToMs(defaultPositionUs);
                    this.maskingPeriodIndex = ((Integer) periodPosition.first).intValue();
                }
                this.internalPlayer.seekTo(timeline, i, C0542C.msToUs(j));
                i = this.listeners.iterator();
                while (i.hasNext() != null) {
                    ((EventListener) i.next()).onPositionDiscontinuity(1);
                }
                return;
            }
        }
        throw new IllegalSeekPositionException(timeline, i, j);
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

    public void stop(boolean z) {
        PlaybackInfo resetPlaybackInfo = getResetPlaybackInfo(z, z, 1);
        this.pendingOperationAcks++;
        this.internalPlayer.stop(z);
        updatePlaybackInfo(resetPlaybackInfo, false, 4, 1, false);
    }

    public void release() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Release ");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(" [");
        stringBuilder.append(ExoPlayerLibraryInfo.VERSION_SLASHY);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("] [");
        stringBuilder.append(ExoPlayerLibraryInfo.registeredModules());
        stringBuilder.append("]");
        Log.i(str, stringBuilder.toString());
        this.internalPlayer.release();
        this.eventHandler.removeCallbacksAndMessages(null);
    }

    public void sendMessages(ExoPlayerMessage... exoPlayerMessageArr) {
        for (ExoPlayerMessage exoPlayerMessage : exoPlayerMessageArr) {
            createMessage(exoPlayerMessage.target).setType(exoPlayerMessage.messageType).setPayload(exoPlayerMessage.message).send();
        }
    }

    public PlayerMessage createMessage(Target target) {
        return new PlayerMessage(this.internalPlayer, target, this.playbackInfo.timeline, getCurrentWindowIndex(), this.internalPlayerHandler);
    }

    public void blockingSendMessages(org.telegram.messenger.exoplayer2.ExoPlayer.ExoPlayerMessage... r8) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r7 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 0;
        r2 = r8.length;
        r3 = r1;
    L_0x0008:
        if (r3 >= r2) goto L_0x0028;
    L_0x000a:
        r4 = r8[r3];
        r5 = r4.target;
        r5 = r7.createMessage(r5);
        r6 = r4.messageType;
        r5 = r5.setType(r6);
        r4 = r4.message;
        r4 = r5.setPayload(r4);
        r4 = r4.send();
        r0.add(r4);
        r3 = r3 + 1;
        goto L_0x0008;
    L_0x0028:
        r8 = r0.iterator();
        r0 = r1;
    L_0x002d:
        r2 = r8.hasNext();
        if (r2 == 0) goto L_0x0047;
    L_0x0033:
        r2 = r8.next();
        r2 = (org.telegram.messenger.exoplayer2.PlayerMessage) r2;
        r3 = 1;
        r4 = r0;
        r0 = r3;
    L_0x003c:
        if (r0 == 0) goto L_0x0045;
    L_0x003e:
        r2.blockUntilDelivered();	 Catch:{ InterruptedException -> 0x0043 }
        r0 = r1;
        goto L_0x003c;
    L_0x0043:
        r4 = r3;
        goto L_0x003c;
    L_0x0045:
        r0 = r4;
        goto L_0x002d;
    L_0x0047:
        if (r0 == 0) goto L_0x0050;
    L_0x0049:
        r8 = java.lang.Thread.currentThread();
        r8.interrupt();
    L_0x0050:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.ExoPlayerImpl.blockingSendMessages(org.telegram.messenger.exoplayer2.ExoPlayer$ExoPlayerMessage[]):void");
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
            return C0542C.TIME_UNSET;
        }
        if (!isPlayingAd()) {
            return timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
        }
        MediaPeriodId mediaPeriodId = this.playbackInfo.periodId;
        timeline.getPeriod(mediaPeriodId.periodIndex, this.period);
        return C0542C.usToMs(this.period.getAdDurationUs(mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup));
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
        long bufferedPosition = getBufferedPosition();
        long duration = getDuration();
        if (bufferedPosition == C0542C.TIME_UNSET) {
            return 0;
        }
        if (duration == C0542C.TIME_UNSET) {
            return 0;
        }
        if (duration == 0) {
            return 100;
        }
        return Util.constrainValue((int) ((bufferedPosition * 100) / duration), 0, 100);
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
        return this.period.getPositionInWindowMs() + C0542C.usToMs(this.playbackInfo.contentPositionUs);
    }

    public int getRendererCount() {
        return this.renderers.length;
    }

    public int getRendererType(int i) {
        return this.renderers[i].getTrackType();
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

    void handleEvent(Message message) {
        Iterator it;
        switch (message.what) {
            case 0:
                handlePlaybackInfo((PlaybackInfo) message.obj, message.arg1, message.arg2 != -1, message.arg2);
                return;
            case 1:
                PlaybackParameters playbackParameters = (PlaybackParameters) message.obj;
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
                ExoPlaybackException exoPlaybackException = (ExoPlaybackException) message.obj;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((EventListener) it.next()).onPlayerError(exoPlaybackException);
                }
                return;
            default:
                throw new IllegalStateException();
        }
    }

    private void handlePlaybackInfo(PlaybackInfo playbackInfo, int i, boolean z, int i2) {
        this.pendingOperationAcks -= i;
        if (this.pendingOperationAcks == 0) {
            if (playbackInfo.timeline == 0) {
                playbackInfo = playbackInfo.copyWithTimeline(Timeline.EMPTY, playbackInfo.manifest);
            }
            PlaybackInfo playbackInfo2 = playbackInfo;
            if (playbackInfo2.startPositionUs == C0542C.TIME_UNSET) {
                playbackInfo2 = playbackInfo2.fromNewPosition(playbackInfo2.periodId, 0, playbackInfo2.contentPositionUs);
            }
            PlaybackInfo playbackInfo3 = playbackInfo2;
            if ((this.playbackInfo.timeline.isEmpty() == null || this.hasPendingPrepare != null) && playbackInfo3.timeline.isEmpty() != null) {
                this.maskingPeriodIndex = 0;
                this.maskingWindowIndex = 0;
                this.maskingWindowPositionMs = 0;
            }
            int i3 = this.hasPendingPrepare != null ? 0 : 2;
            boolean z2 = this.hasPendingSeek;
            this.hasPendingPrepare = false;
            this.hasPendingSeek = false;
            updatePlaybackInfo(playbackInfo3, z, i2, i3, z2);
        }
    }

    private PlaybackInfo getResetPlaybackInfo(boolean z, boolean z2, int i) {
        ExoPlayerImpl exoPlayerImpl = this;
        if (z) {
            exoPlayerImpl.maskingWindowIndex = 0;
            exoPlayerImpl.maskingPeriodIndex = 0;
            exoPlayerImpl.maskingWindowPositionMs = 0;
        } else {
            exoPlayerImpl.maskingWindowIndex = getCurrentWindowIndex();
            exoPlayerImpl.maskingPeriodIndex = getCurrentPeriodIndex();
            exoPlayerImpl.maskingWindowPositionMs = getCurrentPosition();
        }
        return new PlaybackInfo(z2 ? Timeline.EMPTY : exoPlayerImpl.playbackInfo.timeline, z2 ? null : exoPlayerImpl.playbackInfo.manifest, exoPlayerImpl.playbackInfo.periodId, exoPlayerImpl.playbackInfo.startPositionUs, exoPlayerImpl.playbackInfo.contentPositionUs, i, false, z2 ? exoPlayerImpl.emptyTrackSelectorResult : exoPlayerImpl.playbackInfo.trackSelectorResult);
    }

    private void updatePlaybackInfo(PlaybackInfo playbackInfo, boolean z, int i, int i2, boolean z2) {
        Object obj;
        Object obj2;
        Object obj3;
        Object obj4 = 1;
        if (this.playbackInfo.timeline == playbackInfo.timeline) {
            if (this.playbackInfo.manifest == playbackInfo.manifest) {
                obj = null;
                obj2 = this.playbackInfo.playbackState == playbackInfo.playbackState ? 1 : null;
                obj3 = this.playbackInfo.isLoading == playbackInfo.isLoading ? 1 : null;
                if (this.playbackInfo.trackSelectorResult != playbackInfo.trackSelectorResult) {
                    obj4 = null;
                }
                this.playbackInfo = playbackInfo;
                if (obj != null || i2 == 0) {
                    playbackInfo = this.listeners.iterator();
                    while (playbackInfo.hasNext()) {
                        ((EventListener) playbackInfo.next()).onTimelineChanged(this.playbackInfo.timeline, this.playbackInfo.manifest, i2);
                    }
                }
                if (z) {
                    playbackInfo = this.listeners.iterator();
                    while (playbackInfo.hasNext()) {
                        ((EventListener) playbackInfo.next()).onPositionDiscontinuity(i);
                    }
                }
                if (obj4 != null) {
                    this.trackSelector.onSelectionActivated(this.playbackInfo.trackSelectorResult.info);
                    playbackInfo = this.listeners.iterator();
                    while (playbackInfo.hasNext()) {
                        ((EventListener) playbackInfo.next()).onTracksChanged(this.playbackInfo.trackSelectorResult.groups, this.playbackInfo.trackSelectorResult.selections);
                    }
                }
                if (obj3 != null) {
                    playbackInfo = this.listeners.iterator();
                    while (playbackInfo.hasNext()) {
                        ((EventListener) playbackInfo.next()).onLoadingChanged(this.playbackInfo.isLoading);
                    }
                }
                if (obj2 != null) {
                    playbackInfo = this.listeners.iterator();
                    while (playbackInfo.hasNext()) {
                        ((EventListener) playbackInfo.next()).onPlayerStateChanged(this.playWhenReady, this.playbackInfo.playbackState);
                    }
                }
                if (z2) {
                    playbackInfo = this.listeners.iterator();
                    while (playbackInfo.hasNext()) {
                        ((EventListener) playbackInfo.next()).onSeekProcessed();
                    }
                }
            }
        }
        obj = 1;
        if (this.playbackInfo.playbackState == playbackInfo.playbackState) {
        }
        if (this.playbackInfo.isLoading == playbackInfo.isLoading) {
        }
        if (this.playbackInfo.trackSelectorResult != playbackInfo.trackSelectorResult) {
            obj4 = null;
        }
        this.playbackInfo = playbackInfo;
        playbackInfo = this.listeners.iterator();
        while (playbackInfo.hasNext()) {
            ((EventListener) playbackInfo.next()).onTimelineChanged(this.playbackInfo.timeline, this.playbackInfo.manifest, i2);
        }
        if (z) {
            playbackInfo = this.listeners.iterator();
            while (playbackInfo.hasNext()) {
                ((EventListener) playbackInfo.next()).onPositionDiscontinuity(i);
            }
        }
        if (obj4 != null) {
            this.trackSelector.onSelectionActivated(this.playbackInfo.trackSelectorResult.info);
            playbackInfo = this.listeners.iterator();
            while (playbackInfo.hasNext()) {
                ((EventListener) playbackInfo.next()).onTracksChanged(this.playbackInfo.trackSelectorResult.groups, this.playbackInfo.trackSelectorResult.selections);
            }
        }
        if (obj3 != null) {
            playbackInfo = this.listeners.iterator();
            while (playbackInfo.hasNext()) {
                ((EventListener) playbackInfo.next()).onLoadingChanged(this.playbackInfo.isLoading);
            }
        }
        if (obj2 != null) {
            playbackInfo = this.listeners.iterator();
            while (playbackInfo.hasNext()) {
                ((EventListener) playbackInfo.next()).onPlayerStateChanged(this.playWhenReady, this.playbackInfo.playbackState);
            }
        }
        if (z2) {
            playbackInfo = this.listeners.iterator();
            while (playbackInfo.hasNext()) {
                ((EventListener) playbackInfo.next()).onSeekProcessed();
            }
        }
    }

    private long playbackInfoPositionUsToWindowPositionMs(long j) {
        j = C0542C.usToMs(j);
        if (this.playbackInfo.periodId.isAd()) {
            return j;
        }
        this.playbackInfo.timeline.getPeriod(this.playbackInfo.periodId.periodIndex, this.period);
        return j + this.period.getPositionInWindowMs();
    }

    private boolean shouldMaskPosition() {
        if (!this.playbackInfo.timeline.isEmpty()) {
            if (this.pendingOperationAcks <= 0) {
                return false;
            }
        }
        return true;
    }
}
