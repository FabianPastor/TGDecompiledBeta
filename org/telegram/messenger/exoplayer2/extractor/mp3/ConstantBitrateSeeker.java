package org.telegram.messenger.exoplayer2.extractor.mp3;

import org.telegram.messenger.exoplayer2.C;
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

    public ConstantBitrateSeeker(long inputLength, long firstFramePosition, MpegAudioHeader mpegAudioHeader) {
        this.firstFramePosition = firstFramePosition;
        this.frameSize = mpegAudioHeader.frameSize;
        this.bitrate = mpegAudioHeader.bitrate;
        if (inputLength == -1) {
            this.dataSize = -1;
            this.durationUs = C.TIME_UNSET;
            return;
        }
        this.dataSize = inputLength - firstFramePosition;
        this.durationUs = getTimeUs(inputLength);
    }

    public boolean isSeekable() {
        return this.dataSize != -1;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        if (this.dataSize == -1) {
            return new SeekPoints(new SeekPoint(0, this.firstFramePosition));
        }
        long positionOffset = Util.constrainValue((((((long) this.bitrate) * timeUs) / 8000000) / ((long) this.frameSize)) * ((long) this.frameSize), 0, this.dataSize - ((long) this.frameSize));
        long seekPosition = this.firstFramePosition + positionOffset;
        long seekTimeUs = getTimeUs(seekPosition);
        SeekPoint seekPoint = new SeekPoint(seekTimeUs, seekPosition);
        if (seekTimeUs >= timeUs || positionOffset == this.dataSize - ((long) this.frameSize)) {
            return new SeekPoints(seekPoint);
        }
        long secondSeekPosition = seekPosition + ((long) this.frameSize);
        return new SeekPoints(seekPoint, new SeekPoint(getTimeUs(secondSeekPosition), secondSeekPosition));
    }

    public long getTimeUs(long position) {
        return ((Math.max(0, position - this.firstFramePosition) * C.MICROS_PER_SECOND) * 8) / ((long) this.bitrate);
    }

    public long getDurationUs() {
        return this.durationUs;
    }
}
