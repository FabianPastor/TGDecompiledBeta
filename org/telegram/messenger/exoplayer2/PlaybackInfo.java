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

    public PlaybackInfo(Timeline timeline, long startPositionUs, TrackSelectorResult trackSelectorResult) {
        this(timeline, null, new MediaPeriodId(0), startPositionUs, C.TIME_UNSET, 1, false, trackSelectorResult);
    }

    public PlaybackInfo(Timeline timeline, Object manifest, MediaPeriodId periodId, long startPositionUs, long contentPositionUs, int playbackState, boolean isLoading, TrackSelectorResult trackSelectorResult) {
        this.timeline = timeline;
        this.manifest = manifest;
        this.periodId = periodId;
        this.startPositionUs = startPositionUs;
        this.contentPositionUs = contentPositionUs;
        this.positionUs = startPositionUs;
        this.bufferedPositionUs = startPositionUs;
        this.playbackState = playbackState;
        this.isLoading = isLoading;
        this.trackSelectorResult = trackSelectorResult;
    }

    public PlaybackInfo fromNewPosition(int periodIndex, long startPositionUs, long contentPositionUs) {
        return fromNewPosition(new MediaPeriodId(periodIndex), startPositionUs, contentPositionUs);
    }

    public PlaybackInfo fromNewPosition(MediaPeriodId periodId, long startPositionUs, long contentPositionUs) {
        return new PlaybackInfo(this.timeline, this.manifest, periodId, startPositionUs, contentPositionUs, this.playbackState, this.isLoading, this.trackSelectorResult);
    }

    public PlaybackInfo copyWithPeriodIndex(int periodIndex) {
        PlaybackInfo playbackInfo = new PlaybackInfo(this.timeline, this.manifest, this.periodId.copyWithPeriodIndex(periodIndex), this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, this.trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    public PlaybackInfo copyWithTimeline(Timeline timeline, Object manifest) {
        PlaybackInfo playbackInfo = new PlaybackInfo(timeline, manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, this.trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    public PlaybackInfo copyWithPlaybackState(int playbackState) {
        PlaybackInfo playbackInfo = new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, playbackState, this.isLoading, this.trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    public PlaybackInfo copyWithIsLoading(boolean isLoading) {
        PlaybackInfo playbackInfo = new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, isLoading, this.trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    public PlaybackInfo copyWithTrackSelectorResult(TrackSelectorResult trackSelectorResult) {
        PlaybackInfo playbackInfo = new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, trackSelectorResult);
        copyMutablePositions(this, playbackInfo);
        return playbackInfo;
    }

    private static void copyMutablePositions(PlaybackInfo from, PlaybackInfo to) {
        to.positionUs = from.positionUs;
        to.bufferedPositionUs = from.bufferedPositionUs;
    }
}
