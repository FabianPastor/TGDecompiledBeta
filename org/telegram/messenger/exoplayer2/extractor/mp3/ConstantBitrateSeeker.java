package org.telegram.messenger.exoplayer2.extractor.mp3;

import org.telegram.messenger.exoplayer2.C;

final class ConstantBitrateSeeker implements Seeker {
    private static final int BITS_PER_BYTE = 8;
    private final int bitrate;
    private final long durationUs;
    private final long firstFramePosition;

    public ConstantBitrateSeeker(long firstFramePosition, int bitrate, long inputLength) {
        this.firstFramePosition = firstFramePosition;
        this.bitrate = bitrate;
        this.durationUs = inputLength == -1 ? C.TIME_UNSET : getTimeUs(inputLength);
    }

    public boolean isSeekable() {
        return this.durationUs != C.TIME_UNSET;
    }

    public long getPosition(long timeUs) {
        return this.durationUs == C.TIME_UNSET ? 0 : this.firstFramePosition + ((((long) this.bitrate) * timeUs) / 8000000);
    }

    public long getTimeUs(long position) {
        return ((Math.max(0, position - this.firstFramePosition) * C.MICROS_PER_SECOND) * 8) / ((long) this.bitrate);
    }

    public long getDurationUs() {
        return this.durationUs;
    }
}
