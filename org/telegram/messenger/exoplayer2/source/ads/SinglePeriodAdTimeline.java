package org.telegram.messenger.exoplayer2.source.ads;

import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.ForwardingTimeline;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class SinglePeriodAdTimeline extends ForwardingTimeline {
    private final int[] adCounts;
    private final long[][] adDurationsUs;
    private final long[] adGroupTimesUs;
    private final long adResumePositionUs;
    private final int[] adsLoadedCounts;
    private final int[] adsPlayedCounts;
    private final long contentDurationUs;

    public SinglePeriodAdTimeline(Timeline contentTimeline, long[] adGroupTimesUs, int[] adCounts, int[] adsLoadedCounts, int[] adsPlayedCounts, long[][] adDurationsUs, long adResumePositionUs, long contentDurationUs) {
        boolean z = true;
        super(contentTimeline);
        Assertions.checkState(contentTimeline.getPeriodCount() == 1);
        if (contentTimeline.getWindowCount() != 1) {
            z = false;
        }
        Assertions.checkState(z);
        this.adGroupTimesUs = adGroupTimesUs;
        this.adCounts = adCounts;
        this.adsLoadedCounts = adsLoadedCounts;
        this.adsPlayedCounts = adsPlayedCounts;
        this.adDurationsUs = adDurationsUs;
        this.adResumePositionUs = adResumePositionUs;
        this.contentDurationUs = contentDurationUs;
    }

    public Period getPeriod(int periodIndex, Period period, boolean setIds) {
        this.timeline.getPeriod(periodIndex, period, setIds);
        period.set(period.id, period.uid, period.windowIndex, period.durationUs, period.getPositionInWindowUs(), this.adGroupTimesUs, this.adCounts, this.adsLoadedCounts, this.adsPlayedCounts, this.adDurationsUs, this.adResumePositionUs);
        return period;
    }

    public Window getWindow(int windowIndex, Window window, boolean setIds, long defaultPositionProjectionUs) {
        window = super.getWindow(windowIndex, window, setIds, defaultPositionProjectionUs);
        if (window.durationUs == C.TIME_UNSET) {
            window.durationUs = this.contentDurationUs;
        }
        return window;
    }
}
