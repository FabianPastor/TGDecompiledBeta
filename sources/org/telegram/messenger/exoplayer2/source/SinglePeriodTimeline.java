package org.telegram.messenger.exoplayer2.source;

import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SinglePeriodTimeline extends Timeline {
    private static final Object ID = new Object();
    private final boolean isDynamic;
    private final boolean isSeekable;
    private final long periodDurationUs;
    private final long presentationStartTimeMs;
    private final long windowDefaultStartPositionUs;
    private final long windowDurationUs;
    private final long windowPositionInPeriodUs;
    private final long windowStartTimeMs;

    public int getPeriodCount() {
        return 1;
    }

    public int getWindowCount() {
        return 1;
    }

    public SinglePeriodTimeline(long j, boolean z, boolean z2) {
        this(j, j, 0, 0, z, z2);
    }

    public SinglePeriodTimeline(long j, long j2, long j3, long j4, boolean z, boolean z2) {
        this(C0542C.TIME_UNSET, C0542C.TIME_UNSET, j, j2, j3, j4, z, z2);
    }

    public SinglePeriodTimeline(long j, long j2, long j3, long j4, long j5, long j6, boolean z, boolean z2) {
        this.presentationStartTimeMs = j;
        this.windowStartTimeMs = j2;
        this.periodDurationUs = j3;
        this.windowDurationUs = j4;
        this.windowPositionInPeriodUs = j5;
        this.windowDefaultStartPositionUs = j6;
        this.isSeekable = z;
        this.isDynamic = z2;
    }

    public Window getWindow(int i, Window window, boolean z, long j) {
        long j2;
        SinglePeriodTimeline singlePeriodTimeline = this;
        Assertions.checkIndex(i, 0, 1);
        Object obj = z ? ID : null;
        long j3 = singlePeriodTimeline.windowDefaultStartPositionUs;
        if (!singlePeriodTimeline.isDynamic || j == 0) {
            j2 = j3;
        } else {
            if (singlePeriodTimeline.windowDurationUs != C0542C.TIME_UNSET) {
                long j4 = j3 + j;
                if (j4 <= singlePeriodTimeline.windowDurationUs) {
                    j2 = j4;
                }
            }
            j2 = C0542C.TIME_UNSET;
        }
        return window.set(obj, singlePeriodTimeline.presentationStartTimeMs, singlePeriodTimeline.windowStartTimeMs, singlePeriodTimeline.isSeekable, singlePeriodTimeline.isDynamic, j2, singlePeriodTimeline.windowDurationUs, 0, 0, singlePeriodTimeline.windowPositionInPeriodUs);
    }

    public Period getPeriod(int i, Period period, boolean z) {
        Assertions.checkIndex(i, 0, 1);
        Object obj = z ? ID : 0;
        return period.set(obj, obj, 0, this.periodDurationUs, -this.windowPositionInPeriodUs);
    }

    public int getIndexOfPeriod(Object obj) {
        return ID.equals(obj) != null ? null : -1;
    }
}
