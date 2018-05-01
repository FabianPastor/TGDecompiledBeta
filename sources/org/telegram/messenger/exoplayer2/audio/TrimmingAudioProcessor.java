package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor.UnhandledFormatException;
import org.telegram.messenger.exoplayer2.util.Util;

final class TrimmingAudioProcessor implements AudioProcessor {
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private byte[] endBuffer;
    private int endBufferSize;
    private boolean inputEnded;
    private boolean isActive;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int pendingTrimStartBytes;
    private int sampleRateHz;
    private int trimEndSamples;
    private int trimStartSamples;

    public int getOutputEncoding() {
        return 2;
    }

    public void setTrimSampleCount(int i, int i2) {
        this.trimStartSamples = i;
        this.trimEndSamples = i2;
    }

    public boolean configure(int i, int i2, int i3) throws UnhandledFormatException {
        if (i3 != 2) {
            throw new UnhandledFormatException(i, i2, i3);
        }
        this.channelCount = i2;
        this.sampleRateHz = i;
        this.endBuffer = new byte[((this.trimEndSamples * i2) * 2)];
        this.endBufferSize = 0;
        this.pendingTrimStartBytes = (this.trimStartSamples * i2) * 2;
        i2 = this.isActive;
        if (this.trimStartSamples == 0) {
            if (this.trimEndSamples == 0) {
                i3 = 0;
                this.isActive = i3;
                if (i2 == this.isActive) {
                    return 1;
                }
                return false;
            }
        }
        i3 = 1;
        this.isActive = i3;
        if (i2 == this.isActive) {
            return false;
        }
        return 1;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer byteBuffer) {
        int position = byteBuffer.position();
        int limit = byteBuffer.limit();
        int i = limit - position;
        int min = Math.min(i, this.pendingTrimStartBytes);
        this.pendingTrimStartBytes -= min;
        byteBuffer.position(position + min);
        if (this.pendingTrimStartBytes <= 0) {
            i -= min;
            position = (this.endBufferSize + i) - this.endBuffer.length;
            if (this.buffer.capacity() < position) {
                this.buffer = ByteBuffer.allocateDirect(position).order(ByteOrder.nativeOrder());
            } else {
                this.buffer.clear();
            }
            min = Util.constrainValue(position, 0, this.endBufferSize);
            this.buffer.put(this.endBuffer, 0, min);
            position = Util.constrainValue(position - min, 0, i);
            byteBuffer.limit(byteBuffer.position() + position);
            this.buffer.put(byteBuffer);
            byteBuffer.limit(limit);
            i -= position;
            this.endBufferSize -= min;
            System.arraycopy(this.endBuffer, min, this.endBuffer, 0, this.endBufferSize);
            byteBuffer.get(this.endBuffer, this.endBufferSize, i);
            this.endBufferSize += i;
            this.buffer.flip();
            this.outputBuffer = this.buffer;
        }
    }

    public void queueEndOfStream() {
        this.inputEnded = true;
    }

    public ByteBuffer getOutput() {
        ByteBuffer byteBuffer = this.outputBuffer;
        this.outputBuffer = EMPTY_BUFFER;
        return byteBuffer;
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
        this.endBuffer = null;
    }
}
