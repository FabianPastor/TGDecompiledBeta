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

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public void setShuffleModeEnabled(boolean shuffleModeEnabled) {
        this.shuffleModeEnabled = shuffleModeEnabled;
    }

    public boolean isLoading(MediaPeriod mediaPeriod) {
        return this.loading != null && this.loading.mediaPeriod == mediaPeriod;
    }

    public void reevaluateBuffer(long rendererPositionUs) {
        if (this.loading != null) {
            this.loading.reevaluateBuffer(rendererPositionUs);
        }
    }

    public boolean shouldLoadNextMediaPeriod() {
        if (this.loading != null) {
            if (this.loading.info.isFinal || !this.loading.isFullyBuffered() || this.loading.info.durationUs == C0539C.TIME_UNSET || this.length >= MAXIMUM_BUFFER_AHEAD_PERIODS) {
                return false;
            }
        }
        return true;
    }

    public MediaPeriodInfo getNextMediaPeriodInfo(long rendererPositionUs, PlaybackInfo playbackInfo) {
        if (this.loading == null) {
            return getFirstMediaPeriodInfo(playbackInfo);
        }
        return getFollowingMediaPeriodInfo(this.loading.info, this.loading.getRendererOffset(), rendererPositionUs);
    }

    public MediaPeriod enqueueNextMediaPeriod(RendererCapabilities[] rendererCapabilities, long rendererTimestampOffsetUs, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, Object uid, MediaPeriodInfo info) {
        MediaPeriodInfo mediaPeriodInfo;
        long rendererPositionOffsetUs;
        if (this.loading == null) {
            mediaPeriodInfo = info;
            rendererPositionOffsetUs = mediaPeriodInfo.startPositionUs + rendererTimestampOffsetUs;
        } else {
            mediaPeriodInfo = info;
            rendererPositionOffsetUs = r0.loading.getRendererOffset() + r0.loading.info.durationUs;
        }
        MediaPeriodHolder newPeriodHolder = new MediaPeriodHolder(rendererCapabilities, rendererPositionOffsetUs, trackSelector, allocator, mediaSource, uid, mediaPeriodInfo);
        if (r0.loading != null) {
            Assertions.checkState(hasPlayingPeriod());
            r0.loading.next = newPeriodHolder;
        }
        r0.loading = newPeriodHolder;
        r0.length++;
        return newPeriodHolder.mediaPeriod;
    }

    public TrackSelectorResult handleLoadingPeriodPrepared(float playbackSpeed) throws ExoPlaybackException {
        return this.loading.handlePrepared(playbackSpeed);
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
        Assertions.checkState(mediaPeriodHolder != null);
        boolean removedReading = false;
        this.loading = mediaPeriodHolder;
        while (mediaPeriodHolder.next != null) {
            mediaPeriodHolder = mediaPeriodHolder.next;
            if (mediaPeriodHolder == this.reading) {
                this.reading = this.playing;
                removedReading = true;
            }
            mediaPeriodHolder.release();
            this.length--;
        }
        this.loading.next = null;
        return removedReading;
    }

    public void clear() {
        MediaPeriodHolder front = getFrontPeriod();
        if (front != null) {
            front.release();
            removeAfter(front);
        }
        this.playing = null;
        this.loading = null;
        this.reading = null;
        this.length = 0;
    }

    public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo mediaPeriodInfo) {
        return getUpdatedMediaPeriodInfo(mediaPeriodInfo, mediaPeriodInfo.id);
    }

    public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo mediaPeriodInfo, int newPeriodIndex) {
        return getUpdatedMediaPeriodInfo(mediaPeriodInfo, mediaPeriodInfo.id.copyWithPeriodIndex(newPeriodIndex));
    }

    public MediaPeriodId resolveMediaPeriodIdForAds(int periodIndex, long positionUs) {
        this.timeline.getPeriod(periodIndex, this.period);
        int adGroupIndex = this.period.getAdGroupIndexForPositionUs(positionUs);
        if (adGroupIndex == -1) {
            return new MediaPeriodId(periodIndex);
        }
        return new MediaPeriodId(periodIndex, adGroupIndex, this.period.getNextAdIndexToPlay(adGroupIndex));
    }

    private MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo playbackInfo) {
        return getMediaPeriodInfo(playbackInfo.periodId, playbackInfo.contentPositionUs, playbackInfo.startPositionUs);
    }

    private MediaPeriodInfo getFollowingMediaPeriodInfo(MediaPeriodInfo currentMediaPeriodInfo, long rendererOffsetUs, long rendererPositionUs) {
        MediaPeriodQueue mediaPeriodQueue = this;
        MediaPeriodInfo mediaPeriodInfo = currentMediaPeriodInfo;
        MediaPeriodInfo mediaPeriodInfo2 = null;
        if (mediaPeriodInfo.isLastInTimelinePeriod) {
            int nextPeriodIndex = mediaPeriodQueue.timeline.getNextPeriodIndex(mediaPeriodInfo.id.periodIndex, mediaPeriodQueue.period, mediaPeriodQueue.window, mediaPeriodQueue.repeatMode, mediaPeriodQueue.shuffleModeEnabled);
            if (nextPeriodIndex == -1) {
                return null;
            }
            int nextWindowIndex = mediaPeriodQueue.timeline.getPeriod(nextPeriodIndex, mediaPeriodQueue.period).windowIndex;
            long startPositionUs = 0;
            if (mediaPeriodQueue.timeline.getWindow(nextWindowIndex, mediaPeriodQueue.window).firstPeriodIndex == nextPeriodIndex) {
                long defaultPositionProjectionUs = (rendererOffsetUs + mediaPeriodInfo.durationUs) - rendererPositionUs;
                Pair<Integer, Long> defaultPosition = mediaPeriodQueue.timeline.getPeriodPosition(mediaPeriodQueue.window, mediaPeriodQueue.period, nextWindowIndex, C0539C.TIME_UNSET, Math.max(0, defaultPositionProjectionUs));
                if (defaultPosition == null) {
                    return null;
                }
                nextPeriodIndex = ((Integer) defaultPosition.first).intValue();
                startPositionUs = ((Long) defaultPosition.second).longValue();
            }
            return getMediaPeriodInfo(resolveMediaPeriodIdForAds(nextPeriodIndex, startPositionUs), startPositionUs, startPositionUs);
        }
        MediaPeriodId currentPeriodId = mediaPeriodInfo.id;
        int currentAdGroupIndex;
        if (currentPeriodId.isAd()) {
            currentAdGroupIndex = currentPeriodId.adGroupIndex;
            mediaPeriodQueue.timeline.getPeriod(currentPeriodId.periodIndex, mediaPeriodQueue.period);
            int adCountInCurrentAdGroup = mediaPeriodQueue.period.getAdCountInAdGroup(currentAdGroupIndex);
            if (adCountInCurrentAdGroup == -1) {
                return null;
            }
            int nextAdIndexInAdGroup = currentPeriodId.adIndexInAdGroup + 1;
            if (nextAdIndexInAdGroup < adCountInCurrentAdGroup) {
                if (mediaPeriodQueue.period.isAdAvailable(currentAdGroupIndex, nextAdIndexInAdGroup)) {
                    mediaPeriodInfo2 = getMediaPeriodInfoForAd(currentPeriodId.periodIndex, currentAdGroupIndex, nextAdIndexInAdGroup, mediaPeriodInfo.contentPositionUs);
                }
                return mediaPeriodInfo2;
            }
            int nextAdGroupIndex = mediaPeriodQueue.period.getAdGroupIndexAfterPositionUs(mediaPeriodInfo.contentPositionUs);
            return getMediaPeriodInfoForContent(currentPeriodId.periodIndex, mediaPeriodInfo.contentPositionUs, nextAdGroupIndex == -1 ? Long.MIN_VALUE : mediaPeriodQueue.period.getAdGroupTimeUs(nextAdGroupIndex));
        } else if (mediaPeriodInfo.endPositionUs != Long.MIN_VALUE) {
            currentAdGroupIndex = mediaPeriodQueue.period.getAdGroupIndexForPositionUs(mediaPeriodInfo.endPositionUs);
            if (mediaPeriodQueue.period.isAdAvailable(currentAdGroupIndex, 0)) {
                mediaPeriodInfo2 = getMediaPeriodInfoForAd(currentPeriodId.periodIndex, currentAdGroupIndex, 0, mediaPeriodInfo.endPositionUs);
            }
            return mediaPeriodInfo2;
        } else {
            currentAdGroupIndex = mediaPeriodQueue.period.getAdGroupCount();
            if (!(currentAdGroupIndex == 0 || mediaPeriodQueue.period.getAdGroupTimeUs(currentAdGroupIndex - 1) != Long.MIN_VALUE || mediaPeriodQueue.period.hasPlayedAdGroup(currentAdGroupIndex - 1))) {
                if (mediaPeriodQueue.period.isAdAvailable(currentAdGroupIndex - 1, 0)) {
                    return getMediaPeriodInfoForAd(currentPeriodId.periodIndex, currentAdGroupIndex - 1, 0, mediaPeriodQueue.period.getDurationUs());
                }
            }
            return null;
        }
    }

    private MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo info, MediaPeriodId newId) {
        long adDurationUs;
        long durationUs;
        MediaPeriodInfo mediaPeriodInfo = info;
        MediaPeriodId mediaPeriodId = newId;
        long startPositionUs = mediaPeriodInfo.startPositionUs;
        long endPositionUs = mediaPeriodInfo.endPositionUs;
        boolean isLastInPeriod = isLastInPeriod(mediaPeriodId, endPositionUs);
        boolean isLastInTimeline = isLastInTimeline(mediaPeriodId, isLastInPeriod);
        this.timeline.getPeriod(mediaPeriodId.periodIndex, this.period);
        if (newId.isAd()) {
            adDurationUs = r0.period.getAdDurationUs(mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup);
        } else if (endPositionUs == Long.MIN_VALUE) {
            adDurationUs = r0.period.getDurationUs();
        } else {
            durationUs = endPositionUs;
            return new MediaPeriodInfo(mediaPeriodId, startPositionUs, endPositionUs, mediaPeriodInfo.contentPositionUs, durationUs, isLastInPeriod, isLastInTimeline);
        }
        durationUs = adDurationUs;
        return new MediaPeriodInfo(mediaPeriodId, startPositionUs, endPositionUs, mediaPeriodInfo.contentPositionUs, durationUs, isLastInPeriod, isLastInTimeline);
    }

    private MediaPeriodInfo getMediaPeriodInfo(MediaPeriodId id, long contentPositionUs, long startPositionUs) {
        this.timeline.getPeriod(id.periodIndex, this.period);
        if (!id.isAd()) {
            long j;
            int nextAdGroupIndex = this.period.getAdGroupIndexAfterPositionUs(startPositionUs);
            if (nextAdGroupIndex == -1) {
                j = Long.MIN_VALUE;
            } else {
                j = this.period.getAdGroupTimeUs(nextAdGroupIndex);
            }
            return getMediaPeriodInfoForContent(id.periodIndex, startPositionUs, j);
        } else if (this.period.isAdAvailable(id.adGroupIndex, id.adIndexInAdGroup)) {
            return getMediaPeriodInfoForAd(id.periodIndex, id.adGroupIndex, id.adIndexInAdGroup, contentPositionUs);
        } else {
            return null;
        }
    }

    private MediaPeriodInfo getMediaPeriodInfoForAd(int periodIndex, int adGroupIndex, int adIndexInAdGroup, long contentPositionUs) {
        int i = adGroupIndex;
        int i2 = adIndexInAdGroup;
        MediaPeriodId id = new MediaPeriodId(periodIndex, i, i2);
        boolean isLastInPeriod = isLastInPeriod(id, Long.MIN_VALUE);
        boolean isLastInTimeline = isLastInTimeline(id, isLastInPeriod);
        boolean isLastInPeriod2 = isLastInPeriod;
        return new MediaPeriodInfo(id, i2 == this.period.getNextAdIndexToPlay(i) ? r0.period.getAdResumePositionUs() : 0, Long.MIN_VALUE, contentPositionUs, this.timeline.getPeriod(id.periodIndex, this.period).getAdDurationUs(id.adGroupIndex, id.adIndexInAdGroup), isLastInPeriod, isLastInTimeline);
    }

    private MediaPeriodInfo getMediaPeriodInfoForContent(int periodIndex, long startPositionUs, long endUs) {
        long j = endUs;
        MediaPeriodId id = new MediaPeriodId(periodIndex);
        boolean isLastInPeriod = isLastInPeriod(id, j);
        boolean isLastInTimeline = isLastInTimeline(id, isLastInPeriod);
        this.timeline.getPeriod(id.periodIndex, this.period);
        boolean isLastInPeriod2 = isLastInPeriod;
        return new MediaPeriodInfo(id, startPositionUs, j, C0539C.TIME_UNSET, j == Long.MIN_VALUE ? r0.period.getDurationUs() : j, isLastInPeriod, isLastInTimeline);
    }

    private boolean isLastInPeriod(MediaPeriodId id, long endPositionUs) {
        int adGroupCount = this.timeline.getPeriod(id.periodIndex, this.period).getAdGroupCount();
        boolean z = true;
        if (adGroupCount == 0) {
            return true;
        }
        int lastAdGroupIndex = adGroupCount - 1;
        boolean isAd = id.isAd();
        if (this.period.getAdGroupTimeUs(lastAdGroupIndex) != Long.MIN_VALUE) {
            if (isAd || endPositionUs != Long.MIN_VALUE) {
                z = false;
            }
            return z;
        }
        int postrollAdCount = this.period.getAdCountInAdGroup(lastAdGroupIndex);
        if (postrollAdCount == -1) {
            return false;
        }
        boolean isLastAd = isAd && id.adGroupIndex == lastAdGroupIndex && id.adIndexInAdGroup == postrollAdCount - 1;
        if (!isLastAd) {
            if (isAd || this.period.getNextAdIndexToPlay(lastAdGroupIndex) != postrollAdCount) {
                z = false;
            }
        }
        return z;
    }

    private boolean isLastInTimeline(MediaPeriodId id, boolean isLastMediaPeriodInPeriod) {
        return !this.timeline.getWindow(this.timeline.getPeriod(id.periodIndex, this.period).windowIndex, this.window).isDynamic && this.timeline.isLastPeriod(id.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled) && isLastMediaPeriodInPeriod;
    }
}
