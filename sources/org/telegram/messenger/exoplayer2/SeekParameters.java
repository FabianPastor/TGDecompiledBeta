package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SeekParameters {
    public static final SeekParameters CLOSEST_SYNC = new SeekParameters(Long.MAX_VALUE, Long.MAX_VALUE);
    public static final SeekParameters DEFAULT = EXACT;
    public static final SeekParameters EXACT = new SeekParameters(0, 0);
    public static final SeekParameters NEXT_SYNC = new SeekParameters(0, Long.MAX_VALUE);
    public static final SeekParameters PREVIOUS_SYNC = new SeekParameters(Long.MAX_VALUE, 0);
    public final long toleranceAfterUs;
    public final long toleranceBeforeUs;

    public SeekParameters(long j, long j2) {
        boolean z = false;
        Assertions.checkArgument(j >= 0);
        if (j2 >= 0) {
            z = true;
        }
        Assertions.checkArgument(z);
        this.toleranceBeforeUs = j;
        this.toleranceAfterUs = j2;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                SeekParameters seekParameters = (SeekParameters) obj;
                if (this.toleranceBeforeUs != seekParameters.toleranceBeforeUs || this.toleranceAfterUs != seekParameters.toleranceAfterUs) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((int) this.toleranceBeforeUs)) + ((int) this.toleranceAfterUs);
    }
}
