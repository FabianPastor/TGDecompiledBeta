package org.telegram.messenger.exoplayer2.util;

import org.telegram.messenger.exoplayer2.C0542C;

public final class TimestampAdjuster {
    public static final long DO_NOT_OFFSET = Long.MAX_VALUE;
    private static final long MAX_PTS_PLUS_ONE = 8589934592L;
    private long firstSampleTimestampUs;
    private volatile long lastSampleTimestamp = C0542C.TIME_UNSET;
    private long timestampOffsetUs;

    public TimestampAdjuster(long firstSampleTimestampUs) {
        setFirstSampleTimestampUs(firstSampleTimestampUs);
    }

    public synchronized void setFirstSampleTimestampUs(long firstSampleTimestampUs) {
        Assertions.checkState(this.lastSampleTimestamp == C0542C.TIME_UNSET);
        this.firstSampleTimestampUs = firstSampleTimestampUs;
    }

    public long getFirstSampleTimestampUs() {
        return this.firstSampleTimestampUs;
    }

    public long getLastAdjustedTimestampUs() {
        if (this.lastSampleTimestamp != C0542C.TIME_UNSET) {
            return this.lastSampleTimestamp;
        }
        return this.firstSampleTimestampUs != Long.MAX_VALUE ? this.firstSampleTimestampUs : C0542C.TIME_UNSET;
    }

    public long getTimestampOffsetUs() {
        if (this.firstSampleTimestampUs == Long.MAX_VALUE) {
            return 0;
        }
        return this.lastSampleTimestamp == C0542C.TIME_UNSET ? C0542C.TIME_UNSET : this.timestampOffsetUs;
    }

    public void reset() {
        this.lastSampleTimestamp = C0542C.TIME_UNSET;
    }

    public long adjustTsTimestamp(long pts) {
        if (pts == C0542C.TIME_UNSET) {
            return C0542C.TIME_UNSET;
        }
        if (this.lastSampleTimestamp != C0542C.TIME_UNSET) {
            long lastPts = usToPts(this.lastSampleTimestamp);
            long closestWrapCount = (lastPts + 4294967296L) / MAX_PTS_PLUS_ONE;
            long ptsWrapBelow = pts + ((closestWrapCount - 1) * MAX_PTS_PLUS_ONE);
            long ptsWrapAbove = pts + (MAX_PTS_PLUS_ONE * closestWrapCount);
            pts = Math.abs(ptsWrapBelow - lastPts) < Math.abs(ptsWrapAbove - lastPts) ? ptsWrapBelow : ptsWrapAbove;
        }
        return adjustSampleTimestamp(ptsToUs(pts));
    }

    public long adjustSampleTimestamp(long timeUs) {
        if (timeUs == C0542C.TIME_UNSET) {
            return C0542C.TIME_UNSET;
        }
        if (this.lastSampleTimestamp != C0542C.TIME_UNSET) {
            this.lastSampleTimestamp = timeUs;
        } else {
            if (this.firstSampleTimestampUs != Long.MAX_VALUE) {
                this.timestampOffsetUs = this.firstSampleTimestampUs - timeUs;
            }
            synchronized (this) {
                this.lastSampleTimestamp = timeUs;
                notifyAll();
            }
        }
        return timeUs + this.timestampOffsetUs;
    }

    public synchronized void waitUntilInitialized() throws InterruptedException {
        while (this.lastSampleTimestamp == C0542C.TIME_UNSET) {
            wait();
        }
    }

    public static long ptsToUs(long pts) {
        return (C0542C.MICROS_PER_SECOND * pts) / 90000;
    }

    public static long usToPts(long us) {
        return (90000 * us) / C0542C.MICROS_PER_SECOND;
    }
}
