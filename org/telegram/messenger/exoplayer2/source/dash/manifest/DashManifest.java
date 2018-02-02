package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;

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
        if (this.durationMs == C.TIME_UNSET) {
            return C.TIME_UNSET;
        }
        return this.durationMs - ((Period) this.periods.get(index)).startMs;
    }

    public final long getPeriodDurationUs(int index) {
        return C.msToUs(getPeriodDurationMs(index));
    }

    public final DashManifest copy(List<RepresentationKey> representationKeys) {
        LinkedList<RepresentationKey> linkedList = new LinkedList(representationKeys);
        Collections.sort(linkedList);
        linkedList.add(new RepresentationKey(-1, -1, -1));
        ArrayList<Period> copyPeriods = new ArrayList();
        long shiftMs = 0;
        for (int periodIndex = 0; periodIndex < getPeriodCount(); periodIndex++) {
            if (((RepresentationKey) linkedList.peek()).periodIndex != periodIndex) {
                long periodDurationMs = getPeriodDurationMs(periodIndex);
                if (periodDurationMs != C.TIME_UNSET) {
                    shiftMs += periodDurationMs;
                }
            } else {
                Period period = getPeriod(periodIndex);
                copyPeriods.add(new Period(period.id, period.startMs - shiftMs, copyAdaptationSets(period.adaptationSets, linkedList), period.eventStreams));
            }
        }
        return new DashManifest(this.availabilityStartTimeMs, this.durationMs != C.TIME_UNSET ? this.durationMs - shiftMs : C.TIME_UNSET, this.minBufferTimeMs, this.dynamic, this.minUpdatePeriodMs, this.timeShiftBufferDepthMs, this.suggestedPresentationDelayMs, this.publishTimeMs, this.utcTiming, this.location, copyPeriods);
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
