package org.telegram.messenger.exoplayer2.extractor.wav;

import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.Util;

final class WavHeader implements SeekMap {
    private final int averageBytesPerSecond;
    private final int bitsPerSample;
    private final int blockAlignment;
    private long dataSize;
    private long dataStartPosition;
    private final int encoding;
    private final int numChannels;
    private final int sampleRateHz;

    public boolean isSeekable() {
        return true;
    }

    public WavHeader(int i, int i2, int i3, int i4, int i5, int i6) {
        this.numChannels = i;
        this.sampleRateHz = i2;
        this.averageBytesPerSecond = i3;
        this.blockAlignment = i4;
        this.bitsPerSample = i5;
        this.encoding = i6;
    }

    public void setDataBounds(long j, long j2) {
        this.dataStartPosition = j;
        this.dataSize = j2;
    }

    public boolean hasDataBounds() {
        return (this.dataStartPosition == 0 || this.dataSize == 0) ? false : true;
    }

    public long getDurationUs() {
        return ((this.dataSize / ((long) this.blockAlignment)) * C0542C.MICROS_PER_SECOND) / ((long) this.sampleRateHz);
    }

    public SeekPoints getSeekPoints(long j) {
        long constrainValue = Util.constrainValue((((((long) this.averageBytesPerSecond) * j) / C0542C.MICROS_PER_SECOND) / ((long) this.blockAlignment)) * ((long) this.blockAlignment), 0, this.dataSize - ((long) this.blockAlignment));
        long j2 = this.dataStartPosition + constrainValue;
        long timeUs = getTimeUs(j2);
        SeekPoint seekPoint = new SeekPoint(timeUs, j2);
        if (timeUs < j) {
            if (constrainValue != this.dataSize - ((long) this.blockAlignment)) {
                constrainValue = j2 + ((long) this.blockAlignment);
                return new SeekPoints(seekPoint, new SeekPoint(getTimeUs(constrainValue), constrainValue));
            }
        }
        return new SeekPoints(seekPoint);
    }

    public long getTimeUs(long j) {
        return (Math.max(0, j - this.dataStartPosition) * C0542C.MICROS_PER_SECOND) / ((long) this.averageBytesPerSecond);
    }

    public int getBytesPerFrame() {
        return this.blockAlignment;
    }

    public int getBitrate() {
        return (this.sampleRateHz * this.bitsPerSample) * this.numChannels;
    }

    public int getSampleRateHz() {
        return this.sampleRateHz;
    }

    public int getNumChannels() {
        return this.numChannels;
    }

    public int getEncoding() {
        return this.encoding;
    }
}
