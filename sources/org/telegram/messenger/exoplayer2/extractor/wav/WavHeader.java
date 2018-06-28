package org.telegram.messenger.exoplayer2.extractor.wav;

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

    public WavHeader(int numChannels, int sampleRateHz, int averageBytesPerSecond, int blockAlignment, int bitsPerSample, int encoding) {
        this.numChannels = numChannels;
        this.sampleRateHz = sampleRateHz;
        this.averageBytesPerSecond = averageBytesPerSecond;
        this.blockAlignment = blockAlignment;
        this.bitsPerSample = bitsPerSample;
        this.encoding = encoding;
    }

    public void setDataBounds(long dataStartPosition, long dataSize) {
        this.dataStartPosition = dataStartPosition;
        this.dataSize = dataSize;
    }

    public boolean hasDataBounds() {
        return (this.dataStartPosition == 0 || this.dataSize == 0) ? false : true;
    }

    public boolean isSeekable() {
        return true;
    }

    public long getDurationUs() {
        return (1000000 * (this.dataSize / ((long) this.blockAlignment))) / ((long) this.sampleRateHz);
    }

    public SeekPoints getSeekPoints(long timeUs) {
        long positionOffset = Util.constrainValue((((((long) this.averageBytesPerSecond) * timeUs) / 1000000) / ((long) this.blockAlignment)) * ((long) this.blockAlignment), 0, this.dataSize - ((long) this.blockAlignment));
        long seekPosition = this.dataStartPosition + positionOffset;
        long seekTimeUs = getTimeUs(seekPosition);
        SeekPoint seekPoint = new SeekPoint(seekTimeUs, seekPosition);
        if (seekTimeUs >= timeUs || positionOffset == this.dataSize - ((long) this.blockAlignment)) {
            return new SeekPoints(seekPoint);
        }
        long secondSeekPosition = seekPosition + ((long) this.blockAlignment);
        return new SeekPoints(seekPoint, new SeekPoint(getTimeUs(secondSeekPosition), secondSeekPosition));
    }

    public long getTimeUs(long position) {
        return (1000000 * Math.max(0, position - this.dataStartPosition)) / ((long) this.averageBytesPerSecond);
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
