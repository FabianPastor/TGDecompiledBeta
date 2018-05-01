package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor.UnhandledFormatException;

final class ResamplingAudioProcessor implements AudioProcessor {
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private int encoding = 0;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int sampleRateHz = -1;

    public int getOutputEncoding() {
        return 2;
    }

    public boolean configure(int i, int i2, int i3) throws UnhandledFormatException {
        if (i3 != 3 && i3 != 2 && i3 != Integer.MIN_VALUE && i3 != NUM) {
            throw new UnhandledFormatException(i, i2, i3);
        } else if (this.sampleRateHz == i && this.channelCount == i2 && this.encoding == i3) {
            return false;
        } else {
            this.sampleRateHz = i;
            this.channelCount = i2;
            this.encoding = i3;
            if (i3 == 2) {
                this.buffer = EMPTY_BUFFER;
            }
            return true;
        }
    }

    public boolean isActive() {
        return (this.encoding == 0 || this.encoding == 2) ? false : true;
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
        int i2 = this.encoding;
        if (i2 == Integer.MIN_VALUE) {
            i = (i / 3) * 2;
        } else if (i2 == 3) {
            i *= 2;
        } else if (i2 != NUM) {
            throw new IllegalStateException();
        } else {
            i /= 2;
        }
        if (this.buffer.capacity() < i) {
            this.buffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
        } else {
            this.buffer.clear();
        }
        i = this.encoding;
        if (i == Integer.MIN_VALUE) {
            while (position < limit) {
                this.buffer.put(byteBuffer.get(position + 1));
                this.buffer.put(byteBuffer.get(position + 2));
                position += 3;
            }
        } else if (i == 3) {
            while (position < limit) {
                this.buffer.put((byte) 0);
                this.buffer.put((byte) ((byteBuffer.get(position) & 255) - 128));
                position++;
            }
        } else if (i != NUM) {
            throw new IllegalStateException();
        } else {
            while (position < limit) {
                this.buffer.put(byteBuffer.get(position + 2));
                this.buffer.put(byteBuffer.get(position + 3));
                position += 4;
            }
        }
        byteBuffer.position(byteBuffer.limit());
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
        this.sampleRateHz = -1;
        this.channelCount = -1;
        this.encoding = 0;
    }
}
