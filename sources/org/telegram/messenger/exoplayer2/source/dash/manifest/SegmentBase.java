package org.telegram.messenger.exoplayer2.source.dash.manifest;

import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class SegmentBase {
    final RangedUri initialization;
    final long presentationTimeOffset;
    final long timescale;

    public static class SegmentTimelineElement {
        final long duration;
        final long startTime;

        public SegmentTimelineElement(long j, long j2) {
            this.startTime = j;
            this.duration = j2;
        }
    }

    public static abstract class MultiSegmentBase extends SegmentBase {
        final long duration;
        final List<SegmentTimelineElement> segmentTimeline;
        final int startNumber;

        public abstract int getSegmentCount(long j);

        public abstract RangedUri getSegmentUrl(Representation representation, int i);

        public MultiSegmentBase(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list) {
            super(rangedUri, j, j2);
            this.startNumber = i;
            this.duration = j3;
            this.segmentTimeline = list;
        }

        public int getSegmentNum(long j, long j2) {
            int firstSegmentNum = getFirstSegmentNum();
            j2 = getSegmentCount(j2);
            if (j2 == null) {
                return firstSegmentNum;
            }
            if (this.segmentTimeline == null) {
                j = ((int) (j / ((this.duration * C0542C.MICROS_PER_SECOND) / this.timescale))) + this.startNumber;
                if (j >= firstSegmentNum) {
                    if (j2 == -1) {
                        firstSegmentNum = j;
                    } else {
                        firstSegmentNum = Math.min(j, (firstSegmentNum + j2) - 1);
                    }
                }
                return firstSegmentNum;
            }
            int i = (j2 + firstSegmentNum) - 1;
            j2 = firstSegmentNum;
            while (j2 <= i) {
                int i2 = ((i - j2) / 2) + j2;
                long segmentTimeUs = getSegmentTimeUs(i2);
                if (segmentTimeUs < j) {
                    j2 = i2 + 1;
                } else if (segmentTimeUs <= j) {
                    return i2;
                } else {
                    i = i2 - 1;
                }
            }
            if (j2 != firstSegmentNum) {
                j2 = i;
            }
            return j2;
        }

        public final long getSegmentDurationUs(int i, long j) {
            if (this.segmentTimeline != null) {
                return (((SegmentTimelineElement) this.segmentTimeline.get(i - this.startNumber)).duration * 1000000) / this.timescale;
            }
            int segmentCount = getSegmentCount(j);
            long segmentTimeUs = (segmentCount == -1 || i != (getFirstSegmentNum() + segmentCount) - 1) ? (this.duration * 1000000) / this.timescale : j - getSegmentTimeUs(i);
            return segmentTimeUs;
        }

        public final long getSegmentTimeUs(int i) {
            long j;
            if (this.segmentTimeline != null) {
                j = ((SegmentTimelineElement) this.segmentTimeline.get(i - this.startNumber)).startTime - this.presentationTimeOffset;
            } else {
                j = ((long) (i - this.startNumber)) * this.duration;
            }
            return Util.scaleLargeTimestamp(j, C0542C.MICROS_PER_SECOND, this.timescale);
        }

        public int getFirstSegmentNum() {
            return this.startNumber;
        }

        public boolean isExplicit() {
            return this.segmentTimeline != null;
        }
    }

    public static class SingleSegmentBase extends SegmentBase {
        final long indexLength;
        final long indexStart;

        public SingleSegmentBase(RangedUri rangedUri, long j, long j2, long j3, long j4) {
            super(rangedUri, j, j2);
            this.indexStart = j3;
            this.indexLength = j4;
        }

        public SingleSegmentBase() {
            this(null, 1, 0, 0, 0);
        }

        public RangedUri getIndex() {
            return this.indexLength <= 0 ? null : new RangedUri(null, this.indexStart, this.indexLength);
        }
    }

    public static class SegmentList extends MultiSegmentBase {
        final List<RangedUri> mediaSegments;

        public boolean isExplicit() {
            return true;
        }

        public SegmentList(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list, List<RangedUri> list2) {
            super(rangedUri, j, j2, i, j3, list);
            this.mediaSegments = list2;
        }

        public RangedUri getSegmentUrl(Representation representation, int i) {
            return (RangedUri) this.mediaSegments.get(i - this.startNumber);
        }

        public int getSegmentCount(long j) {
            return this.mediaSegments.size();
        }
    }

    public static class SegmentTemplate extends MultiSegmentBase {
        final UrlTemplate initializationTemplate;
        final UrlTemplate mediaTemplate;

        public SegmentTemplate(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list, UrlTemplate urlTemplate, UrlTemplate urlTemplate2) {
            super(rangedUri, j, j2, i, j3, list);
            this.initializationTemplate = urlTemplate;
            this.mediaTemplate = urlTemplate2;
        }

        public RangedUri getInitialization(Representation representation) {
            if (this.initializationTemplate != null) {
                return new RangedUri(this.initializationTemplate.buildUri(representation.format.id, 0, representation.format.bitrate, 0), 0, -1);
            }
            return super.getInitialization(representation);
        }

        public RangedUri getSegmentUrl(Representation representation, int i) {
            long j;
            Representation representation2 = representation;
            if (this.segmentTimeline != null) {
                j = ((SegmentTimelineElement) r0.segmentTimeline.get(i - r0.startNumber)).startTime;
            } else {
                j = ((long) (i - r0.startNumber)) * r0.duration;
            }
            long j2 = j;
            return new RangedUri(r0.mediaTemplate.buildUri(representation2.format.id, i, representation2.format.bitrate, j2), 0, -1);
        }

        public int getSegmentCount(long j) {
            if (this.segmentTimeline != null) {
                return this.segmentTimeline.size();
            }
            return j != C0542C.TIME_UNSET ? (int) Util.ceilDivide(j, (this.duration * C0542C.MICROS_PER_SECOND) / this.timescale) : -1;
        }
    }

    public SegmentBase(RangedUri rangedUri, long j, long j2) {
        this.initialization = rangedUri;
        this.timescale = j;
        this.presentationTimeOffset = j2;
    }

    public RangedUri getInitialization(Representation representation) {
        return this.initialization;
    }

    public long getPresentationTimeOffsetUs() {
        return Util.scaleLargeTimestamp(this.presentationTimeOffset, C0542C.MICROS_PER_SECOND, this.timescale);
    }
}
