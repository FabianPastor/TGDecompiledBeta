package org.telegram.messenger.exoplayer2.util;

import org.telegram.messenger.exoplayer2.C0600C;

public final class FlacStreamInfo {
    public final int bitsPerSample;
    public final int channels;
    public final int maxBlockSize;
    public final int maxFrameSize;
    public final int minBlockSize;
    public final int minFrameSize;
    public final int sampleRate;
    public final long totalSamples;

    public FlacStreamInfo(byte[] data, int offset) {
        ParsableBitArray scratch = new ParsableBitArray(data);
        scratch.setPosition(offset * 8);
        this.minBlockSize = scratch.readBits(16);
        this.maxBlockSize = scratch.readBits(16);
        this.minFrameSize = scratch.readBits(24);
        this.maxFrameSize = scratch.readBits(24);
        this.sampleRate = scratch.readBits(20);
        this.channels = scratch.readBits(3) + 1;
        this.bitsPerSample = scratch.readBits(5) + 1;
        this.totalSamples = ((((long) scratch.readBits(4)) & 15) << 32) | (((long) scratch.readBits(32)) & 4294967295L);
    }

    public FlacStreamInfo(int minBlockSize, int maxBlockSize, int minFrameSize, int maxFrameSize, int sampleRate, int channels, int bitsPerSample, long totalSamples) {
        this.minBlockSize = minBlockSize;
        this.maxBlockSize = maxBlockSize;
        this.minFrameSize = minFrameSize;
        this.maxFrameSize = maxFrameSize;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bitsPerSample = bitsPerSample;
        this.totalSamples = totalSamples;
    }

    public int maxDecodedFrameSize() {
        return (this.maxBlockSize * this.channels) * (this.bitsPerSample / 8);
    }

    public int bitRate() {
        return this.bitsPerSample * this.sampleRate;
    }

    public long durationUs() {
        return (this.totalSamples * C0600C.MICROS_PER_SECOND) / ((long) this.sampleRate);
    }
}
