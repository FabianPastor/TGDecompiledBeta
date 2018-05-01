package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.ShuffleOrder.DefaultShuffleOrder;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ConcatenatingMediaSource implements MediaSource {
    private final boolean[] duplicateFlags;
    private final boolean isAtomic;
    private Listener listener;
    private final Object[] manifests;
    private final MediaSource[] mediaSources;
    private final ShuffleOrder shuffleOrder;
    private final Map<MediaPeriod, Integer> sourceIndexByMediaPeriod;
    private ConcatenatedTimeline timeline;
    private final Timeline[] timelines;

    private static final class ConcatenatedTimeline extends AbstractConcatenatedTimeline {
        private final boolean isAtomic;
        private final int[] sourcePeriodOffsets;
        private final int[] sourceWindowOffsets;
        private final Timeline[] timelines;

        public ConcatenatedTimeline(Timeline[] timelineArr, boolean z, ShuffleOrder shuffleOrder) {
            super(shuffleOrder);
            int[] iArr = new int[timelineArr.length];
            int[] iArr2 = new int[timelineArr.length];
            long j = 0;
            int i = 0;
            int i2 = i;
            while (i < timelineArr.length) {
                Timeline timeline = timelineArr[i];
                long periodCount = j + ((long) timeline.getPeriodCount());
                Assertions.checkState(periodCount <= 2147483647L, "ConcatenatingMediaSource children contain too many periods");
                iArr[i] = (int) periodCount;
                i2 += timeline.getWindowCount();
                iArr2[i] = i2;
                i++;
                j = periodCount;
            }
            this.timelines = timelineArr;
            this.sourcePeriodOffsets = iArr;
            this.sourceWindowOffsets = iArr2;
            this.isAtomic = z;
        }

        public int getWindowCount() {
            return this.sourceWindowOffsets[this.sourceWindowOffsets.length - 1];
        }

        public int getPeriodCount() {
            return this.sourcePeriodOffsets[this.sourcePeriodOffsets.length - 1];
        }

        public int getNextWindowIndex(int i, int i2, boolean z) {
            boolean z2 = true;
            if (this.isAtomic && i2 == 1) {
                i2 = 2;
            }
            if (this.isAtomic || !z) {
                z2 = false;
            }
            return super.getNextWindowIndex(i, i2, z2);
        }

        public int getPreviousWindowIndex(int i, int i2, boolean z) {
            boolean z2 = true;
            if (this.isAtomic && i2 == 1) {
                i2 = 2;
            }
            if (this.isAtomic || !z) {
                z2 = false;
            }
            return super.getPreviousWindowIndex(i, i2, z2);
        }

        public int getLastWindowIndex(boolean z) {
            z = !this.isAtomic && z;
            return super.getLastWindowIndex(z);
        }

        public int getFirstWindowIndex(boolean z) {
            z = !this.isAtomic && z;
            return super.getFirstWindowIndex(z);
        }

        protected int getChildIndexByPeriodIndex(int i) {
            return Util.binarySearchFloor(this.sourcePeriodOffsets, i + 1, false, false) + 1;
        }

        protected int getChildIndexByWindowIndex(int i) {
            return Util.binarySearchFloor(this.sourceWindowOffsets, i + 1, false, false) + 1;
        }

        protected int getChildIndexByChildUid(Object obj) {
            if (obj instanceof Integer) {
                return ((Integer) obj).intValue();
            }
            return -1;
        }

        protected Timeline getTimelineByChildIndex(int i) {
            return this.timelines[i];
        }

        protected int getFirstPeriodIndexByChildIndex(int i) {
            return i == 0 ? 0 : this.sourcePeriodOffsets[i - 1];
        }

        protected int getFirstWindowIndexByChildIndex(int i) {
            return i == 0 ? 0 : this.sourceWindowOffsets[i - 1];
        }

        protected Object getChildUidByChildIndex(int i) {
            return Integer.valueOf(i);
        }
    }

    public ConcatenatingMediaSource(MediaSource... mediaSourceArr) {
        this(false, mediaSourceArr);
    }

    public ConcatenatingMediaSource(boolean z, MediaSource... mediaSourceArr) {
        this(z, new DefaultShuffleOrder(mediaSourceArr.length), mediaSourceArr);
    }

    public ConcatenatingMediaSource(boolean z, ShuffleOrder shuffleOrder, MediaSource... mediaSourceArr) {
        boolean z2 = false;
        for (Object checkNotNull : mediaSourceArr) {
            Assertions.checkNotNull(checkNotNull);
        }
        if (shuffleOrder.getLength() == mediaSourceArr.length) {
            z2 = true;
        }
        Assertions.checkArgument(z2);
        this.mediaSources = mediaSourceArr;
        this.isAtomic = z;
        this.shuffleOrder = shuffleOrder;
        this.timelines = new Timeline[mediaSourceArr.length];
        this.manifests = new Object[mediaSourceArr.length];
        this.sourceIndexByMediaPeriod = new HashMap();
        this.duplicateFlags = buildDuplicateFlags(mediaSourceArr);
    }

    public void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener) {
        Assertions.checkState(!this.listener, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.listener = listener;
        if (this.mediaSources.length) {
            for (z = false; z < this.mediaSources.length; z++) {
                if (this.duplicateFlags[z] == null) {
                    this.mediaSources[z].prepareSource(exoPlayer, false, new Listener() {
                        public void onSourceInfoRefreshed(MediaSource mediaSource, Timeline timeline, Object obj) {
                            ConcatenatingMediaSource.this.handleSourceInfoRefreshed(z, timeline, obj);
                        }
                    });
                }
            }
            return;
        }
        listener.onSourceInfoRefreshed(this, Timeline.EMPTY, false);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        for (int i = 0; i < this.mediaSources.length; i++) {
            if (!this.duplicateFlags[i]) {
                this.mediaSources[i].maybeThrowSourceInfoRefreshError();
            }
        }
    }

    public MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator) {
        int childIndexByPeriodIndex = this.timeline.getChildIndexByPeriodIndex(mediaPeriodId.periodIndex);
        mediaPeriodId = this.mediaSources[childIndexByPeriodIndex].createPeriod(mediaPeriodId.copyWithPeriodIndex(mediaPeriodId.periodIndex - this.timeline.getFirstPeriodIndexByChildIndex(childIndexByPeriodIndex)), allocator);
        this.sourceIndexByMediaPeriod.put(mediaPeriodId, Integer.valueOf(childIndexByPeriodIndex));
        return mediaPeriodId;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        int intValue = ((Integer) this.sourceIndexByMediaPeriod.get(mediaPeriod)).intValue();
        this.sourceIndexByMediaPeriod.remove(mediaPeriod);
        this.mediaSources[intValue].releasePeriod(mediaPeriod);
    }

    public void releaseSource() {
        for (int i = 0; i < this.mediaSources.length; i++) {
            if (!this.duplicateFlags[i]) {
                this.mediaSources[i].releaseSource();
            }
        }
    }

    private void handleSourceInfoRefreshed(int i, Timeline timeline, Object obj) {
        this.timelines[i] = timeline;
        this.manifests[i] = obj;
        for (int i2 = i + 1; i2 < this.mediaSources.length; i2++) {
            if (this.mediaSources[i2] == this.mediaSources[i]) {
                this.timelines[i2] = timeline;
                this.manifests[i2] = obj;
            }
        }
        i = this.timelines;
        timeline = i.length;
        Timeline timeline2 = null;
        while (timeline2 < timeline) {
            if (i[timeline2] != null) {
                timeline2++;
            } else {
                return;
            }
        }
        this.timeline = new ConcatenatedTimeline((Timeline[]) this.timelines.clone(), this.isAtomic, this.shuffleOrder);
        this.listener.onSourceInfoRefreshed(this, this.timeline, this.manifests.clone());
    }

    private static boolean[] buildDuplicateFlags(MediaSource[] mediaSourceArr) {
        boolean[] zArr = new boolean[mediaSourceArr.length];
        IdentityHashMap identityHashMap = new IdentityHashMap(mediaSourceArr.length);
        for (int i = 0; i < mediaSourceArr.length; i++) {
            Object obj = mediaSourceArr[i];
            if (identityHashMap.containsKey(obj)) {
                zArr[i] = true;
            } else {
                identityHashMap.put(obj, null);
            }
        }
        return zArr;
    }
}
