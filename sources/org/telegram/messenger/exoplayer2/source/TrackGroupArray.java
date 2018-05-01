package org.telegram.messenger.exoplayer2.source;

import java.util.Arrays;

public final class TrackGroupArray {
    public static final TrackGroupArray EMPTY = new TrackGroupArray(new TrackGroup[0]);
    private int hashCode;
    public final int length;
    private final TrackGroup[] trackGroups;

    public TrackGroupArray(TrackGroup... trackGroupArr) {
        this.trackGroups = trackGroupArr;
        this.length = trackGroupArr.length;
    }

    public TrackGroup get(int i) {
        return this.trackGroups[i];
    }

    public int indexOf(TrackGroup trackGroup) {
        for (int i = 0; i < this.length; i++) {
            if (this.trackGroups[i] == trackGroup) {
                return i;
            }
        }
        return -1;
    }

    public boolean isEmpty() {
        return this.length == 0;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = Arrays.hashCode(this.trackGroups);
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                TrackGroupArray trackGroupArray = (TrackGroupArray) obj;
                if (this.length != trackGroupArray.length || Arrays.equals(this.trackGroups, trackGroupArray.trackGroups) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }
}
