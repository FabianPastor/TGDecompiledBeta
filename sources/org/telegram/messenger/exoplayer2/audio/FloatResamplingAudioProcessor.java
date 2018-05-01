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

    public boolean configure(int sampleRateHz, int channelCount, int encoding) throws UnhandledFormatException {
        if (!Util.isEncodingHighResolutionIntegerPcm(encoding)) {
            throw new UnhandledFormatException(sampleRateHz, channelCount, encoding);
        } else if (this.sampleRateHz == sampleRateHz && this.channelCount == channelCount && this.sourceEncoding == encoding) {
            return false;
        } else {
            this.sampleRateHz = sampleRateHz;
            this.channelCount = channelCount;
            this.sourceEncoding = encoding;
            return true;
        }
    }

    public boolean isActive() {
        return Util.isEncodingHighResolutionIntegerPcm(this.sourceEncoding);
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputEncoding() {
        return 4;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        Assertions.checkState(isActive());
        boolean isInput32Bit = this.sourceEncoding == NUM;
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int size = limit - position;
        int resampledSize = isInput32Bit ? size : (size / 3) * 4;
        if (this.buffer.capacity() < resampledSize) {
            this.buffer = ByteBuffer.allocateDirect(resampledSize).order(ByteOrder.nativeOrder());
        } else {
            this.buffer.clear();
        }
        int i;
        if (isInput32Bit) {
            for (i = position; i < limit; i += 4) {
                writePcm32BitFloat((((inputBuffer.get(i) & 255) | ((inputBuffer.get(i + 1) & 255) << 8)) | ((inputBuffer.get(i + 2) & 255) << 16)) | ((inputBuffer.get(i + 3) & 255) << 24), this.buffer);
            }
        } else {
            for (i = position; i < limit; i += 3) {
                writePcm32BitFloat((((inputBuffer.get(i) & 255) << 8) | ((inputBuffer.get(i + 1) & 255) << 16)) | ((inputBuffer.get(i + 2) & 255) << 24), this.buffer);
            }
        }
        inputBuffer.position(inputBuffer.limit());
        this.buffer.flip();
        this.outputBuffer = this.buffer;
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
    }

    public void reset() {
        flush();
        this.buffer = EMPTY_BUFFER;
        this.sampleRateHz = -1;
        this.channelCount = -1;
        this.sourceEncoding = 0;
    }

    private static void writePcm32BitFloat(int pcm32BitInt, ByteBuffer buffer) {
        int floatBits = Float.floatToIntBits((float) (PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR * ((double) pcm32BitInt)));
        if (floatBits == FLOAT_NAN_AS_INT) {
            floatBits = Float.floatToIntBits(0.0f);
        }
        buffer.putInt(floatBits);
    }
}
