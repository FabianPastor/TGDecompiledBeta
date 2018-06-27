package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor.UnhandledFormatException;
import org.telegram.messenger.exoplayer2.util.Util;

final class TrimmingAudioProcessor implements AudioProcessor {
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private byte[] endBuffer = new byte[0];
    private int endBufferSize;
    private boolean inputEnded;
    private boolean isActive;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int pendingTrimStartBytes;
    private int sampleRateHz = -1;
    private int trimEndFrames;
    private int trimStartFrames;

    public void setTrimFrameCount(int trimStartFrames, int trimEndFrames) {
        this.trimStartFrames = trimStartFrames;
        this.trimEndFrames = trimEndFrames;
    }

    public boolean configure(int sampleRateHz, int channelCount, int encoding) throws UnhandledFormatException {
        if (encoding != 2) {
            throw new UnhandledFormatException(sampleRateHz, channelCount, encoding);
        }
        boolean z;
        this.channelCount = channelCount;
        this.sampleRateHz = sampleRateHz;
        this.endBuffer = new byte[((this.trimEndFrames * channelCount) * 2)];
        this.endBufferSize = 0;
        this.pendingTrimStartBytes = (this.trimStartFrames * channelCount) * 2;
        boolean wasActive = this.isActive;
        if (this.trimStartFrames == 0 && this.trimEndFrames == 0) {
            z = false;
        } else {
            z = true;
        }
        this.isActive = z;
        if (wasActive != this.isActive) {
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputEncoding() {
        return 2;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int remaining = limit - position;
        int trimBytes = Math.min(remaining, this.pendingTrimStartBytes);
        this.pendingTrimStartBytes -= trimBytes;
        inputBuffer.position(position + trimBytes);
        if (this.pendingTrimStartBytes <= 0) {
            remaining -= trimBytes;
            int remainingBytesToOutput = (this.endBufferSize + remaining) - this.endBuffer.length;
            if (this.buffer.capacity() < remainingBytesToOutput) {
                this.buffer = ByteBuffer.allocateDirect(remainingBytesToOutput).order(ByteOrder.nativeOrder());
            } else {
                this.buffer.clear();
            }
            int endBufferBytesToOutput = Util.constrainValue(remainingBytesToOutput, 0, this.endBufferSize);
            this.buffer.put(this.endBuffer, 0, endBufferBytesToOutput);
            int inputBufferBytesToOutput = Util.constrainValue(remainingBytesToOutput - endBufferBytesToOutput, 0, remaining);
            inputBuffer.limit(inputBuffer.position() + inputBufferBytesToOutput);
            this.buffer.put(inputBuffer);
            inputBuffer.limit(limit);
            remaining -= inputBufferBytesToOutput;
            this.endBufferSize -= endBufferBytesToOutput;
            System.arraycopy(this.endBuffer, endBufferBytesToOutput, this.endBuffer, 0, this.endBufferSize);
            inputBuffer.get(this.endBuffer, this.endBufferSize, remaining);
            this.endBufferSize += remaining;
            this.buffer.flip();
            this.outputBuffer = this.buffer;
        }
    }

    public void queueEndOfStream() {
        this.inputEnded = true;
    }

    public ByteBuffer getOutput() {
        ByteBuffer outputBuffer = this.outputBuffer;
        this.outputBuffer = EMPTY_BUFFER;
        return outputBuffer;
    }

    public boolean isEnded() {
        return this.inputEnded && this.outputBuffer == EMPTY_BUFFER;
    }

    public void flush() {
        this.outputBuffer = EMPTY_BUFFER;
        this.inputEnded = false;
        this.pendingTrimStartBytes = 0;
        this.endBufferSize = 0;
    }

    public void reset() {
        flush();
        this.buffer = EMPTY_BUFFER;
        this.channelCount = -1;
        this.sampleRateHz = -1;
        this.endBuffer = new byte[0];
    }
}
