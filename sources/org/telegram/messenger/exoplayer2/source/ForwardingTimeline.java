package org.telegram.messenger.exoplayer2.source;

import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;

public abstract class ForwardingTimeline extends Timeline {
    protected final Timeline timeline;

    public ForwardingTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public int getWindowCount() {
        return this.timeline.getWindowCount();
    }

    public int getNextWindowIndex(int i, int i2, boolean z) {
        return this.timeline.getNextWindowIndex(i, i2, z);
    }

    public int getPreviousWindowIndex(int i, int i2, boolean z) {
        return this.timeline.getPreviousWindowIndex(i, i2, z);
    }

    public int getLastWindowIndex(boolean z) {
        return this.timeline.getLastWindowIndex(z);
    }

    public int getFirstWindowIndex(boolean z) {
        return this.timeline.getFirstWindowIndex(z);
    }

    public Window getWindow(int i, Window window, boolean z, long j) {
        return this.timeline.getWindow(i, window, z, j);
    }

    public int getPeriodCount() {
        return this.timeline.getPeriodCount();
    }

    public Period getPeriod(int i, Period period, boolean z) {
        return this.timeline.getPeriod(i, period, z);
    }

    public int getIndexOfPeriod(Object obj) {
        return this.timeline.getIndexOfPeriod(obj);
    }
}
