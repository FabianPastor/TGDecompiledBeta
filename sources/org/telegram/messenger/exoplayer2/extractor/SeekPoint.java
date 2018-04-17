package org.telegram.messenger.exoplayer2.extractor;

public final class SeekPoint {
    public static final SeekPoint START = new SeekPoint(0, 0);
    public final long position;
    public final long timeUs;

    public SeekPoint(long timeUs, long position) {
        this.timeUs = timeUs;
        this.position = position;
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
                SeekPoint other = (SeekPoint) obj;
                if (this.timeUs != other.timeUs || this.position != other.position) {
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
