package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;

public class DashManifest {
    public final long availabilityStartTimeMs;
    public final long durationMs;
    public final boolean dynamic;
    public final Uri location;
    public final long minBufferTimeMs;
    public final long minUpdatePeriodMs;
    private final List<Period> periods;
    public final long publishTimeMs;
    public final long suggestedPresentationDelayMs;
    public final long timeShiftBufferDepthMs;
    public final UtcTimingElement utcTiming;

    public DashManifest(long availabilityStartTimeMs, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdatePeriodMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, long publishTimeMs, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        this.availabilityStartTimeMs = availabilityStartTimeMs;
        this.durationMs = durationMs;
        this.minBufferTimeMs = minBufferTimeMs;
        this.dynamic = dynamic;
        this.minUpdatePeriodMs = minUpdatePeriodMs;
        this.timeShiftBufferDepthMs = timeShiftBufferDepthMs;
        this.suggestedPresentationDelayMs = suggestedPresentationDelayMs;
        this.publishTimeMs = publishTimeMs;
        this.utcTiming = utcTiming;
        this.location = location;
        r0.periods = periods == null ? Collections.emptyList() : periods;
    }

    public final int getPeriodCount() {
        return this.periods.size();
    }

    public final Period getPeriod(int index) {
        return (Period) this.periods.get(index);
    }

    public final long getPeriodDurationMs(int index) {
        if (index == this.periods.size() - 1) {
            return this.durationMs == C0542C.TIME_UNSET ? C0542C.TIME_UNSET : this.durationMs - ((Period) this.periods.get(index)).startMs;
        } else {
            return ((Period) this.periods.get(index + 1)).startMs - ((Period) this.periods.get(index)).startMs;
        }
    }

    public final long getPeriodDurationUs(int index) {
        return C0542C.msToUs(getPeriodDurationMs(index));
    }

    public final DashManifest copy(List<RepresentationKey> representationKeys) {
        long j;
        long shiftMs;
        DashManifest dashManifest = this;
        LinkedList<RepresentationKey> keys = new LinkedList(representationKeys);
        Collections.sort(keys);
        keys.add(new RepresentationKey(-1, -1, -1));
        ArrayList<Period> copyPeriods = new ArrayList();
        int periodIndex = 0;
        long shiftMs2 = 0;
        while (true) {
            int periodIndex2 = periodIndex;
            int periodCount = getPeriodCount();
            j = C0542C.TIME_UNSET;
            if (periodIndex2 >= periodCount) {
                break;
            }
            if (((RepresentationKey) keys.peek()).periodIndex != periodIndex2) {
                long periodDurationMs = getPeriodDurationMs(periodIndex2);
                if (periodDurationMs != C0542C.TIME_UNSET) {
                    shiftMs = shiftMs2 + periodDurationMs;
                } else {
                    shiftMs = shiftMs2;
                }
                shiftMs2 = shiftMs;
            } else {
                Period period = getPeriod(periodIndex2);
                ArrayList<AdaptationSet> copyAdaptationSets = copyAdaptationSets(period.adaptationSets, keys);
                copyPeriods.add(new Period(period.id, period.startMs - shiftMs2, copyAdaptationSets, period.eventStreams));
            }
            periodIndex = periodIndex2 + 1;
        }
        if (dashManifest.durationMs != C0542C.TIME_UNSET) {
            j = dashManifest.durationMs - shiftMs2;
        }
        long newDuration = j;
        shiftMs = dashManifest.availabilityStartTimeMs;
        long j2 = dashManifest.minBufferTimeMs;
        boolean z = dashManifest.dynamic;
        long j3 = dashManifest.minUpdatePeriodMs;
        long j4 = dashManifest.timeShiftBufferDepthMs;
        long j5 = dashManifest.suggestedPresentationDelayMs;
        long newDuration2 = newDuration;
        newDuration = dashManifest.publishTimeMs;
        long j6 = newDuration;
        return new DashManifest(shiftMs, newDuration2, j2, z, j3, j4, j5, j6, dashManifest.utcTiming, dashManifest.location, copyPeriods);
    }

    private static ArrayList<AdaptationSet> copyAdaptationSets(List<AdaptationSet> adaptationSets, LinkedList<RepresentationKey> keys) {
        RepresentationKey key = (RepresentationKey) keys.poll();
        int periodIndex = key.periodIndex;
        ArrayList<AdaptationSet> copyAdaptationSets = new ArrayList();
        do {
            int adaptationSetIndex = key.adaptationSetIndex;
            AdaptationSet adaptationSet = (AdaptationSet) adaptationSets.get(adaptationSetIndex);
            List<Representation> representations = adaptationSet.representations;
            ArrayList<Representation> copyRepresentations = new ArrayList();
            do {
                copyRepresentations.add((Representation) representations.get(key.representationIndex));
                key = (RepresentationKey) keys.poll();
                if (key.periodIndex != periodIndex) {
                    break;
                }
            } while (key.adaptationSetIndex == adaptationSetIndex);
            copyAdaptationSets.add(new AdaptationSet(adaptationSet.id, adaptationSet.type, copyRepresentations, adaptationSet.accessibilityDescriptors, adaptationSet.supplementalProperties));
        } while (key.periodIndex == periodIndex);
        keys.addFirst(key);
        return copyAdaptationSets;
    }
}
