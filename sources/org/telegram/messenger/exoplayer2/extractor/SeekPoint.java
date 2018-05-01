package org.telegram.messenger.exoplayer2.extractor;

public final class SeekPoint {
    public static final SeekPoint START = new SeekPoint(0, 0);
    public final long position;
    public final long timeUs;

    public SeekPoint(long j, long j2) {
        this.timeUs = j;
        this.position = j2;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[timeUs=");
        stringBuilder.append(this.timeUs);
        stringBuilder.append(", position=");
        stringBuilder.append(this.position);
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
                SeekPoint seekPoint = (SeekPoint) obj;
                if (this.timeUs != seekPoint.timeUs || this.position != seekPoint.position) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((int) this.timeUs)) + ((int) this.position);
    }
}
