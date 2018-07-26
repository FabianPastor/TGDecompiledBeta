package org.telegram.messenger.exoplayer2.source.dash.manifest;

import java.util.List;
import org.telegram.messenger.exoplayer2.C0621C;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class SegmentBase {
    final RangedUri initialization;
    final long presentationTimeOffset;
    final long timescale;

    public static abstract class MultiSegmentBase extends SegmentBase {
        final long duration;
        final List<SegmentTimelineElement> segmentTimeline;
        final long startNumber;

        public abstract int getSegmentCount(long j);

        public abstract RangedUri getSegmentUrl(Representation representation, long j);

        public MultiSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> segmentTimeline) {
            super(initialization, timescale, presentationTimeOffset);
            this.startNumber = startNumber;
            this.duration = duration;
            this.segmentTimeline = segmentTimeline;
        }

        public long getSegmentNum(long timeUs, long periodDurationUs) {
            long firstSegmentNum = getFirstSegmentNum();
            long segmentCount = (long) getSegmentCount(periodDurationUs);
            if (segmentCount == 0) {
                return firstSegmentNum;
            }
            if (this.segmentTimeline == null) {
                long segmentNum = this.startNumber + (timeUs / ((this.duration * 1000000) / this.timescale));
                if (segmentNum < firstSegmentNum) {
                    return firstSegmentNum;
                }
                if (segmentCount == -1) {
                    return segmentNum;
                }
                return Math.min(segmentNum, (firstSegmentNum + segmentCount) - 1);
            }
            long lowIndex = firstSegmentNum;
            long highIndex = (firstSegmentNum + segmentCount) - 1;
            while (lowIndex <= highIndex) {
                long midIndex = lowIndex + ((highIndex - lowIndex) / 2);
                long midTimeUs = getSegmentTimeUs(midIndex);
                if (midTimeUs < timeUs) {
                    lowIndex = midIndex + 1;
                } else if (midTimeUs <= timeUs) {
                    return midIndex;
                } else {
                    highIndex = midIndex - 1;
                }
            }
            if (lowIndex != firstSegmentNum) {
                lowIndex = highIndex;
            }
            return lowIndex;
        }

        public final long getSegmentDurationUs(long sequenceNumber, long periodDurationUs) {
            if (this.segmentTimeline != null) {
                return (((SegmentTimelineElement) this.segmentTimeline.get((int) (sequenceNumber - this.startNumber))).duration * 1000000) / this.timescale;
            }
            int segmentCount = getSegmentCount(periodDurationUs);
            return (segmentCount == -1 || sequenceNumber != (getFirstSegmentNum() + ((long) segmentCount)) - 1) ? (this.duration * 1000000) / this.timescale : periodDurationUs - getSegmentTimeUs(sequenceNumber);
        }

        public final long getSegmentTimeUs(long sequenceNumber) {
            long unscaledSegmentTime;
            if (this.segmentTimeline != null) {
                unscaledSegmentTime = ((SegmentTimelineElement) this.segmentTimeline.get((int) (sequenceNumber - this.startNumber))).startTime - this.presentationTimeOffset;
            } else {
                unscaledSegmentTime = (sequenceNumber - this.startNumber) * this.duration;
            }
            return Util.scaleLargeTimestamp(unscaledSegmentTime, 1000000, this.timescale);
        }

        public long getFirstSegmentNum() {
            return this.startNumber;
        }

        public boolean isExplicit() {
            return this.segmentTimeline != null;
        }
    }

    public static class SegmentList extends MultiSegmentBase {
        final List<RangedUri> mediaSegments;

        public SegmentList(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> segmentTimeline, List<RangedUri> mediaSegments) {
            super(initialization, timescale, presentationTimeOffset, startNumber, duration, segmentTimeline);
            this.mediaSegments = mediaSegments;
        }

        public RangedUri getSegmentUrl(Representation representation, long sequenceNumber) {
            return (RangedUri) this.mediaSegments.get((int) (sequenceNumber - this.startNumber));
        }

        public int getSegmentCount(long periodDurationUs) {
            return this.mediaSegments.size();
        }

        public boolean isExplicit() {
            return true;
        }
    }

    public static class SegmentTemplate extends MultiSegmentBase {
        final UrlTemplate initializationTemplate;
        final UrlTemplate mediaTemplate;

        public SegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> segmentTimeline, UrlTemplate initializationTemplate, UrlTemplate mediaTemplate) {
            super(initialization, timescale, presentationTimeOffset, startNumber, duration, segmentTimeline);
            this.initializationTemplate = initializationTemplate;
            this.mediaTemplate = mediaTemplate;
        }

        public RangedUri getInitialization(Representation representation) {
            if (this.initializationTemplate != null) {
                return new RangedUri(this.initializationTemplate.buildUri(representation.format.id, 0, representation.format.bitrate, 0), 0, -1);
            }
            return super.getInitialization(representation);
        }

        public RangedUri getSegmentUrl(Representation representation, long sequenceNumber) {
            long time;
            if (this.segmentTimeline != null) {
                time = ((SegmentTimelineElement) this.segmentTimeline.get((int) (sequenceNumber - this.startNumber))).startTime;
            } else {
                time = (sequenceNumber - this.startNumber) * this.duration;
            }
            return new RangedUri(this.mediaTemplate.buildUri(representation.format.id, sequenceNumber, representation.format.bitrate, time), 0, -1);
        }

        public int getSegmentCount(long periodDurationUs) {
            if (this.segmentTimeline != null) {
                return this.segmentTimeline.size();
            }
            if (periodDurationUs != C0621C.TIME_UNSET) {
                return (int) Util.ceilDivide(periodDurationUs, (this.duration * 1000000) / this.timescale);
            }
            return -1;
        }
    }

    public static class SegmentTimelineElement {
        final long duration;
        final long startTime;

        public SegmentTimelineElement(long startTime, long duration) {
            this.startTime = startTime;
            this.duration = duration;
        }
    }

    public static class SingleSegmentBase extends SegmentBase {
        final long indexLength;
        final long indexStart;

        public SingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long indexStart, long indexLength) {
            super(initialization, timescale, presentationTimeOffset);
            this.indexStart = indexStart;
            this.indexLength = indexLength;
        }

        public SingleSegmentBase() {
            this(null, 1, 0, 0, 0);
        }

        public RangedUri getIndex() {
            return this.indexLength <= 0 ? null : new RangedUri(null, this.indexStart, this.indexLength);
        }
    }

    public SegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset) {
        this.initialization = initialization;
        this.timescale = timescale;
        this.presentationTimeOffset = presentationTimeOffset;
    }

    public RangedUri getInitialization(Representation representation) {
        return this.initialization;
    }

    public long getPresentationTimeOffsetUs() {
        return Util.scaleLargeTimestamp(this.presentationTimeOffset, 1000000, this.timescale);
    }
}
