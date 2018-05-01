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

    public DashManifest(long j, long j2, long j3, boolean z, long j4, long j5, long j6, long j7, UtcTimingElement utcTimingElement, Uri uri, List<Period> list) {
        this.availabilityStartTimeMs = j;
        this.durationMs = j2;
        this.minBufferTimeMs = j3;
        this.dynamic = z;
        this.minUpdatePeriodMs = j4;
        this.timeShiftBufferDepthMs = j5;
        this.suggestedPresentationDelayMs = j6;
        this.publishTimeMs = j7;
        this.utcTiming = utcTimingElement;
        this.location = uri;
        r0.periods = list == null ? Collections.emptyList() : list;
    }

    public final int getPeriodCount() {
        return this.periods.size();
    }

    public final Period getPeriod(int i) {
        return (Period) this.periods.get(i);
    }

    public final long getPeriodDurationMs(int i) {
        if (i == this.periods.size() - 1) {
            return this.durationMs == C0542C.TIME_UNSET ? C0542C.TIME_UNSET : this.durationMs - ((Period) this.periods.get(i)).startMs;
        } else {
            return ((Period) this.periods.get(i + 1)).startMs - ((Period) this.periods.get(i)).startMs;
        }
    }

    public final long getPeriodDurationUs(int i) {
        return C0542C.msToUs(getPeriodDurationMs(i));
    }

    public final DashManifest copy(List<RepresentationKey> list) {
        long j;
        long periodDurationMs;
        DashManifest dashManifest = this;
        LinkedList linkedList = new LinkedList(list);
        Collections.sort(linkedList);
        linkedList.add(new RepresentationKey(-1, -1, -1));
        ArrayList arrayList = new ArrayList();
        long j2 = 0;
        int i = 0;
        while (true) {
            int periodCount = getPeriodCount();
            j = C0542C.TIME_UNSET;
            if (i >= periodCount) {
                break;
            }
            if (((RepresentationKey) linkedList.peek()).periodIndex != i) {
                periodDurationMs = getPeriodDurationMs(i);
                if (periodDurationMs != C0542C.TIME_UNSET) {
                    j2 += periodDurationMs;
                }
            } else {
                Period period = getPeriod(i);
                List copyAdaptationSets = copyAdaptationSets(period.adaptationSets, linkedList);
                arrayList.add(new Period(period.id, period.startMs - j2, copyAdaptationSets, period.eventStreams));
            }
            i++;
        }
        if (dashManifest.durationMs != C0542C.TIME_UNSET) {
            j = dashManifest.durationMs - j2;
        }
        long j3 = dashManifest.availabilityStartTimeMs;
        periodDurationMs = dashManifest.minBufferTimeMs;
        boolean z = dashManifest.dynamic;
        long j4 = dashManifest.minUpdatePeriodMs;
        long j5 = dashManifest.timeShiftBufferDepthMs;
        ArrayList arrayList2 = arrayList;
        long j6 = dashManifest.suggestedPresentationDelayMs;
        long j7 = dashManifest.publishTimeMs;
        return new DashManifest(j3, j, periodDurationMs, z, j4, j5, j6, j7, dashManifest.utcTiming, dashManifest.location, arrayList2);
    }

    private static ArrayList<AdaptationSet> copyAdaptationSets(List<AdaptationSet> list, LinkedList<RepresentationKey> linkedList) {
        RepresentationKey representationKey = (RepresentationKey) linkedList.poll();
        int i = representationKey.periodIndex;
        ArrayList<AdaptationSet> arrayList = new ArrayList();
        do {
            int i2 = representationKey.adaptationSetIndex;
            AdaptationSet adaptationSet = (AdaptationSet) list.get(i2);
            List list2 = adaptationSet.representations;
            List arrayList2 = new ArrayList();
            do {
                arrayList2.add((Representation) list2.get(representationKey.representationIndex));
                representationKey = (RepresentationKey) linkedList.poll();
                if (representationKey.periodIndex != i) {
                    break;
                }
            } while (representationKey.adaptationSetIndex == i2);
            arrayList.add(new AdaptationSet(adaptationSet.id, adaptationSet.type, arrayList2, adaptationSet.accessibilityDescriptors, adaptationSet.supplementalProperties));
        } while (representationKey.periodIndex == i);
        linkedList.addFirst(representationKey);
        return arrayList;
    }
}
