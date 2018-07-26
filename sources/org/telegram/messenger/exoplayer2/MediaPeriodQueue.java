package org.telegram.messenger.exoplayer2;

import android.util.Pair;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class MediaPeriodQueue {
    private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
    private int length;
    private MediaPeriodHolder loading;
    private long nextWindowSequenceNumber;
    private Object oldFrontPeriodUid;
    private long oldFrontPeriodWindowSequenceNumber;
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

    public boolean updateRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
        return updateForPlaybackModeChange();
    }

    public boolean updateShuffleModeEnabled(boolean shuffleModeEnabled) {
        this.shuffleModeEnabled = shuffleModeEnabled;
        return updateForPlaybackModeChange();
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
        return this.loading == null || (!this.loading.info.isFinal && this.loading.isFullyBuffered() && this.loading.info.durationUs != C0559C.TIME_UNSET && this.length < MAXIMUM_BUFFER_AHEAD_PERIODS);
    }

    public MediaPeriodInfo getNextMediaPeriodInfo(long rendererPositionUs, PlaybackInfo playbackInfo) {
        if (this.loading == null) {
            return getFirstMediaPeriodInfo(playbackInfo);
        }
        return getFollowingMediaPeriodInfo(this.loading, rendererPositionUs);
    }

    public MediaPeriod enqueueNextMediaPeriod(RendererCapabilities[] rendererCapabilities, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, Object uid, MediaPeriodInfo info) {
        long rendererPositionOffsetUs;
        if (this.loading == null) {
            rendererPositionOffsetUs = info.startPositionUs;
        } else {
            rendererPositionOffsetUs = this.loading.getRendererOffset() + this.loading.info.durationUs;
        }
        MediaPeriodHolder newPeriodHolder = new MediaPeriodHolder(rendererCapabilities, rendererPositionOffsetUs, trackSelector, allocator, mediaSource, uid, info);
        if (this.loading != null) {
            Assertions.checkState(hasPlayingPeriod());
            this.loading.next = newPeriodHolder;
        }
        this.oldFrontPeriodUid = null;
        this.loading = newPeriodHolder;
        this.length++;
        return newPeriodHolder.mediaPeriod;
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

    public void clear(boolean keepFrontPeriodUid) {
        MediaPeriodHolder front = getFrontPeriod();
        if (front != null) {
            this.oldFrontPeriodUid = keepFrontPeriodUid ? front.uid : null;
            this.oldFrontPeriodWindowSequenceNumber = front.info.id.windowSequenceNumber;
            front.release();
            removeAfter(front);
        } else if (!keepFrontPeriodUid) {
            this.oldFrontPeriodUid = null;
        }
        this.playing = null;
        this.loading = null;
        this.reading = null;
        this.length = 0;
    }

    public boolean updateQueuedPeriods(MediaPeriodId playingPeriodId, long rendererPositionUs) {
        int periodIndex = playingPeriodId.periodIndex;
        MediaPeriodHolder previousPeriodHolder = null;
        MediaPeriodHolder periodHolder = getFrontPeriod();
        while (periodHolder != null) {
            if (previousPeriodHolder == null) {
                periodHolder.info = getUpdatedMediaPeriodInfo(periodHolder.info, periodIndex);
            } else if (periodIndex == -1 || !periodHolder.uid.equals(this.timeline.getPeriod(periodIndex, this.period, true).uid)) {
                boolean z;
                if (removeAfter(previousPeriodHolder)) {
                    z = false;
                } else {
                    z = true;
                }
                return z;
            } else {
                MediaPeriodInfo periodInfo = getFollowingMediaPeriodInfo(previousPeriodHolder, rendererPositionUs);
                if (periodInfo != null) {
                    periodHolder.info = getUpdatedMediaPeriodInfo(periodHolder.info, periodIndex);
                    if (!canKeepMediaPeriodHolder(periodHolder, periodInfo)) {
                        if (removeAfter(previousPeriodHolder)) {
                            return false;
                        }
                        return true;
                    }
                } else if (removeAfter(previousPeriodHolder)) {
                    return false;
                } else {
                    return true;
                }
            }
            if (periodHolder.info.isLastInTimelinePeriod) {
                periodIndex = this.timeline.getNextPeriodIndex(periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            }
            previousPeriodHolder = periodHolder;
            periodHolder = periodHolder.next;
        }
        return true;
    }

    public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo mediaPeriodInfo, int newPeriodIndex) {
        return getUpdatedMediaPeriodInfo(mediaPeriodInfo, mediaPeriodInfo.id.copyWithPeriodIndex(newPeriodIndex));
    }

    public MediaPeriodId resolveMediaPeriodIdForAds(int periodIndex, long positionUs) {
        return resolveMediaPeriodIdForAds(periodIndex, positionUs, resolvePeriodIndexToWindowSequenceNumber(periodIndex));
    }

    private MediaPeriodId resolveMediaPeriodIdForAds(int periodIndex, long positionUs, long windowSequenceNumber) {
        this.timeline.getPeriod(periodIndex, this.period);
        int adGroupIndex = this.period.getAdGroupIndexForPositionUs(positionUs);
        if (adGroupIndex == -1) {
            return new MediaPeriodId(periodIndex, windowSequenceNumber);
        }
        return new MediaPeriodId(periodIndex, adGroupIndex, this.period.getFirstAdIndexToPlay(adGroupIndex), windowSequenceNumber);
    }

    private long resolvePeriodIndexToWindowSequenceNumber(int periodIndex) {
        MediaPeriodHolder mediaPeriodHolder;
        Object periodUid = this.timeline.getPeriod(periodIndex, this.period, true).uid;
        int windowIndex = this.period.windowIndex;
        if (this.oldFrontPeriodUid != null) {
            int oldFrontPeriodIndex = this.timeline.getIndexOfPeriod(this.oldFrontPeriodUid);
            if (oldFrontPeriodIndex != -1 && this.timeline.getPeriod(oldFrontPeriodIndex, this.period).windowIndex == windowIndex) {
                return this.oldFrontPeriodWindowSequenceNumber;
            }
        }
        for (mediaPeriodHolder = getFrontPeriod(); mediaPeriodHolder != null; mediaPeriodHolder = mediaPeriodHolder.next) {
            if (mediaPeriodHolder.uid.equals(periodUid)) {
                return mediaPeriodHolder.info.id.windowSequenceNumber;
            }
        }
        for (mediaPeriodHolder = getFrontPeriod(); mediaPeriodHolder != null; mediaPeriodHolder = mediaPeriodHolder.next) {
            int indexOfHolderInTimeline = this.timeline.getIndexOfPeriod(mediaPeriodHolder.uid);
            if (indexOfHolderInTimeline != -1 && this.timeline.getPeriod(indexOfHolderInTimeline, this.period).windowIndex == windowIndex) {
                return mediaPeriodHolder.info.id.windowSequenceNumber;
            }
        }
        long j = this.nextWindowSequenceNumber;
        this.nextWindowSequenceNumber = 1 + j;
        return j;
    }

    private boolean canKeepMediaPeriodHolder(MediaPeriodHolder periodHolder, MediaPeriodInfo info) {
        MediaPeriodInfo periodHolderInfo = periodHolder.info;
        return periodHolderInfo.startPositionUs == info.startPositionUs && periodHolderInfo.endPositionUs == info.endPositionUs && periodHolderInfo.id.equals(info.id);
    }

    private boolean updateForPlaybackModeChange() {
        MediaPeriodHolder lastValidPeriodHolder = getFrontPeriod();
        if (lastValidPeriodHolder == null) {
            return true;
        }
        boolean readingPeriodRemoved;
        while (true) {
            int nextPeriodIndex = this.timeline.getNextPeriodIndex(lastValidPeriodHolder.info.id.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            while (lastValidPeriodHolder.next != null && !lastValidPeriodHolder.info.isLastInTimelinePeriod) {
                lastValidPeriodHolder = lastValidPeriodHolder.next;
            }
            if (nextPeriodIndex == -1 || lastValidPeriodHolder.next == null || lastValidPeriodHolder.next.info.id.periodIndex != nextPeriodIndex) {
                readingPeriodRemoved = removeAfter(lastValidPeriodHolder);
                lastValidPeriodHolder.info = getUpdatedMediaPeriodInfo(lastValidPeriodHolder.info, lastValidPeriodHolder.info.id);
            } else {
                lastValidPeriodHolder = lastValidPeriodHolder.next;
            }
        }
        readingPeriodRemoved = removeAfter(lastValidPeriodHolder);
        lastValidPeriodHolder.info = getUpdatedMediaPeriodInfo(lastValidPeriodHolder.info, lastValidPeriodHolder.info.id);
        boolean z = (readingPeriodRemoved && hasPlayingPeriod()) ? false : true;
        return z;
    }

    private MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo playbackInfo) {
        return getMediaPeriodInfo(playbackInfo.periodId, playbackInfo.contentPositionUs, playbackInfo.startPositionUs);
    }

    private MediaPeriodInfo getFollowingMediaPeriodInfo(MediaPeriodHolder mediaPeriodHolder, long rendererPositionUs) {
        MediaPeriodInfo mediaPeriodInfo = mediaPeriodHolder.info;
        if (mediaPeriodInfo.isLastInTimelinePeriod) {
            int nextPeriodIndex = this.timeline.getNextPeriodIndex(mediaPeriodInfo.id.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            if (nextPeriodIndex == -1) {
                return null;
            }
            long startPositionUs;
            int nextWindowIndex = this.timeline.getPeriod(nextPeriodIndex, this.period, true).windowIndex;
            Object nextPeriodUid = this.period.uid;
            long windowSequenceNumber = mediaPeriodInfo.id.windowSequenceNumber;
            if (this.timeline.getWindow(nextWindowIndex, this.window).firstPeriodIndex == nextPeriodIndex) {
                Pair<Integer, Long> defaultPosition = this.timeline.getPeriodPosition(this.window, this.period, nextWindowIndex, C0559C.TIME_UNSET, Math.max(0, (mediaPeriodHolder.getRendererOffset() + mediaPeriodInfo.durationUs) - rendererPositionUs));
                if (defaultPosition == null) {
                    return null;
                }
                nextPeriodIndex = ((Integer) defaultPosition.first).intValue();
                startPositionUs = ((Long) defaultPosition.second).longValue();
                if (mediaPeriodHolder.next == null || !mediaPeriodHolder.next.uid.equals(nextPeriodUid)) {
                    windowSequenceNumber = this.nextWindowSequenceNumber;
                    this.nextWindowSequenceNumber = 1 + windowSequenceNumber;
                } else {
                    windowSequenceNumber = mediaPeriodHolder.next.info.id.windowSequenceNumber;
                }
            } else {
                startPositionUs = 0;
            }
            return getMediaPeriodInfo(resolveMediaPeriodIdForAds(nextPeriodIndex, startPositionUs, windowSequenceNumber), startPositionUs, startPositionUs);
        }
        MediaPeriodId currentPeriodId = mediaPeriodInfo.id;
        this.timeline.getPeriod(currentPeriodId.periodIndex, this.period);
        int adGroupIndex;
        if (currentPeriodId.isAd()) {
            adGroupIndex = currentPeriodId.adGroupIndex;
            int adCountInCurrentAdGroup = this.period.getAdCountInAdGroup(adGroupIndex);
            if (adCountInCurrentAdGroup == -1) {
                return null;
            }
            int nextAdIndexInAdGroup = this.period.getNextAdIndexToPlay(adGroupIndex, currentPeriodId.adIndexInAdGroup);
            if (nextAdIndexInAdGroup >= adCountInCurrentAdGroup) {
                return getMediaPeriodInfoForContent(currentPeriodId.periodIndex, mediaPeriodInfo.contentPositionUs, currentPeriodId.windowSequenceNumber);
            }
            if (this.period.isAdAvailable(adGroupIndex, nextAdIndexInAdGroup)) {
                return getMediaPeriodInfoForAd(currentPeriodId.periodIndex, adGroupIndex, nextAdIndexInAdGroup, mediaPeriodInfo.contentPositionUs, currentPeriodId.windowSequenceNumber);
            }
            return null;
        } else if (mediaPeriodInfo.endPositionUs != Long.MIN_VALUE) {
            int nextAdGroupIndex = this.period.getAdGroupIndexForPositionUs(mediaPeriodInfo.endPositionUs);
            if (nextAdGroupIndex == -1) {
                return getMediaPeriodInfoForContent(currentPeriodId.periodIndex, mediaPeriodInfo.endPositionUs, currentPeriodId.windowSequenceNumber);
            }
            adIndexInAdGroup = this.period.getFirstAdIndexToPlay(nextAdGroupIndex);
            if (this.period.isAdAvailable(nextAdGroupIndex, adIndexInAdGroup)) {
                return getMediaPeriodInfoForAd(currentPeriodId.periodIndex, nextAdGroupIndex, adIndexInAdGroup, mediaPeriodInfo.endPositionUs, currentPeriodId.windowSequenceNumber);
            }
            return null;
        } else {
            int adGroupCount = this.period.getAdGroupCount();
            if (adGroupCount == 0) {
                return null;
            }
            adGroupIndex = adGroupCount - 1;
            if (this.period.getAdGroupTimeUs(adGroupIndex) != Long.MIN_VALUE || this.period.hasPlayedAdGroup(adGroupIndex)) {
                return null;
            }
            adIndexInAdGroup = this.period.getFirstAdIndexToPlay(adGroupIndex);
            if (!this.period.isAdAvailable(adGroupIndex, adIndexInAdGroup)) {
                return null;
            }
            return getMediaPeriodInfoForAd(currentPeriodId.periodIndex, adGroupIndex, adIndexInAdGroup, this.period.getDurationUs(), currentPeriodId.windowSequenceNumber);
        }
    }

    private MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo info, MediaPeriodId newId) {
        long startPositionUs = info.startPositionUs;
        long endPositionUs = info.endPositionUs;
        boolean isLastInPeriod = isLastInPeriod(newId, endPositionUs);
        boolean isLastInTimeline = isLastInTimeline(newId, isLastInPeriod);
        this.timeline.getPeriod(newId.periodIndex, this.period);
        long durationUs = newId.isAd() ? this.period.getAdDurationUs(newId.adGroupIndex, newId.adIndexInAdGroup) : endPositionUs == Long.MIN_VALUE ? this.period.getDurationUs() : endPositionUs;
        return new MediaPeriodInfo(newId, startPositionUs, endPositionUs, info.contentPositionUs, durationUs, isLastInPeriod, isLastInTimeline);
    }

    private MediaPeriodInfo getMediaPeriodInfo(MediaPeriodId id, long contentPositionUs, long startPositionUs) {
        this.timeline.getPeriod(id.periodIndex, this.period);
        if (!id.isAd()) {
            return getMediaPeriodInfoForContent(id.periodIndex, startPositionUs, id.windowSequenceNumber);
        } else if (!this.period.isAdAvailable(id.adGroupIndex, id.adIndexInAdGroup)) {
            return null;
        } else {
            return getMediaPeriodInfoForAd(id.periodIndex, id.adGroupIndex, id.adIndexInAdGroup, contentPositionUs, id.windowSequenceNumber);
        }
    }

    private MediaPeriodInfo getMediaPeriodInfoForAd(int periodIndex, int adGroupIndex, int adIndexInAdGroup, long contentPositionUs, long windowSequenceNumber) {
        MediaPeriodId id = new MediaPeriodId(periodIndex, adGroupIndex, adIndexInAdGroup, windowSequenceNumber);
        boolean isLastInPeriod = isLastInPeriod(id, Long.MIN_VALUE);
        boolean isLastInTimeline = isLastInTimeline(id, isLastInPeriod);
        return new MediaPeriodInfo(id, adIndexInAdGroup == this.period.getFirstAdIndexToPlay(adGroupIndex) ? this.period.getAdResumePositionUs() : 0, Long.MIN_VALUE, contentPositionUs, this.timeline.getPeriod(id.periodIndex, this.period).getAdDurationUs(id.adGroupIndex, id.adIndexInAdGroup), isLastInPeriod, isLastInTimeline);
    }

    private MediaPeriodInfo getMediaPeriodInfoForContent(int periodIndex, long startPositionUs, long windowSequenceNumber) {
        long endUs;
        long durationUs;
        MediaPeriodId id = new MediaPeriodId(periodIndex, windowSequenceNumber);
        this.timeline.getPeriod(id.periodIndex, this.period);
        int nextAdGroupIndex = this.period.getAdGroupIndexAfterPositionUs(startPositionUs);
        if (nextAdGroupIndex == -1) {
            endUs = Long.MIN_VALUE;
        } else {
            endUs = this.period.getAdGroupTimeUs(nextAdGroupIndex);
        }
        boolean isLastInPeriod = isLastInPeriod(id, endUs);
        boolean isLastInTimeline = isLastInTimeline(id, isLastInPeriod);
        if (endUs == Long.MIN_VALUE) {
            durationUs = this.period.getDurationUs();
        } else {
            durationUs = endUs;
        }
        return new MediaPeriodInfo(id, startPositionUs, endUs, C0559C.TIME_UNSET, durationUs, isLastInPeriod, isLastInTimeline);
    }

    private boolean isLastInPeriod(MediaPeriodId id, long endPositionUs) {
        boolean z = false;
        int adGroupCount = this.timeline.getPeriod(id.periodIndex, this.period).getAdGroupCount();
        if (adGroupCount == 0) {
            return true;
        }
        int lastAdGroupIndex = adGroupCount - 1;
        boolean isAd = id.isAd();
        if (this.period.getAdGroupTimeUs(lastAdGroupIndex) == Long.MIN_VALUE) {
            int postrollAdCount = this.period.getAdCountInAdGroup(lastAdGroupIndex);
            if (postrollAdCount == -1) {
                return false;
            }
            boolean isLastAd;
            if (isAd && id.adGroupIndex == lastAdGroupIndex && id.adIndexInAdGroup == postrollAdCount - 1) {
                isLastAd = true;
            } else {
                isLastAd = false;
            }
            if (isLastAd || (!isAd && this.period.getFirstAdIndexToPlay(lastAdGroupIndex) == postrollAdCount)) {
                z = true;
            }
            return z;
        } else if (isAd || endPositionUs != Long.MIN_VALUE) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isLastInTimeline(MediaPeriodId id, boolean isLastMediaPeriodInPeriod) {
        return !this.timeline.getWindow(this.timeline.getPeriod(id.periodIndex, this.period).windowIndex, this.window).isDynamic && this.timeline.isLastPeriod(id.periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled) && isLastMediaPeriodInPeriod;
    }
}
