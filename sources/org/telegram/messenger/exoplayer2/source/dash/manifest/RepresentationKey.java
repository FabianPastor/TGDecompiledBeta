package org.telegram.messenger.exoplayer2.source.dash.manifest;

public final class RepresentationKey implements Comparable<RepresentationKey> {
    public final int adaptationSetIndex;
    public final int periodIndex;
    public final int representationIndex;

    public RepresentationKey(int periodIndex, int adaptationSetIndex, int representationIndex) {
        this.periodIndex = periodIndex;
        this.adaptationSetIndex = adaptationSetIndex;
        this.representationIndex = representationIndex;
    }

    public String toString() {
        return this.periodIndex + "." + this.adaptationSetIndex + "." + this.representationIndex;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RepresentationKey that = (RepresentationKey) o;
        if (this.periodIndex == that.periodIndex && this.adaptationSetIndex == that.adaptationSetIndex && this.representationIndex == that.representationIndex) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((this.periodIndex * 31) + this.adaptationSetIndex) * 31) + this.representationIndex;
    }

    public int compareTo(RepresentationKey o) {
        int result = this.periodIndex - o.periodIndex;
        if (result != 0) {
            return result;
        }
        result = this.adaptationSetIndex - o.adaptationSetIndex;
        if (result == 0) {
            return this.representationIndex - o.representationIndex;
        }
        return result;
    }
}
