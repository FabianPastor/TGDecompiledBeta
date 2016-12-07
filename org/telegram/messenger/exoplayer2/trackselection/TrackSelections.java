package org.telegram.messenger.exoplayer2.trackselection;

import java.util.Arrays;

public final class TrackSelections<T> {
    private int hashCode;
    public final T info;
    public final int length;
    private final TrackSelection[] trackSelections;

    public TrackSelections(T info, TrackSelection... trackSelections) {
        this.info = info;
        this.trackSelections = trackSelections;
        this.length = trackSelections.length;
    }

    public TrackSelection get(int index) {
        return this.trackSelections[index];
    }

    public TrackSelection[] getAll() {
        return (TrackSelection[]) this.trackSelections.clone();
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = Arrays.hashCode(this.trackSelections) + 527;
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Arrays.equals(this.trackSelections, ((TrackSelections) obj).trackSelections);
    }
}
