package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.util.Assertions;

public final class PlaybackParameters {
    public static final PlaybackParameters DEFAULT = new PlaybackParameters(1.0f, 1.0f);
    public final float pitch;
    private final int scaledUsPerMs;
    public final float speed;

    public PlaybackParameters(float f, float f2) {
        boolean z = false;
        Assertions.checkArgument(f > 0.0f);
        if (f2 > 0.0f) {
            z = true;
        }
        Assertions.checkArgument(z);
        this.speed = f;
        this.pitch = f2;
        this.scaledUsPerMs = Math.round(f * 1000.0f);
    }

    public long getMediaTimeUsForPlayoutTimeMs(long j) {
        return j * ((long) this.scaledUsPerMs);
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                PlaybackParameters playbackParameters = (PlaybackParameters) obj;
                if (this.speed != playbackParameters.speed || this.pitch != playbackParameters.pitch) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * (527 + Float.floatToRawIntBits(this.speed))) + Float.floatToRawIntBits(this.pitch);
    }
}
