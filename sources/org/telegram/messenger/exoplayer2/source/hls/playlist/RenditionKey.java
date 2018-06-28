package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RenditionKey implements Comparable<RenditionKey> {
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_SUBTITLE = 2;
    public static final int TYPE_VARIANT = 0;
    public final int trackIndex;
    public final int type;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public RenditionKey(int type, int trackIndex) {
        this.type = type;
        this.trackIndex = trackIndex;
    }

    public String toString() {
        return this.type + "." + this.trackIndex;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RenditionKey that = (RenditionKey) o;
        if (this.type == that.type && this.trackIndex == that.trackIndex) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.type * 31) + this.trackIndex;
    }

    public int compareTo(RenditionKey other) {
        int result = this.type - other.type;
        if (result == 0) {
            return this.trackIndex - other.trackIndex;
        }
        return result;
    }
}
