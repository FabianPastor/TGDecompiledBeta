package org.telegram.messenger.exoplayer2;

import android.util.Pair;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectorResult;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class MediaPeriodQueue {
    private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
    private int length;
    private MediaPeriodHolder loading;
    private final Period period = new Period();
    private MediaPeriodHolder playing;
    private MediaPeriodHolder reading;
    private int repeatMode;
    private boolean shuffleModeEnabled;
    private Timeline timeline;
    private final Window window = new Window();

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public void setRepeatMode(int i) {
        this.repeatMode = i;
    }

    public void setShuffleModeEnabled(boolean z) {
        this.shuffleModeEnabled = z;
    }

    public boolean isLoading(MediaPeriod mediaPeriod) {
        return (this.loading == null || this.loading.mediaPeriod != mediaPeriod) ? null : true;
    }

    public void reevaluateBuffer(long j) {
        if (this.loading != null) {
            this.loading.reevaluateBuffer(j);
        }
    }

    public boolean shouldLoadNextMediaPeriod() {
        if (this.loading != null) {
            if (this.loading.info.isFinal || !this.loading.isFullyBuffered() || this.loading.info.durationUs == C0542C.TIME_UNSET || this.length >= MAXIMUM_BUFFER_AHEAD_PERIODS) {
                return false;
            }
        }
        return true;
    }

    public MediaPeriodInfo getNextMediaPeriodInfo(long j, PlaybackInfo playbackInfo) {
        if (this.loading == null) {
            return getFirstMediaPeriodInfo(playbackInfo);
        }
        return getFollowingMediaPeriodInfo(this.loading.info, this.loading.getRendererOffset(), j);
    }

    public MediaPeriod enqueueNextMediaPeriod(RendererCapabilities[] rendererCapabilitiesArr, long j, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, Object obj, MediaPeriodInfo mediaPeriodInfo) {
        MediaPeriodInfo mediaPeriodInfo2;
        long j2;
        if (this.loading == null) {
            mediaPeriodInfo2 = mediaPeriodInfo;
            j2 = mediaPeriodInfo2.startPositionUs + j;
        } else {
            mediaPeriodInfo2 = mediaPeriodInfo;
            j2 = r0.loading.getRendererOffset() + r0.loading.info.durationUs;
        }
        MediaPeriodHolder mediaPeriodHolder = new MediaPeriodHolder(rendererCapabilitiesArr, j2, trackSelector, allocator, mediaSource, obj, mediaPeriodInfo2);
        if (r0.loading != null) {
            Assertions.checkState(hasPlayingPeriod());
            r0.loading.next = mediaPeriodHolder;
        }
        r0.loading = mediaPeriodHolder;
        r0.length++;
        return mediaPeriodHolder.mediaPeriod;
    }

    public TrackSelectorResult handleLoadingPeriodPrepared(float f) throws ExoPlaybackException {
        return this.loading.handlePrepared(f);
    }

    public MediaPeriodHolder getLoadingPeriod() {
        return this.loading;
    }

    public MediaPeriodHolder getPlayingPeriod() {
        return this.playing;
    }

    public MediaPeriodHolder getReadingPeriod() {
        return this.reading;
    }

    public MediaPeriodHolder getFrontPeriod() {
        return hasPlayingPeriod() ? this.playing : this.loading;
    }

    public boolean hasPlayingPeriod() {
        return this.playing != null;
    }

    public MediaPeriodHolder advanceReadingPeriod() {
        boolean z = (this.reading == null || this.reading.next == null) ? false : true;
        Assertions.checkState(z);
        this.reading = this.reading.next;
        return this.reading;
    }

    public MediaPeriodHolder advancePlayingPeriod() {
        if (this.playing != null) {
            if (this.playing == this.reading) {
                this.reading = this.playing.next;
            }
            this.playing.release();
            this.playing = this.playing.next;
            this.length--;
            if (this.length == 0) {
                this.loading = null;
            }
        } else {
            this.playing = this.loading;
            this.reading = this.loading;
        }
        return this.playing;
    }

    public boolean removeAfter(MediaPeriodHolder mediaPeriodHolder) {
        boolean z = false;
        Assertions.checkState(mediaPeriodHolder != null);
        this.loading = mediaPeriodHolder;
        while (mediaPeriodHolder.next != null) {
            mediaPeriodHolder = mediaPeriodHolder.next;
            if (mediaPeriodHolder == this.reading) {
                this.reading = this.playing;
                z = true;
            }
            mediaPeriodHolder.release();
            this.length--;
        }
        this.loading.next = null;
        return z;
    }

    public void clear() {
        MediaPeriodHolder frontPeriod = getFrontPeriod();
        if (frontPeriod != null) {
            frontPeriod.release();
            removeAfter(frontPeriod);
        }
        this.playing = null;
        this.loading = null;
        this.reading = null;
        this.length = 0;
    }

    public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo mediaPeriodInfo) {
        return getUpdatedMediaPeriodInfo(mediaPeriodInfo, mediaPeriodInfo.id);
    }

    public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo mediaPeriodInfo, int i) {
        return getUpdatedMediaPeriodInfo(mediaPeriodInfo, mediaPeriodInfo.id.copyWithPeriodIndex(i));
    }

    public MediaPeriodId resolveMediaPeriodIdForAds(int i, long j) {
        this.timeline.getPeriod(i, this.period);
        j = this.period.getAdGroupIndexForPositionUs(j);
        if (j == -1) {
            return new MediaPeriodId(i);
        }
        return new MediaPeriodId(i, j, this.period.getNextAdIndexToPlay(j));
    }

    private MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo playbackInfo) {
        return getMediaPeriodInfo(playbackInfo.periodId, playbackInfo.contentPositionUs, playbackInfo.startPositionUs);
    }

    private MediaPeriodInfo getFollowingMediaPeriodInfo(MediaPeriodInfo mediaPeriodInfo, long j, long j2) {
        MediaPeriodQueue mediaPeriodQueue = this;
        MediaPeriodInfo mediaPeriodInfo2 = mediaPeriodInfo;
        MediaPeriodInfo mediaPeriodInfo3 = null;
        long j3;
        if (mediaPeriodInfo2.isLastInTimelinePeriod) {
            int nextPeriodIndex = mediaPeriodQueue.timeline.getNextPeriodIndex(mediaPeriodInfo2.id.periodIndex, mediaPeriodQueue.period, mediaPeriodQueue.window, mediaPeriodQueue.repeatMode, mediaPeriodQueue.shuffleModeEnabled);
            if (nextPeriodIndex == -1) {
                return null;
            }
            int i = mediaPeriodQueue.timeline.getPeriod(nextPeriodIndex, mediaPeriodQueue.period).windowIndex;
            j3 = 0;
            if (mediaPeriodQueue.timeline.getWindow(i, mediaPeriodQueue.window).firstPeriodIndex == nextPeriodIndex) {
                long j4 = (j + mediaPeriodInfo2.durationUs) - j2;
                Pair periodPosition = mediaPeriodQueue.timeline.getPeriodPosition(mediaPeriodQueue.window, mediaPeriodQueue.period, i, C0542C.TIME_UNSET, Math.max(0, j4));
                if (periodPosition == null) {
                    return null;
                }
                nextPeriodIndex = ((Integer) periodPosition.first).intValue();
                j3 = ((Long) periodPosition.second).longValue();
            }
            return getMediaPeriodInfo(resolveMediaPeriodIdForAds(nextPeriodIndex, j3), j3, j3);
        }
        MediaPeriodId mediaPeriodId = mediaPeriodInfo2.id;
        int adCountInAdGroup;
        if (mediaPeriodId.isAd()) {
            int i2 = mediaPeriodId.adGroupIndex;
            mediaPeriodQueue.timeline.getPeriod(mediaPeriodId.periodIndex, mediaPeriodQueue.period);
            adCountInAdGroup = mediaPeriodQueue.period.getAdCountInAdGroup(i2);
            if (adCountInAdGroup == -1) {
                return null;
            }
            int i3 = mediaPeriodId.adIndexInAdGroup + 1;
            if (i3 < adCountInAdGroup) {
                if (mediaPeriodQueue.period.isAdAvailable(i2, i3)) {
                    mediaPeriodInfo3 = getMediaPeriodInfoForAd(mediaPeriodId.periodIndex, i2, i3, mediaPeriodInfo2.contentPositionUs);
                }
                return mediaPeriodInfo3;
            }
            int adGroupIndexAfterPositionUs = mediaPeriodQueue.period.getAdGroupIndexAfterPositionUs(mediaPeriodInfo2.contentPositionUs);
            if (adGroupIndexAfterPositionUs == -1) {
                j3 = Long.MIN_VALUE;
            } else {
                j3 = mediaPeriodQueue.period.getAdGroupTimeUs(adGroupIndexAfterPositionUs);
            }
            return getMediaPeriodInfoForContent(mediaPeriodId.periodIndex, mediaPeriodInfo2.contentPositionUs, j3);
        } else if (mediaPeriodInfo2.endPositionUs != Long.MIN_VALUE) {
            int adGroupIndexForPositionUs = mediaPeriodQueue.period.getAdGroupIndexForPositionUs(mediaPeriodInfo2.endPositionUs);
            if (mediaPeriodQueue.period.isAdAvailable(adGroupIndexForPositionUs, 0)) {
                mediaPeriodInfo3 = getMediaPeriodInfoForAd(mediaPeriodId.periodIndex, adGroupIndexForPositionUs, 0, mediaPeriodInfo2.endPositionUs);
            }
            return mediaPeriodInfo3;
        } else {
            int adGroupCount = mediaPeriodQueue.period.getAdGroupCount();
            if (adGroupCount != 0) {
                adCountInAdGroup = adGroupCount - 1;
                if (mediaPeriodQueue.period.getAdGroupTimeUs(adCountInAdGroup) == Long.MIN_VALUE && !mediaPeriodQueue.period.hasPlayedAdGroup(adCountInAdGroup)) {
                    if (mediaPeriodQueue.period.isAdAvailable(adCountInAdGroup, 0)) {
                        return getMediaPeriodInfoForAd(mediaPeriodId.periodIndex, adCountInAdGroup, 0, mediaPeriodQueue.period.getDurationUs());
                    }
                }
            }
            return null;
        }
    }

    private MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo mediaPeriodInfo, MediaPeriodId mediaPeriodId) {
        long adDurationUs;
        long j;
        long j2 = mediaPeriodInfo.startPositionUs;
        long j3 = mediaPeriodInfo.endPositionUs;
        boolean isLastInPeriod = isLastInPeriod(mediaPeriodId, j3);
        boolean isLastInTimeline = isLastInTimeline(mediaPeriodId, isLastInPeriod);
        this.timeline.getPeriod(mediaPeriodId.periodIndex, this.period);
        if (mediaPeriodId.isAd()) {
            adDurationUs = this.period.getAdDurationUs(mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup);
        } else if (j3 == Long.MIN_VALUE) {
            adDurationUs = this.period.getDurationUs();
        } else {
            j = j3;
            return new MediaPeriodInfo(mediaPeriodId, j2, j3, mediaPeriodInfo.contentPositionUs, j, isLastInPeriod, isLastInTimeline);
        }
        j = adDurationUs;
        return new MediaPeriodInfo(mediaPeriodId, j2, j3, mediaPeriodInfo.contentPositionUs, j, isLastInPeriod, isLastInTimeline);
    }

    private MediaPeriodInfo getMediaPeriodInfo(MediaPeriodId mediaPeriodId, long j, long j2) {
        this.timeline.getPeriod(mediaPeriodId.periodIndex, this.period);
        if (!mediaPeriodId.isAd()) {
            j = this.period.getAdGroupIndexAfterPositionUs(j2);
            if (j == -1) {
                j = Long.MIN_VALUE;
            } else {
                j = this.period.getAdGroupTimeUs(j);
            }
            return getMediaPeriodInfoForContent(mediaPeriodId.periodIndex, j2, j);
        } else if (this.period.isAdAvailable(mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup) == null) {
            return null;
        } else {
            return getMediaPeriodInfoForAd(mediaPeriodId.periodIndex, mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup, j);
        }
    }

    private MediaPeriodInfo getMediaPeriodInfoForAd(int i, int i2, int i3, long j) {
        int i4 = i2;
        int i5 = i3;
        MediaPeriodId mediaPeriodId = new MediaPeriodId(i, i4, i5);
        boolean isLastInPeriod = isLastInPeriod(mediaPeriodId, Long.MIN_VALUE);
        boolean isLastInTimeline = isLastInTimeline(mediaPeriodId, isLastInPeriod);
        return new MediaPeriodInfo(mediaPeriodId, i5 == this.period.getNextAdIndexToPlay(i4) ? r0.period.getAdResumePositionUs() : 0, Long.MIN_VALUE, j, this.timeline.getPeriod(mediaPeriodId.periodIndex, this.period).getAdDurationUs(mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup), isLastInPeriod, isLastInTimeline);
    }

    private MediaPeriodInfo getMediaPeriodInfoForContent(int i, long j, long j2) {
        long j3 = j2;
        MediaPeriodId mediaPeriodId = new MediaPeriodId(i);
        boolean isLastInPeriod = isLastInPeriod(mediaPeriodId, j3);
        boolean isLastInTimeline = isLastInTimeline(mediaPeriodId, isLastInPeriod);
        this.timeline.getPeriod(mediaPeriodId.periodIndex, this.period);
        return new MediaPeriodInfo(mediaPeriodId, j, j3, C0542C.TIME_UNSET, j3 == Long.MIN_VALUE ? r0.period.getDurationUs() : j3, isLastInPeriod, isLastInTimeline);
    }

    private boolean isLastInPeriod(MediaPeriodId mediaPeriodId, long j) {
        int adGroupCount = this.timeline.getPeriod(mediaPeriodId.periodIndex, this.period).getAdGroupCount();
        boolean z = true;
        if (adGroupCount == 0) {
            return true;
        }
        adGroupCount--;
        boolean isAd = mediaPeriodId.isAd();
        if (this.period.getAdGroupTimeUs(adGroupCount) != Long.MIN_VALUE) {
            if (isAd || j != Long.MIN_VALUE) {
                z = false;
            }
            return z;
        }
        j = this.period.getAdCountInAdGroup(adGroupCount);
        if (j == -1) {
            return false;
        }
        mediaPeriodId = (isAd && mediaPeriodId.adGroupIndex == adGroupCount && mediaPeriodId.adIndexInAdGroup == j - 1) ? 1 : null;
        if (mediaPeriodId == null) {
            if (isAd || this.period.getNextAdIndexToPlay(adGroupCount) != j) {
                z = false;
            }
        }
        return z;
    }

    private boolean isLastInTimeline(MediaPeriodId mediaPeriodId, boolean z) {
        return (this.timeline.getWindow(this.timeline.getPeriod(mediaPeriodId.periodIndex, this.period).windowIndex, this.window).isDynamic || this.timeline.isLastPeriod(mediaPeriodId.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled) == null || !z) ? null : true;
    }
}
