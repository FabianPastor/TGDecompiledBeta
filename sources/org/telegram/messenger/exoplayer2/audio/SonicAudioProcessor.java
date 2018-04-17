package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor.UnhandledFormatException;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SonicAudioProcessor implements AudioProcessor {
    private static final float CLOSE_THRESHOLD = 0.01f;
    public static final float MAXIMUM_PITCH = 8.0f;
    public static final float MAXIMUM_SPEED = 8.0f;
    public static final float MINIMUM_PITCH = 0.1f;
    public static final float MINIMUM_SPEED = 0.1f;
    private static final int MIN_BYTES_FOR_SPEEDUP_CALCULATION = 1024;
    public static final int SAMPLE_RATE_NO_CHANGE = -1;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private long inputBytes;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private long outputBytes;
    private int outputSampleRateHz = -1;
    private int pendingOutputSampleRateHz = -1;
    private float pitch = 1.0f;
    private int sampleRateHz = -1;
    private ShortBuffer shortBuffer = this.buffer.asShortBuffer();
    private Sonic sonic;
    private float speed = 1.0f;

    public float setSpeed(float speed) {
        this.speed = Util.constrainValue(speed, 0.1f, 8.0f);
        return this.speed;
    }

    public float setPitch(float pitch) {
        this.pitch = Util.constrainValue(pitch, 0.1f, 8.0f);
        return pitch;
    }

    public void setOutputSampleRateHz(int sampleRateHz) {
        this.pendingOutputSampleRateHz = sampleRateHz;
    }

    public long scaleDurationForSpeedup(long duration) {
        if (this.outputBytes < 1024) {
            return (long) (((double) this.speed) * ((double) duration));
        }
        long scaleLargeTimestamp;
        if (this.outputSampleRateHz == this.sampleRateHz) {
            scaleLargeTimestamp = Util.scaleLargeTimestamp(duration, this.inputBytes, this.outputBytes);
        } else {
            scaleLargeTimestamp = Util.scaleLargeTimestamp(duration, this.inputBytes * ((long) this.outputSampleRateHz), this.outputBytes * ((long) this.sampleRateHz));
        }
        return scaleLargeTimestamp;
    }

    public boolean configure(int sampleRateHz, int channelCount, int encoding) throws UnhandledFormatException {
        if (encoding != 2) {
            throw new UnhandledFormatException(sampleRateHz, channelCount, encoding);
        }
        int outputSampleRateHz = this.pendingOutputSampleRateHz == -1 ? sampleRateHz : this.pendingOutputSampleRateHz;
        if (this.sampleRateHz == sampleRateHz && this.channelCount == channelCount && this.outputSampleRateHz == outputSampleRateHz) {
            return false;
        }
        this.sampleRateHz = sampleRateHz;
        this.channelCount = channelCount;
        this.outputSampleRateHz = outputSampleRateHz;
        return true;
    }

    public boolean isActive() {
        if (Math.abs(this.speed - 1.0f) < CLOSE_THRESHOLD && Math.abs(this.pitch - 1.0f) < CLOSE_THRESHOLD) {
            if (this.outputSampleRateHz == this.sampleRateHz) {
                return false;
            }
        }
        return true;
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputEncoding() {
        return 2;
    }

    public int getOutputSampleRateHz() {
        return this.outputSampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        if (inputBuffer.hasRemaining()) {
            ShortBuffer shortBuffer = inputBuffer.asShortBuffer();
            int inputSize = inputBuffer.remaining();
            this.inputBytes += (long) inputSize;
            this.sonic.queueInput(shortBuffer);
            inputBuffer.position(inputBuffer.position() + inputSize);
        }
        int outputSize = (this.sonic.getSamplesAvailable() * this.channelCount) * 2;
        if (outputSize > 0) {
            if (this.buffer.capacity() < outputSize) {
                this.buffer = ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder());
                this.shortBuffer = this.buffer.asShortBuffer();
            } else {
                this.buffer.clear();
                this.shortBuffer.clear();
            }
            this.sonic.getOutput(this.shortBuffer);
            this.outputBytes += (long) outputSize;
            this.buffer.limit(outputSize);
            this.outputBuffer = this.buffer;
        }
    }

    public void queueEndOfStream() {
        this.sonic.queueEndOfStream();
        this.inputEnded = true;
    }

    public ByteBuffer getOutput() {
        ByteBuffer outputBuffer = this.outputBuffer;
        this.outputBuffer = EMPTY_BUFFER;
        return outputBuffer;
    }

    public boolean isEnded() {
        return this.inputEnded && (this.sonic == null || this.sonic.getSamplesAvailable() == 0);
    }

    public void flush() {
        this.sonic = new Sonic(this.sampleRateHz, this.channelCount, this.speed, this.pitch, this.outputSampleRateHz);
        this.outputBuffer = EMPTY_BUFFER;
        this.inputBytes = 0;
        this.outputBytes = 0;
        this.inputEnded = false;
    }

    public void reset() {
        this.sonic = null;
        this.buffer = EMPTY_BUFFER;
        this.shortBuffer = this.buffer.asShortBuffer();
        this.outputBuffer = EMPTY_BUFFER;
        this.channelCount = -1;
        this.sampleRateHz = -1;
        this.outputSampleRateHz = -1;
        this.inputBytes = 0;
        this.outputBytes = 0;
        this.inputEnded = false;
        this.pendingOutputSampleRateHz = -1;
    }
}
