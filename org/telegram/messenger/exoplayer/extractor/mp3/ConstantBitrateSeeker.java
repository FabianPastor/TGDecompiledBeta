package org.telegram.messenger.exoplayer.extractor.mp3;

import org.telegram.messenger.exoplayer.C;

final class ConstantBitrateSeeker implements Seeker {
    private static final int BITS_PER_BYTE = 8;
    private final int bitrate;
    private final long durationUs;
    private final long firstFramePosition;

    public ConstantBitrateSeeker(long firstFramePosition, int bitrate, long inputLength) {
        long j = -1;
        this.firstFramePosition = firstFramePosition;
        this.bitrate = bitrate;
        if (inputLength != -1) {
            j = getTimeUs(inputLength);
        }
        this.durationUs = j;
    }

    public boolean isSeekable() {
        return this.durationUs != -1;
    }

    public long getPosition(long timeUs) {
        return this.durationUs == -1 ? 0 : this.firstFramePosition + ((((long) this.bitrate) * timeUs) / 8000000);
    }

    public long getTimeUs(long position) {
        return ((Math.max(0, position - this.firstFramePosition) * C.MICROS_PER_SECOND) * 8) / ((long) this.bitrate);
    }

    public long getDurationUs() {
        return this.durationUs;
    }
}
