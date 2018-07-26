package org.telegram.messenger.exoplayer2.source;

import org.telegram.messenger.exoplayer2.C0559C;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SinglePeriodTimeline extends Timeline {
    private static final Object UID = new Object();
    private final boolean isDynamic;
    private final boolean isSeekable;
    private final long periodDurationUs;
    private final long presentationStartTimeMs;
    private final Object tag;
    private final long windowDefaultStartPositionUs;
    private final long windowDurationUs;
    private final long windowPositionInPeriodUs;
    private final long windowStartTimeMs;

    public SinglePeriodTimeline(long durationUs, boolean isSeekable, boolean isDynamic) {
        this(durationUs, isSeekable, isDynamic, null);
    }

    public SinglePeriodTimeline(long durationUs, boolean isSeekable, boolean isDynamic, Object tag) {
        this(durationUs, durationUs, 0, 0, isSeekable, isDynamic, tag);
    }

    public SinglePeriodTimeline(long periodDurationUs, long windowDurationUs, long windowPositionInPeriodUs, long windowDefaultStartPositionUs, boolean isSeekable, boolean isDynamic, Object tag) {
        this(C0559C.TIME_UNSET, C0559C.TIME_UNSET, periodDurationUs, windowDurationUs, windowPositionInPeriodUs, windowDefaultStartPositionUs, isSeekable, isDynamic, tag);
    }

    public SinglePeriodTimeline(long presentationStartTimeMs, long windowStartTimeMs, long periodDurationUs, long windowDurationUs, long windowPositionInPeriodUs, long windowDefaultStartPositionUs, boolean isSeekable, boolean isDynamic, Object tag) {
        this.presentationStartTimeMs = presentationStartTimeMs;
        this.windowStartTimeMs = windowStartTimeMs;
        this.periodDurationUs = periodDurationUs;
        this.windowDurationUs = windowDurationUs;
        this.windowPositionInPeriodUs = windowPositionInPeriodUs;
        this.windowDefaultStartPositionUs = windowDefaultStartPositionUs;
        this.isSeekable = isSeekable;
        this.isDynamic = isDynamic;
        this.tag = tag;
    }

    public int getWindowCount() {
        return 1;
    }

    public Window getWindow(int windowIndex, Window window, boolean setTag, long defaultPositionProjectionUs) {
        Assertions.checkIndex(windowIndex, 0, 1);
        Object tag = setTag ? this.tag : null;
        long windowDefaultStartPositionUs = this.windowDefaultStartPositionUs;
        if (this.isDynamic && defaultPositionProjectionUs != 0) {
            if (this.windowDurationUs == C0559C.TIME_UNSET) {
                windowDefaultStartPositionUs = C0559C.TIME_UNSET;
            } else {
                windowDefaultStartPositionUs += defaultPositionProjectionUs;
                if (windowDefaultStartPositionUs > this.windowDurationUs) {
                    windowDefaultStartPositionUs = C0559C.TIME_UNSET;
                }
            }
        }
        return window.set(tag, this.presentationStartTimeMs, this.windowStartTimeMs, this.isSeekable, this.isDynamic, windowDefaultStartPositionUs, this.windowDurationUs, 0, 0, this.windowPositionInPeriodUs);
    }

    public int getPeriodCount() {
        return 1;
    }

    public Period getPeriod(int periodIndex, Period period, boolean setIds) {
        Object uid;
        Assertions.checkIndex(periodIndex, 0, 1);
        if (setIds) {
            uid = UID;
        } else {
            uid = null;
        }
        return period.set(null, uid, 0, this.periodDurationUs, -this.windowPositionInPeriodUs);
    }

    public int getIndexOfPeriod(Object uid) {
        return UID.equals(uid) ? 0 : -1;
    }
}
