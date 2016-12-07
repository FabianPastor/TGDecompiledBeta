package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;

public class DashManifest {
    public final long availabilityStartTime;
    public final long duration;
    public final boolean dynamic;
    public final Uri location;
    public final long minBufferTime;
    public final long minUpdatePeriod;
    private final List<Period> periods;
    public final long suggestedPresentationDelay;
    public final long timeShiftBufferDepth;
    public final UtcTimingElement utcTiming;

    public DashManifest(long availabilityStartTime, long duration, long minBufferTime, boolean dynamic, long minUpdatePeriod, long timeShiftBufferDepth, long suggestedPresentationDelay, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        this.availabilityStartTime = availabilityStartTime;
        this.duration = duration;
        this.minBufferTime = minBufferTime;
        this.dynamic = dynamic;
        this.minUpdatePeriod = minUpdatePeriod;
        this.timeShiftBufferDepth = timeShiftBufferDepth;
        this.suggestedPresentationDelay = suggestedPresentationDelay;
        this.utcTiming = utcTiming;
        this.location = location;
        if (periods == null) {
            periods = Collections.emptyList();
        }
        this.periods = periods;
    }

    public final int getPeriodCount() {
        return this.periods.size();
    }

    public final Period getPeriod(int index) {
        return (Period) this.periods.get(index);
    }

    public final long getPeriodDurationMs(int index) {
        if (index != this.periods.size() - 1) {
            return ((Period) this.periods.get(index + 1)).startMs - ((Period) this.periods.get(index)).startMs;
        }
        if (this.duration == C.TIME_UNSET) {
            return C.TIME_UNSET;
        }
        return this.duration - ((Period) this.periods.get(index)).startMs;
    }

    public final long getPeriodDurationUs(int index) {
        return C.msToUs(getPeriodDurationMs(index));
    }
}
