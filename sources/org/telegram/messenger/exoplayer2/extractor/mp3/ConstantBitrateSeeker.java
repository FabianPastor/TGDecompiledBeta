package org.telegram.messenger.exoplayer2.extractor.mp3;

import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.Util;

final class ConstantBitrateSeeker implements Seeker {
    private static final int BITS_PER_BYTE = 8;
    private final int bitrate;
    private final long dataSize;
    private final long durationUs;
    private final long firstFramePosition;
    private final int frameSize;

    public ConstantBitrateSeeker(long j, long j2, MpegAudioHeader mpegAudioHeader) {
        this.firstFramePosition = j2;
        this.frameSize = mpegAudioHeader.frameSize;
        this.bitrate = mpegAudioHeader.bitrate;
        if (j == -1) {
            this.dataSize = -1;
            this.durationUs = C0542C.TIME_UNSET;
            return;
        }
        this.dataSize = j - j2;
        this.durationUs = getTimeUs(j);
    }

    public boolean isSeekable() {
        return this.dataSize != -1;
    }

    public SeekPoints getSeekPoints(long j) {
        if (this.dataSize == -1) {
            return new SeekPoints(new SeekPoint(0, this.firstFramePosition));
        }
        long constrainValue = Util.constrainValue((((((long) this.bitrate) * j) / 8000000) / ((long) this.frameSize)) * ((long) this.frameSize), 0, this.dataSize - ((long) this.frameSize));
        long j2 = this.firstFramePosition + constrainValue;
        long timeUs = getTimeUs(j2);
        SeekPoint seekPoint = new SeekPoint(timeUs, j2);
        if (timeUs < j) {
            if (constrainValue != this.dataSize - ((long) this.frameSize)) {
                constrainValue = j2 + ((long) this.frameSize);
                return new SeekPoints(seekPoint, new SeekPoint(getTimeUs(constrainValue), constrainValue));
            }
        }
        return new SeekPoints(seekPoint);
    }

    public long getTimeUs(long j) {
        return ((Math.max(0, j - this.firstFramePosition) * C0542C.MICROS_PER_SECOND) * 8) / ((long) this.bitrate);
    }

    public long getDurationUs() {
        return this.durationUs;
    }
}
