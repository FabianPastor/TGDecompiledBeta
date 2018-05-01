package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor.UnhandledFormatException;

final class ChannelMappingAudioProcessor implements AudioProcessor {
    private boolean active;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int[] outputChannels;
    private int[] pendingOutputChannels;
    private int sampleRateHz = -1;

    public int getOutputEncoding() {
        return 2;
    }

    public void setChannelMap(int[] iArr) {
        this.pendingOutputChannels = iArr;
    }

    public boolean configure(int i, int i2, int i3) throws UnhandledFormatException {
        boolean equals = Arrays.equals(this.pendingOutputChannels, this.outputChannels) ^ true;
        this.outputChannels = this.pendingOutputChannels;
        if (this.outputChannels == null) {
            this.active = false;
            return equals;
        } else if (i3 != 2) {
            throw new UnhandledFormatException(i, i2, i3);
        } else if (!equals && this.sampleRateHz == i && this.channelCount == i2) {
            return false;
        } else {
            this.sampleRateHz = i;
            this.channelCount = i2;
            this.active = i2 != this.outputChannels.length;
            int i4 = 0;
            while (i4 < this.outputChannels.length) {
                int i5 = this.outputChannels[i4];
                if (i5 >= i2) {
                    throw new UnhandledFormatException(i, i2, i3);
                }
                this.active = (i5 != i4 ? 1 : 0) | this.active;
                i4++;
            }
            return true;
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public int getOutputChannelCount() {
        return this.outputChannels == null ? this.channelCount : this.outputChannels.length;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer byteBuffer) {
        int position = byteBuffer.position();
        int limit = byteBuffer.limit();
        int length = (((limit - position) / (this.channelCount * 2)) * this.outputChannels.length) * 2;
        if (this.buffer.capacity() < length) {
            this.buffer = ByteBuffer.allocateDirect(length).order(ByteOrder.nativeOrder());
        } else {
            this.buffer.clear();
        }
        while (position < limit) {
            for (int i : this.outputChannels) {
                this.buffer.putShort(byteBuffer.getShort((i * 2) + position));
            }
            position += this.channelCount * 2;
        }
        byteBuffer.position(limit);
        this.buffer.flip();
        this.outputBuffer = this.buffer;
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
    }

    public void reset() {
        flush();
        this.buffer = EMPTY_BUFFER;
        this.channelCount = -1;
        this.sampleRateHz = -1;
        this.outputChannels = null;
        this.active = false;
    }
}
