package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.util.Assertions;

public final class PlaybackParameters {
    public static final PlaybackParameters DEFAULT = new PlaybackParameters(1.0f, 1.0f);
    public final float pitch;
    private final int scaledUsPerMs;
    public final float speed;

    public PlaybackParameters(float speed, float pitch) {
        boolean z = false;
        Assertions.checkArgument(speed > 0.0f);
        if (pitch > 0.0f) {
            z = true;
        }
        Assertions.checkArgument(z);
        this.speed = speed;
        this.pitch = pitch;
        this.scaledUsPerMs = Math.round(1000.0f * speed);
    }

    public long getMediaTimeUsForPlayoutTimeMs(long timeMs) {
        return ((long) this.scaledUsPerMs) * timeMs;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                PlaybackParameters other = (PlaybackParameters) obj;
                if (this.speed != other.speed || this.pitch != other.pitch) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((31 * 17) + Float.floatToRawIntBits(this.speed))) + Float.floatToRawIntBits(this.pitch);
    }
}
