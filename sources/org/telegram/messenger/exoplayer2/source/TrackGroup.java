package org.telegram.messenger.exoplayer2.source;

import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class TrackGroup {
    private final Format[] formats;
    private int hashCode;
    public final int length;

    public TrackGroup(Format... formats) {
        Assertions.checkState(formats.length > 0);
        this.formats = formats;
        this.length = formats.length;
    }

    public Format getFormat(int index) {
        return this.formats[index];
    }

    public int indexOf(Format format) {
        for (int i = 0; i < this.formats.length; i++) {
            if (format == this.formats[i]) {
                return i;
            }
        }
        return -1;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (31 * 17) + Arrays.hashCode(this.formats);
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
                TrackGroup other = (TrackGroup) obj;
                if (this.length != other.length || !Arrays.equals(this.formats, other.formats)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }
}
