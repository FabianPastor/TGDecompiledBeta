package org.telegram.messenger.exoplayer2.extractor;

import org.telegram.messenger.exoplayer2.util.Assertions;

public interface SeekMap {

    public static final class SeekPoints {
        public final SeekPoint first;
        public final SeekPoint second;

        public SeekPoints(SeekPoint point) {
            this(point, point);
        }

        public SeekPoints(SeekPoint first, SeekPoint second) {
            this.first = (SeekPoint) Assertions.checkNotNull(first);
            this.second = (SeekPoint) Assertions.checkNotNull(second);
        }

        public String toString() {
            String str;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            stringBuilder.append(this.first);
            if (this.first.equals(this.second)) {
                str = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(", ");
                stringBuilder2.append(this.second);
                str = stringBuilder2.toString();
            }
            stringBuilder.append(str);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    SeekPoints other = (SeekPoints) obj;
                    if (!this.first.equals(other.first) || !this.second.equals(other.second)) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (31 * this.first.hashCode()) + this.second.hashCode();
        }
    }

    public static final class Unseekable implements SeekMap {
        private final long durationUs;
        private final SeekPoints startSeekPoints;

        public Unseekable(long durationUs) {
            this(durationUs, 0);
        }

        public Unseekable(long durationUs, long startPosition) {
            this.durationUs = durationUs;
            this.startSeekPoints = new SeekPoints(startPosition == 0 ? SeekPoint.START : new SeekPoint(0, startPosition));
        }

        public boolean isSeekable() {
            return false;
        }

        public long getDurationUs() {
            return this.durationUs;
        }

        public SeekPoints getSeekPoints(long timeUs) {
            return this.startSeekPoints;
        }
    }

    long getDurationUs();

    SeekPoints getSeekPoints(long j);

    boolean isSeekable();
}
