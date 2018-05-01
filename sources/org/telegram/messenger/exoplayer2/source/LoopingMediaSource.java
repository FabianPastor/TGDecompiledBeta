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

        public int getNextWindowIndex(int i, int i2, boolean z) {
            i = this.timeline.getNextWindowIndex(i, i2, z);
            return i == -1 ? getFirstWindowIndex(z) : i;
        }

        public int getPreviousWindowIndex(int i, int i2, boolean z) {
            i = this.timeline.getPreviousWindowIndex(i, i2, z);
            return i == -1 ? getLastWindowIndex(z) : i;
        }
    }

    private static final class LoopingTimeline extends AbstractConcatenatedTimeline {
        private final int childPeriodCount;
        private final Timeline childTimeline;
        private final int childWindowCount;
        private final int loopCount;

        public LoopingTimeline(Timeline timeline, int i) {
            super(new UnshuffledShuffleOrder(i));
            this.childTimeline = timeline;
            this.childPeriodCount = timeline.getPeriodCount();
            this.childWindowCount = timeline.getWindowCount();
            this.loopCount = i;
            if (this.childPeriodCount > null) {
                Assertions.checkState(i <= Integer.MAX_VALUE / this.childPeriodCount ? true : null, "LoopingMediaSource contains too many periods");
            }
        }

        public int getWindowCount() {
            return this.childWindowCount * this.loopCount;
        }

        public int getPeriodCount() {
            return this.childPeriodCount * this.loopCount;
        }

        protected int getChildIndexByPeriodIndex(int i) {
            return i / this.childPeriodCount;
        }

        protected int getChildIndexByWindowIndex(int i) {
            return i / this.childWindowCount;
        }

        protected int getChildIndexByChildUid(Object obj) {
            if (obj instanceof Integer) {
                return ((Integer) obj).intValue();
            }
            return -1;
        }

        protected Timeline getTimelineByChildIndex(int i) {
            return this.childTimeline;
        }

        protected int getFirstPeriodIndexByChildIndex(int i) {
            return i * this.childPeriodCount;
        }

        protected int getFirstWindowIndexByChildIndex(int i) {
            return i * this.childWindowCount;
        }

        protected Object getChildUidByChildIndex(int i) {
            return Integer.valueOf(i);
        }
    }

    public LoopingMediaSource(MediaSource mediaSource) {
        this(mediaSource, ConnectionsManager.DEFAULT_DATACENTER_ID);
    }

    public LoopingMediaSource(MediaSource mediaSource, int i) {
        Assertions.checkArgument(i > 0);
        this.childSource = mediaSource;
        this.loopCount = i;
    }

    public void prepareSource(ExoPlayer exoPlayer, boolean z, final Listener listener) {
        Assertions.checkState(this.wasPrepareSourceCalled ^ true, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.wasPrepareSourceCalled = true;
        this.childSource.prepareSource(exoPlayer, false, new Listener() {
            public void onSourceInfoRefreshed(MediaSource mediaSource, Timeline timeline, Object obj) {
                LoopingMediaSource.this.childPeriodCount = timeline.getPeriodCount();
                listener.onSourceInfoRefreshed(LoopingMediaSource.this, LoopingMediaSource.this.loopCount != Integer.MAX_VALUE ? new LoopingTimeline(timeline, LoopingMediaSource.this.loopCount) : new InfinitelyLoopingTimeline(timeline), obj);
            }
        });
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.childSource.maybeThrowSourceInfoRefreshError();
    }

    public MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator) {
        if (this.loopCount != ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return this.childSource.createPeriod(mediaPeriodId.copyWithPeriodIndex(mediaPeriodId.periodIndex % this.childPeriodCount), allocator);
        }
        return this.childSource.createPeriod(mediaPeriodId, allocator);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        this.childSource.releasePeriod(mediaPeriod);
    }

    public void releaseSource() {
        this.childSource.releaseSource();
    }
}
