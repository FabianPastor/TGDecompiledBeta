package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.ShuffleOrder.UnshuffledShuffleOrder;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.tgnet.ConnectionsManager;

public final class LoopingMediaSource implements MediaSource {
    private int childPeriodCount;
    private final MediaSource childSource;
    private final int loopCount;
    private boolean wasPrepareSourceCalled;

    private static final class InfinitelyLoopingTimeline extends ForwardingTimeline {
        public InfinitelyLoopingTimeline(Timeline timeline) {
            super(timeline);
        }

        public int getNextWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
            int childNextWindowIndex = this.timeline.getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
            return childNextWindowIndex == -1 ? getFirstWindowIndex(shuffleModeEnabled) : childNextWindowIndex;
        }

        public int getPreviousWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
            int childPreviousWindowIndex = this.timeline.getPreviousWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
            return childPreviousWindowIndex == -1 ? getLastWindowIndex(shuffleModeEnabled) : childPreviousWindowIndex;
        }
    }

    private static final class LoopingTimeline extends AbstractConcatenatedTimeline {
        private final int childPeriodCount;
        private final Timeline childTimeline;
        private final int childWindowCount;
        private final int loopCount;

        public LoopingTimeline(Timeline childTimeline, int loopCount) {
            super(new UnshuffledShuffleOrder(loopCount));
            this.childTimeline = childTimeline;
            this.childPeriodCount = childTimeline.getPeriodCount();
            this.childWindowCount = childTimeline.getWindowCount();
            this.loopCount = loopCount;
            if (this.childPeriodCount > 0) {
                Assertions.checkState(loopCount <= ConnectionsManager.DEFAULT_DATACENTER_ID / this.childPeriodCount, "LoopingMediaSource contains too many periods");
            }
        }

        public int getWindowCount() {
            return this.childWindowCount * this.loopCount;
        }

        public int getPeriodCount() {
            return this.childPeriodCount * this.loopCount;
        }

        protected int getChildIndexByPeriodIndex(int periodIndex) {
            return periodIndex / this.childPeriodCount;
        }

        protected int getChildIndexByWindowIndex(int windowIndex) {
            return windowIndex / this.childWindowCount;
        }

        protected int getChildIndexByChildUid(Object childUid) {
            if (childUid instanceof Integer) {
                return ((Integer) childUid).intValue();
            }
            return -1;
        }

        protected Timeline getTimelineByChildIndex(int childIndex) {
            return this.childTimeline;
        }

        protected int getFirstPeriodIndexByChildIndex(int childIndex) {
            return this.childPeriodCount * childIndex;
        }

        protected int getFirstWindowIndexByChildIndex(int childIndex) {
            return this.childWindowCount * childIndex;
        }

        protected Object getChildUidByChildIndex(int childIndex) {
            return Integer.valueOf(childIndex);
        }
    }

    public LoopingMediaSource(MediaSource childSource) {
        this(childSource, ConnectionsManager.DEFAULT_DATACENTER_ID);
    }

    public LoopingMediaSource(MediaSource childSource, int loopCount) {
        Assertions.checkArgument(loopCount > 0);
        this.childSource = childSource;
        this.loopCount = loopCount;
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, final Listener listener) {
        Assertions.checkState(this.wasPrepareSourceCalled ^ true, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.wasPrepareSourceCalled = true;
        this.childSource.prepareSource(player, false, new Listener() {
            public void onSourceInfoRefreshed(MediaSource source, Timeline timeline, Object manifest) {
                LoopingMediaSource.this.childPeriodCount = timeline.getPeriodCount();
                listener.onSourceInfoRefreshed(LoopingMediaSource.this, LoopingMediaSource.this.loopCount != ConnectionsManager.DEFAULT_DATACENTER_ID ? new LoopingTimeline(timeline, LoopingMediaSource.this.loopCount) : new InfinitelyLoopingTimeline(timeline), manifest);
            }
        });
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.childSource.maybeThrowSourceInfoRefreshError();
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        if (this.loopCount != ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return this.childSource.createPeriod(id.copyWithPeriodIndex(id.periodIndex % this.childPeriodCount), allocator);
        }
        return this.childSource.createPeriod(id, allocator);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        this.childSource.releasePeriod(mediaPeriod);
    }

    public void releaseSource() {
        this.childSource.releaseSource();
    }
}
