package org.telegram.messenger.exoplayer;

import org.telegram.messenger.exoplayer.util.Clock;

public interface TimeRange {

    public static final class DynamicTimeRange implements TimeRange {
        private final long bufferDepthUs;
        private final long elapsedRealtimeAtStartUs;
        private final long maxEndTimeUs;
        private final long minStartTimeUs;
        private final Clock systemClock;

        public DynamicTimeRange(long minStartTimeUs, long maxEndTimeUs, long elapsedRealtimeAtStartUs, long bufferDepthUs, Clock systemClock) {
            this.minStartTimeUs = minStartTimeUs;
            this.maxEndTimeUs = maxEndTimeUs;
            this.elapsedRealtimeAtStartUs = elapsedRealtimeAtStartUs;
            this.bufferDepthUs = bufferDepthUs;
            this.systemClock = systemClock;
        }

        public boolean isStatic() {
            return false;
        }

        public long[] getCurrentBoundsMs(long[] out) {
            out = getCurrentBoundsUs(out);
            out[0] = out[0] / 1000;
            out[1] = out[1] / 1000;
            return out;
        }

        public long[] getCurrentBoundsUs(long[] out) {
            if (out == null || out.length < 2) {
                out = new long[2];
            }
            long currentEndTimeUs = Math.min(this.maxEndTimeUs, (this.systemClock.elapsedRealtime() * 1000) - this.elapsedRealtimeAtStartUs);
            long currentStartTimeUs = this.minStartTimeUs;
            if (this.bufferDepthUs != -1) {
                currentStartTimeUs = Math.max(currentStartTimeUs, currentEndTimeUs - this.bufferDepthUs);
            }
            out[0] = currentStartTimeUs;
            out[1] = currentEndTimeUs;
            return out;
        }

        public int hashCode() {
            return ((((((((int) this.minStartTimeUs) + 527) * 31) + ((int) this.maxEndTimeUs)) * 31) + ((int) this.elapsedRealtimeAtStartUs)) * 31) + ((int) this.bufferDepthUs);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DynamicTimeRange other = (DynamicTimeRange) obj;
            if (other.minStartTimeUs == this.minStartTimeUs && other.maxEndTimeUs == this.maxEndTimeUs && other.elapsedRealtimeAtStartUs == this.elapsedRealtimeAtStartUs && other.bufferDepthUs == this.bufferDepthUs) {
                return true;
            }
            return false;
        }
    }

    public static final class StaticTimeRange implements TimeRange {
        private final long endTimeUs;
        private final long startTimeUs;

        public StaticTimeRange(long startTimeUs, long endTimeUs) {
            this.startTimeUs = startTimeUs;
            this.endTimeUs = endTimeUs;
        }

        public boolean isStatic() {
            return true;
        }

        public long[] getCurrentBoundsMs(long[] out) {
            out = getCurrentBoundsUs(out);
            out[0] = out[0] / 1000;
            out[1] = out[1] / 1000;
            return out;
        }

        public long[] getCurrentBoundsUs(long[] out) {
            if (out == null || out.length < 2) {
                out = new long[2];
            }
            out[0] = this.startTimeUs;
            out[1] = this.endTimeUs;
            return out;
        }

        public int hashCode() {
            return ((((int) this.startTimeUs) + 527) * 31) + ((int) this.endTimeUs);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            StaticTimeRange other = (StaticTimeRange) obj;
            if (other.startTimeUs == this.startTimeUs && other.endTimeUs == this.endTimeUs) {
                return true;
            }
            return false;
        }
    }

    long[] getCurrentBoundsMs(long[] jArr);

    long[] getCurrentBoundsUs(long[] jArr);

    boolean isStatic();
}
