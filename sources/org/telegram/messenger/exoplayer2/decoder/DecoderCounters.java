package org.telegram.messenger.exoplayer2.decoder;

public final class DecoderCounters {
    public int decoderInitCount;
    public int decoderReleaseCount;
    public int droppedBufferCount;
    public int droppedToKeyframeCount;
    public int inputBufferCount;
    public int maxConsecutiveDroppedBufferCount;
    public int renderedOutputBufferCount;
    public int skippedInputBufferCount;
    public int skippedOutputBufferCount;

    public synchronized void ensureUpdated() {
    }

    public void merge(DecoderCounters other) {
        this.decoderInitCount += other.decoderInitCount;
        this.decoderReleaseCount += other.decoderReleaseCount;
        this.inputBufferCount += other.inputBufferCount;
        this.skippedInputBufferCount += other.skippedInputBufferCount;
        this.renderedOutputBufferCount += other.renderedOutputBufferCount;
        this.skippedOutputBufferCount += other.skippedOutputBufferCount;
        this.droppedBufferCount += other.droppedBufferCount;
        this.maxConsecutiveDroppedBufferCount = Math.max(this.maxConsecutiveDroppedBufferCount, other.maxConsecutiveDroppedBufferCount);
        this.droppedToKeyframeCount += other.droppedToKeyframeCount;
    }
}
