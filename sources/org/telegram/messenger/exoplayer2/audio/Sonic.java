package org.telegram.messenger.exoplayer2.audio;

import java.nio.ShortBuffer;
import java.util.Arrays;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class Sonic {
    private static final int AMDF_FREQUENCY = 4000;
    private static final int MAXIMUM_PITCH = 400;
    private static final int MINIMUM_PITCH = 65;
    private final int channelCount;
    private final short[] downSampleBuffer = new short[this.maxRequiredFrameCount];
    private short[] inputBuffer;
    private int inputFrameCount;
    private final int inputSampleRateHz;
    private int maxDiff;
    private final int maxPeriod;
    private final int maxRequiredFrameCount = (this.maxPeriod * 2);
    private int minDiff;
    private final int minPeriod;
    private int newRatePosition;
    private int oldRatePosition;
    private short[] outputBuffer;
    private int outputFrameCount;
    private final float pitch;
    private short[] pitchBuffer;
    private int pitchFrameCount;
    private int prevMinDiff;
    private int prevPeriod;
    private final float rate;
    private int remainingInputToCopyFrameCount;
    private final float speed;

    public Sonic(int inputSampleRateHz, int channelCount, float speed, float pitch, int outputSampleRateHz) {
        this.inputSampleRateHz = inputSampleRateHz;
        this.channelCount = channelCount;
        this.speed = speed;
        this.pitch = pitch;
        this.rate = ((float) inputSampleRateHz) / ((float) outputSampleRateHz);
        this.minPeriod = inputSampleRateHz / MAXIMUM_PITCH;
        this.maxPeriod = inputSampleRateHz / 65;
        this.inputBuffer = new short[(this.maxRequiredFrameCount * channelCount)];
        this.outputBuffer = new short[(this.maxRequiredFrameCount * channelCount)];
        this.pitchBuffer = new short[(this.maxRequiredFrameCount * channelCount)];
    }

    public void queueInput(ShortBuffer buffer) {
        int framesToWrite = buffer.remaining() / this.channelCount;
        int bytesToWrite = (this.channelCount * framesToWrite) * 2;
        this.inputBuffer = ensureSpaceForAdditionalFrames(this.inputBuffer, this.inputFrameCount, framesToWrite);
        buffer.get(this.inputBuffer, this.inputFrameCount * this.channelCount, bytesToWrite / 2);
        this.inputFrameCount += framesToWrite;
        processStreamInput();
    }

    public void getOutput(ShortBuffer buffer) {
        int framesToRead = Math.min(buffer.remaining() / this.channelCount, this.outputFrameCount);
        buffer.put(this.outputBuffer, 0, this.channelCount * framesToRead);
        this.outputFrameCount -= framesToRead;
        System.arraycopy(this.outputBuffer, this.channelCount * framesToRead, this.outputBuffer, 0, this.outputFrameCount * this.channelCount);
    }

    public void queueEndOfStream() {
        int remainingFrameCount = this.inputFrameCount;
        float r = this.rate * this.pitch;
        int expectedOutputFrames = this.outputFrameCount + ((int) ((((((float) remainingFrameCount) / (this.speed / this.pitch)) + ((float) this.pitchFrameCount)) / r) + 0.5f));
        this.inputBuffer = ensureSpaceForAdditionalFrames(this.inputBuffer, this.inputFrameCount, (this.maxRequiredFrameCount * 2) + remainingFrameCount);
        for (int xSample = 0; xSample < (this.maxRequiredFrameCount * 2) * this.channelCount; xSample++) {
            this.inputBuffer[(this.channelCount * remainingFrameCount) + xSample] = (short) 0;
        }
        this.inputFrameCount += this.maxRequiredFrameCount * 2;
        processStreamInput();
        if (this.outputFrameCount > expectedOutputFrames) {
            this.outputFrameCount = expectedOutputFrames;
        }
        this.inputFrameCount = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.pitchFrameCount = 0;
    }

    public void flush() {
        this.inputFrameCount = 0;
        this.outputFrameCount = 0;
        this.pitchFrameCount = 0;
        this.oldRatePosition = 0;
        this.newRatePosition = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.prevPeriod = 0;
        this.prevMinDiff = 0;
        this.minDiff = 0;
        this.maxDiff = 0;
    }

    public int getFramesAvailable() {
        return this.outputFrameCount;
    }

    private short[] ensureSpaceForAdditionalFrames(short[] buffer, int frameCount, int additionalFrameCount) {
        int currentCapacityFrames = buffer.length / this.channelCount;
        if (frameCount + additionalFrameCount <= currentCapacityFrames) {
            return buffer;
        }
        return Arrays.copyOf(buffer, this.channelCount * (((currentCapacityFrames * 3) / 2) + additionalFrameCount));
    }

    private void removeProcessedInputFrames(int positionFrames) {
        int remainingFrames = this.inputFrameCount - positionFrames;
        System.arraycopy(this.inputBuffer, this.channelCount * positionFrames, this.inputBuffer, 0, this.channelCount * remainingFrames);
        this.inputFrameCount = remainingFrames;
    }

    private void copyToOutput(short[] samples, int positionFrames, int frameCount) {
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, frameCount);
        System.arraycopy(samples, this.channelCount * positionFrames, this.outputBuffer, this.outputFrameCount * this.channelCount, this.channelCount * frameCount);
        this.outputFrameCount += frameCount;
    }

    private int copyInputToOutput(int positionFrames) {
        int frameCount = Math.min(this.maxRequiredFrameCount, this.remainingInputToCopyFrameCount);
        copyToOutput(this.inputBuffer, positionFrames, frameCount);
        this.remainingInputToCopyFrameCount -= frameCount;
        return frameCount;
    }

    private void downSampleInput(short[] samples, int position, int skip) {
        int frameCount = this.maxRequiredFrameCount / skip;
        int samplesPerValue = this.channelCount * skip;
        position *= this.channelCount;
        for (int i = 0; i < frameCount; i++) {
            int value = 0;
            for (int j = 0; j < samplesPerValue; j++) {
                value += samples[((i * samplesPerValue) + position) + j];
            }
            this.downSampleBuffer[i] = (short) (value / samplesPerValue);
        }
    }

    private int findPitchPeriodInRange(short[] samples, int position, int minPeriod, int maxPeriod) {
        int bestPeriod = 0;
        int worstPeriod = 255;
        int minDiff = 1;
        int maxDiff = 0;
        position *= this.channelCount;
        for (int period = minPeriod; period <= maxPeriod; period++) {
            int diff = 0;
            for (int i = 0; i < period; i++) {
                diff += Math.abs(samples[position + i] - samples[(position + period) + i]);
            }
            if (diff * bestPeriod < minDiff * period) {
                minDiff = diff;
                bestPeriod = period;
            }
            if (diff * worstPeriod > maxDiff * period) {
                maxDiff = diff;
                worstPeriod = period;
            }
        }
        this.minDiff = minDiff / bestPeriod;
        this.maxDiff = maxDiff / worstPeriod;
        return bestPeriod;
    }

    private boolean previousPeriodBetter(int minDiff, int maxDiff) {
        if (minDiff == 0 || this.prevPeriod == 0 || maxDiff > minDiff * 3 || minDiff * 2 <= this.prevMinDiff * 3) {
            return false;
        }
        return true;
    }

    private int findPitchPeriod(short[] samples, int position) {
        int skip;
        int period;
        int retPeriod;
        if (this.inputSampleRateHz > AMDF_FREQUENCY) {
            skip = this.inputSampleRateHz / AMDF_FREQUENCY;
        } else {
            skip = 1;
        }
        if (this.channelCount == 1 && skip == 1) {
            period = findPitchPeriodInRange(samples, position, this.minPeriod, this.maxPeriod);
        } else {
            downSampleInput(samples, position, skip);
            period = findPitchPeriodInRange(this.downSampleBuffer, 0, this.minPeriod / skip, this.maxPeriod / skip);
            if (skip != 1) {
                period *= skip;
                int minP = period - (skip * 4);
                int maxP = period + (skip * 4);
                if (minP < this.minPeriod) {
                    minP = this.minPeriod;
                }
                if (maxP > this.maxPeriod) {
                    maxP = this.maxPeriod;
                }
                if (this.channelCount == 1) {
                    period = findPitchPeriodInRange(samples, position, minP, maxP);
                } else {
                    downSampleInput(samples, position, 1);
                    period = findPitchPeriodInRange(this.downSampleBuffer, 0, minP, maxP);
                }
            }
        }
        if (previousPeriodBetter(this.minDiff, this.maxDiff)) {
            retPeriod = this.prevPeriod;
        } else {
            retPeriod = period;
        }
        this.prevMinDiff = this.minDiff;
        this.prevPeriod = period;
        return retPeriod;
    }

    private void moveNewSamplesToPitchBuffer(int originalOutputFrameCount) {
        int frameCount = this.outputFrameCount - originalOutputFrameCount;
        this.pitchBuffer = ensureSpaceForAdditionalFrames(this.pitchBuffer, this.pitchFrameCount, frameCount);
        System.arraycopy(this.outputBuffer, this.channelCount * originalOutputFrameCount, this.pitchBuffer, this.pitchFrameCount * this.channelCount, this.channelCount * frameCount);
        this.outputFrameCount = originalOutputFrameCount;
        this.pitchFrameCount += frameCount;
    }

    private void removePitchFrames(int frameCount) {
        if (frameCount != 0) {
            System.arraycopy(this.pitchBuffer, this.channelCount * frameCount, this.pitchBuffer, 0, (this.pitchFrameCount - frameCount) * this.channelCount);
            this.pitchFrameCount -= frameCount;
        }
    }

    private short interpolate(short[] in, int inPos, int oldSampleRate, int newSampleRate) {
        int rightPosition = (this.oldRatePosition + 1) * newSampleRate;
        int ratio = rightPosition - (this.newRatePosition * oldSampleRate);
        int width = rightPosition - (this.oldRatePosition * newSampleRate);
        return (short) (((ratio * in[inPos]) + ((width - ratio) * in[this.channelCount + inPos])) / width);
    }

    private void adjustRate(float rate, int originalOutputFrameCount) {
        if (this.outputFrameCount != originalOutputFrameCount) {
            int newSampleRate = (int) (((float) this.inputSampleRateHz) / rate);
            int oldSampleRate = this.inputSampleRateHz;
            while (true) {
                if (newSampleRate <= MessagesController.UPDATE_MASK_CHAT_ADMINS && oldSampleRate <= MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                    break;
                }
                newSampleRate /= 2;
                oldSampleRate /= 2;
            }
            moveNewSamplesToPitchBuffer(originalOutputFrameCount);
            for (int position = 0; position < this.pitchFrameCount - 1; position++) {
                while ((this.oldRatePosition + 1) * newSampleRate > this.newRatePosition * oldSampleRate) {
                    this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, 1);
                    for (int i = 0; i < this.channelCount; i++) {
                        this.outputBuffer[(this.outputFrameCount * this.channelCount) + i] = interpolate(this.pitchBuffer, (this.channelCount * position) + i, oldSampleRate, newSampleRate);
                    }
                    this.newRatePosition++;
                    this.outputFrameCount++;
                }
                this.oldRatePosition++;
                if (this.oldRatePosition == oldSampleRate) {
                    this.oldRatePosition = 0;
                    Assertions.checkState(this.newRatePosition == newSampleRate);
                    this.newRatePosition = 0;
                }
            }
            removePitchFrames(this.pitchFrameCount - 1);
        }
    }

    private int skipPitchPeriod(short[] samples, int position, float speed, int period) {
        int newFrameCount;
        if (speed >= 2.0f) {
            newFrameCount = (int) (((float) period) / (speed - 1.0f));
        } else {
            newFrameCount = period;
            this.remainingInputToCopyFrameCount = (int) ((((float) period) * (2.0f - speed)) / (speed - 1.0f));
        }
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, newFrameCount);
        overlapAdd(newFrameCount, this.channelCount, this.outputBuffer, this.outputFrameCount, samples, position, samples, position + period);
        this.outputFrameCount += newFrameCount;
        return newFrameCount;
    }

    private int insertPitchPeriod(short[] samples, int position, float speed, int period) {
        int newFrameCount;
        if (speed < 0.5f) {
            newFrameCount = (int) ((((float) period) * speed) / (1.0f - speed));
        } else {
            newFrameCount = period;
            this.remainingInputToCopyFrameCount = (int) ((((float) period) * ((2.0f * speed) - 1.0f)) / (1.0f - speed));
        }
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, period + newFrameCount);
        System.arraycopy(samples, this.channelCount * position, this.outputBuffer, this.outputFrameCount * this.channelCount, this.channelCount * period);
        overlapAdd(newFrameCount, this.channelCount, this.outputBuffer, this.outputFrameCount + period, samples, position + period, samples, position);
        this.outputFrameCount += period + newFrameCount;
        return newFrameCount;
    }

    private void changeSpeed(float speed) {
        if (this.inputFrameCount >= this.maxRequiredFrameCount) {
            int frameCount = this.inputFrameCount;
            int positionFrames = 0;
            do {
                if (this.remainingInputToCopyFrameCount > 0) {
                    positionFrames += copyInputToOutput(positionFrames);
                } else {
                    int period = findPitchPeriod(this.inputBuffer, positionFrames);
                    if (((double) speed) > 1.0d) {
                        positionFrames += skipPitchPeriod(this.inputBuffer, positionFrames, speed, period) + period;
                    } else {
                        positionFrames += insertPitchPeriod(this.inputBuffer, positionFrames, speed, period);
                    }
                }
            } while (this.maxRequiredFrameCount + positionFrames <= frameCount);
            removeProcessedInputFrames(positionFrames);
        }
    }

    private void processStreamInput() {
        int originalOutputFrameCount = this.outputFrameCount;
        float s = this.speed / this.pitch;
        float r = this.rate * this.pitch;
        if (((double) s) > 1.00001d || ((double) s) < 0.99999d) {
            changeSpeed(s);
        } else {
            copyToOutput(this.inputBuffer, 0, this.inputFrameCount);
            this.inputFrameCount = 0;
        }
        if (r != 1.0f) {
            adjustRate(r, originalOutputFrameCount);
        }
    }

    private static void overlapAdd(int frameCount, int channelCount, short[] out, int outPosition, short[] rampDown, int rampDownPosition, short[] rampUp, int rampUpPosition) {
        for (int i = 0; i < channelCount; i++) {
            int o = (outPosition * channelCount) + i;
            int u = (rampUpPosition * channelCount) + i;
            int d = (rampDownPosition * channelCount) + i;
            for (int t = 0; t < frameCount; t++) {
                out[o] = (short) (((rampDown[d] * (frameCount - t)) + (rampUp[u] * t)) / frameCount);
                o += channelCount;
                d += channelCount;
                u += channelCount;
            }
        }
    }
}
