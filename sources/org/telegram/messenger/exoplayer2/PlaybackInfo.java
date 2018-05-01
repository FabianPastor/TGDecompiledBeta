package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;

final class PlaybackInfo {
    public volatile long bufferedPositionUs;
    public final long contentPositionUs;
    public final boolean isLoading;
    public final Object manifest;
    public final MediaPeriodId periodId;
    public final int playbackState;
    public volatile long positionUs;
    public final long startPositionUs;
    public final Timeline timeline;
    public final TrackSelectorResult trackSelectorResult;

    public PlaybackInfo(Timeline timeline, long j, TrackSelectorResult trackSelectorResult) {
        this(timeline, null, new MediaPeriodId(0), j, C0542C.TIME_UNSET, 1, false, trackSelectorResult);
    }

    public PlaybackInfo(Timeline timeline, Object obj, MediaPeriodId mediaPeriodId, long j, long j2, int i, boolean z, TrackSelectorResult trackSelectorResult) {
        this.timeline = timeline;
        this.manifest = obj;
        this.periodId = mediaPeriodId;
        this.startPositionUs = j;
        this.contentPositionUs = j2;
        this.positionUs = j;
        this.bufferedPositionUs = j;
        this.playbackState = i;
        this.isLoading = z;
        this.trackSelectorResult = trackSelectorResult;
    }

    public PlaybackInfo fromNewPosition(int i, long j, long j2) {
        return fromNewPosition(new MediaPeriodId(i), j, j2);
    }

    public PlaybackInfo fromNewPosition(MediaPeriodId mediaPeriodId, long j, long j2) {
        return new PlaybackInfo(this.timeline, this.manifest, mediaPeriodId, j, j2, this.playbackState, this.isLoading, this.trackSelectorResult);
    }

    public PlaybackInfo copyWithPeriodIndex(int i) {
        PlaybackInfo playbackInfo = new PlaybackInfo(this.timeline, this.manifest, this.periodId.copyWithPeriodIndex(i), this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, this.trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    public PlaybackInfo copyWithTimeline(Timeline timeline, Object obj) {
        PlaybackInfo playbackInfo = new PlaybackInfo(timeline, obj, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, this.trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    public PlaybackInfo copyWithPlaybackState(int i) {
        PlaybackInfo playbackInfo = new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, i, this.isLoading, this.trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    public PlaybackInfo copyWithIsLoading(boolean z) {
        PlaybackInfo playbackInfo = new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, z, this.trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    public PlaybackInfo copyWithTrackSelectorResult(TrackSelectorResult trackSelectorResult) {
        PlaybackInfo playbackInfo = new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    private static void copyMutablePositions(PlaybackInfo playbackInfo, PlaybackInfo playbackInfo2) {
        playbackInfo2.positionUs = playbackInfo.positionUs;
        playbackInfo2.bufferedPositionUs = playbackInfo.bufferedPositionUs;
    }
}
