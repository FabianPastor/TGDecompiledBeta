package org.telegram.messenger.exoplayer2.source;

import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.tgnet.ConnectionsManager;

public final class LoopingMediaSource implements MediaSource {
    private static final String TAG = "LoopingMediaSource";
    private int childPeriodCount;
    private final MediaSource childSource;
    private final int loopCount;

    private static final class LoopingTimeline extends Timeline {
        private final int childPeriodCount;
        private final Timeline childTimeline;
        private final int childWindowCount;
        private final int loopCount;

        public LoopingTimeline(Timeline childTimeline, int loopCount) {
            this.childTimeline = childTimeline;
            this.childPeriodCount = childTimeline.getPeriodCount();
            this.childWindowCount = childTimeline.getWindowCount();
            int maxLoopCount = ConnectionsManager.DEFAULT_DATACENTER_ID / this.childPeriodCount;
            if (loopCount > maxLoopCount) {
                if (loopCount != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    Log.w(LoopingMediaSource.TAG, "Capped loops to avoid overflow: " + loopCount + " -> " + maxLoopCount);
                }
                this.loopCount = maxLoopCount;
                return;
            }
            this.loopCount = loopCount;
        }

        public int getWindowCount() {
            return this.childWindowCount * this.loopCount;
        }

        public Window getWindow(int windowIndex, Window window, boolean setIds, long defaultPositionProjectionUs) {
            this.childTimeline.getWindow(windowIndex % this.childWindowCount, window, setIds, defaultPositionProjectionUs);
            int periodIndexOffset = (windowIndex / this.childWindowCount) * this.childPeriodCount;
            window.firstPeriodIndex += periodIndexOffset;
            window.lastPeriodIndex += periodIndexOffset;
            return window;
        }

        public int getPeriodCount() {
            return this.childPeriodCount * this.loopCount;
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            this.childTimeline.getPeriod(periodIndex % this.childPeriodCount, period, setIds);
            int loopCount = periodIndex / this.childPeriodCount;
            period.windowIndex += this.childWindowCount * loopCount;
            if (setIds) {
                period.uid = Pair.create(Integer.valueOf(loopCount), period.uid);
            }
            return period;
        }

        public int getIndexOfPeriod(Object uid) {
            if (!(uid instanceof Pair)) {
                return -1;
            }
            Pair<?, ?> loopCountAndChildUid = (Pair) uid;
            if (!(loopCountAndChildUid.first instanceof Integer)) {
                return -1;
            }
            return this.childTimeline.getIndexOfPeriod(loopCountAndChildUid.second) + (((Integer) loopCountAndChildUid.first).intValue() * this.childPeriodCount);
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

    public void prepareSource(final Listener listener) {
        this.childSource.prepareSource(new Listener() {
            public void onSourceInfoRefreshed(Timeline timeline, Object manifest) {
                LoopingMediaSource.this.childPeriodCount = timeline.getPeriodCount();
                listener.onSourceInfoRefreshed(new LoopingTimeline(timeline, LoopingMediaSource.this.loopCount), manifest);
            }
        });
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.childSource.maybeThrowSourceInfoRefreshError();
    }

    public MediaPeriod createPeriod(int index, Allocator allocator, long positionUs) {
        return this.childSource.createPeriod(index % this.childPeriodCount, allocator, positionUs);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        this.childSource.releasePeriod(mediaPeriod);
    }

    public void releaseSource() {
        this.childSource.releaseSource();
    }
}
