package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor.UnhandledFormatException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class FloatResamplingAudioProcessor implements AudioProcessor {
    private static final int FLOAT_NAN_AS_INT = Float.floatToIntBits(Float.NaN);
    private static final double PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR = 4.656612875245797E-10d;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int sampleRateHz = -1;
    private int sourceEncoding = 0;

    public int getOutputEncoding() {
        return 4;
    }

    public boolean configure(int i, int i2, int i3) throws UnhandledFormatException {
        if (!Util.isEncodingHighResolutionIntegerPcm(i3)) {
            throw new UnhandledFormatException(i, i2, i3);
        } else if (this.sampleRateHz == i && this.channelCount == i2 && this.sourceEncoding == i3) {
            return false;
        } else {
            this.sampleRateHz = i;
            this.channelCount = i2;
            this.sourceEncoding = i3;
            return true;
        }
    }

    public boolean isActive() {
        return Util.isEncodingHighResolutionIntegerPcm(this.sourceEncoding);
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer byteBuffer) {
        Assertions.checkState(isActive());
        Object obj = this.sourceEncoding == NUM ? 1 : null;
        int position = byteBuffer.position();
        int limit = byteBuffer.limit();
        int i = limit - position;
        if (obj == null) {
            i = (i / 3) * 4;
        }
        if (this.buffer.capacity() < i) {
            this.buffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
        } else {
            this.buffer.clear();
        }
        if (obj != null) {
            while (position < limit) {
                writePcm32BitFloat((((byteBuffer.get(position) & 255) | ((byteBuffer.get(position + 1) & 255) << 8)) | ((byteBuffer.get(position + 2) & 255) << 16)) | ((byteBuffer.get(position + 3) & 255) << 24), this.buffer);
                position += 4;
            }
        } else {
            while (position < limit) {
                writePcm32BitFloat((((byteBuffer.get(position) & 255) << 8) | ((byteBuffer.get(position + 1) & 255) << 16)) | ((byteBuffer.get(position + 2) & 255) << 24), this.buffer);
                position += 3;
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
        this.sourceEncoding = 0;
    }

    private static void writePcm32BitFloat(int i, ByteBuffer byteBuffer) {
        i = Float.floatToIntBits((float) (PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR * ((double) i)));
        if (i == FLOAT_NAN_AS_INT) {
            i = Float.floatToIntBits(0);
        }
        byteBuffer.putInt(i);
    }
}
