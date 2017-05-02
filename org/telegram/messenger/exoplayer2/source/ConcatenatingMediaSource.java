package org.telegram.messenger.exoplayer2.source;

import android.util.Pair;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ConcatenatingMediaSource implements MediaSource {
    private final boolean[] duplicateFlags;
    private Listener listener;
    private final Object[] manifests;
    private final MediaSource[] mediaSources;
    private final Map<MediaPeriod, Integer> sourceIndexByMediaPeriod = new HashMap();
    private ConcatenatedTimeline timeline;
    private final Timeline[] timelines;

    private static final class ConcatenatedTimeline extends Timeline {
        private final int[] sourcePeriodOffsets;
        private final int[] sourceWindowOffsets;
        private final Timeline[] timelines;

        public ConcatenatedTimeline(Timeline[] timelines) {
            int[] sourcePeriodOffsets = new int[timelines.length];
            int[] sourceWindowOffsets = new int[timelines.length];
            long periodCount = 0;
            int windowCount = 0;
            for (int i = 0; i < timelines.length; i++) {
                Timeline timeline = timelines[i];
                periodCount += (long) timeline.getPeriodCount();
                Assertions.checkState(periodCount <= 2147483647L, "ConcatenatingMediaSource children contain too many periods");
                sourcePeriodOffsets[i] = (int) periodCount;
                windowCount += timeline.getWindowCount();
                sourceWindowOffsets[i] = windowCount;
            }
            this.timelines = timelines;
            this.sourcePeriodOffsets = sourcePeriodOffsets;
            this.sourceWindowOffsets = sourceWindowOffsets;
        }

        public int getWindowCount() {
            return this.sourceWindowOffsets[this.sourceWindowOffsets.length - 1];
        }

        public Window getWindow(int windowIndex, Window window, boolean setIds, long defaultPositionProjectionUs) {
            int sourceIndex = getSourceIndexForWindow(windowIndex);
            int firstWindowIndexInSource = getFirstWindowIndexInSource(sourceIndex);
            int firstPeriodIndexInSource = getFirstPeriodIndexInSource(sourceIndex);
            this.timelines[sourceIndex].getWindow(windowIndex - firstWindowIndexInSource, window, setIds, defaultPositionProjectionUs);
            window.firstPeriodIndex += firstPeriodIndexInSource;
            window.lastPeriodIndex += firstPeriodIndexInSource;
            return window;
        }

        public int getPeriodCount() {
            return this.sourcePeriodOffsets[this.sourcePeriodOffsets.length - 1];
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            int sourceIndex = getSourceIndexForPeriod(periodIndex);
            int firstWindowIndexInSource = getFirstWindowIndexInSource(sourceIndex);
            this.timelines[sourceIndex].getPeriod(periodIndex - getFirstPeriodIndexInSource(sourceIndex), period, setIds);
            period.windowIndex += firstWindowIndexInSource;
            if (setIds) {
                period.uid = Pair.create(Integer.valueOf(sourceIndex), period.uid);
            }
            return period;
        }

        public int getIndexOfPeriod(Object uid) {
            if (!(uid instanceof Pair)) {
                return -1;
            }
            Pair<?, ?> sourceIndexAndPeriodId = (Pair) uid;
            if (!(sourceIndexAndPeriodId.first instanceof Integer)) {
                return -1;
            }
            int sourceIndex = ((Integer) sourceIndexAndPeriodId.first).intValue();
            Object periodId = sourceIndexAndPeriodId.second;
            if (sourceIndex < 0 || sourceIndex >= this.timelines.length) {
                return -1;
            }
            int i;
            int periodIndexInSource = this.timelines[sourceIndex].getIndexOfPeriod(periodId);
            if (periodIndexInSource == -1) {
                i = -1;
            } else {
                i = getFirstPeriodIndexInSource(sourceIndex) + periodIndexInSource;
            }
            return i;
        }

        private int getSourceIndexForPeriod(int periodIndex) {
            return Util.binarySearchFloor(this.sourcePeriodOffsets, periodIndex, true, false) + 1;
        }

        private int getFirstPeriodIndexInSource(int sourceIndex) {
            return sourceIndex == 0 ? 0 : this.sourcePeriodOffsets[sourceIndex - 1];
        }

        private int getSourceIndexForWindow(int windowIndex) {
            return Util.binarySearchFloor(this.sourceWindowOffsets, windowIndex, true, false) + 1;
        }

        private int getFirstWindowIndexInSource(int sourceIndex) {
            return sourceIndex == 0 ? 0 : this.sourceWindowOffsets[sourceIndex - 1];
        }
    }

    public ConcatenatingMediaSource(MediaSource... mediaSources) {
        this.mediaSources = mediaSources;
        this.timelines = new Timeline[mediaSources.length];
        this.manifests = new Object[mediaSources.length];
        this.duplicateFlags = buildDuplicateFlags(mediaSources);
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        this.listener = listener;
        for (int i = 0; i < this.mediaSources.length; i++) {
            if (!this.duplicateFlags[i]) {
                final int index = i;
                this.mediaSources[i].prepareSource(player, false, new Listener() {
                    public void onSourceInfoRefreshed(Timeline timeline, Object manifest) {
                        ConcatenatingMediaSource.this.handleSourceInfoRefreshed(index, timeline, manifest);
                    }
                });
            }
        }
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        for (int i = 0; i < this.mediaSources.length; i++) {
            if (!this.duplicateFlags[i]) {
                this.mediaSources[i].maybeThrowSourceInfoRefreshError();
            }
        }
    }

    public MediaPeriod createPeriod(int index, Allocator allocator, long positionUs) {
        int sourceIndex = this.timeline.getSourceIndexForPeriod(index);
        MediaPeriod mediaPeriod = this.mediaSources[sourceIndex].createPeriod(index - this.timeline.getFirstPeriodIndexInSource(sourceIndex), allocator, positionUs);
        this.sourceIndexByMediaPeriod.put(mediaPeriod, Integer.valueOf(sourceIndex));
        return mediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        int sourceIndex = ((Integer) this.sourceIndexByMediaPeriod.get(mediaPeriod)).intValue();
        this.sourceIndexByMediaPeriod.remove(mediaPeriod);
        this.mediaSources[sourceIndex].releasePeriod(mediaPeriod);
    }

    public void releaseSource() {
        for (int i = 0; i < this.mediaSources.length; i++) {
            if (!this.duplicateFlags[i]) {
                this.mediaSources[i].releaseSource();
            }
        }
    }

    private void handleSourceInfoRefreshed(int sourceFirstIndex, Timeline sourceTimeline, Object sourceManifest) {
        this.timelines[sourceFirstIndex] = sourceTimeline;
        this.manifests[sourceFirstIndex] = sourceManifest;
        for (int i = sourceFirstIndex + 1; i < this.mediaSources.length; i++) {
            if (this.mediaSources[i] == this.mediaSources[sourceFirstIndex]) {
                this.timelines[i] = sourceTimeline;
                this.manifests[i] = sourceManifest;
            }
        }
        Timeline[] timelineArr = this.timelines;
        int length = timelineArr.length;
        int i2 = 0;
        while (i2 < length) {
            if (timelineArr[i2] != null) {
                i2++;
            } else {
                return;
            }
        }
        this.timeline = new ConcatenatedTimeline((Timeline[]) this.timelines.clone());
        this.listener.onSourceInfoRefreshed(this.timeline, this.manifests.clone());
    }

    private static boolean[] buildDuplicateFlags(MediaSource[] mediaSources) {
        boolean[] duplicateFlags = new boolean[mediaSources.length];
        IdentityHashMap<MediaSource, Void> sources = new IdentityHashMap(mediaSources.length);
        for (int i = 0; i < mediaSources.length; i++) {
            MediaSource source = mediaSources[i];
            if (sources.containsKey(source)) {
                duplicateFlags[i] = true;
            } else {
                sources.put(source, null);
            }
        }
        return duplicateFlags;
    }
}
