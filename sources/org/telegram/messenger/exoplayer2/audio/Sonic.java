package org.telegram.messenger.exoplayer2.audio;

import java.nio.ShortBuffer;
import java.util.Arrays;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class Sonic {
    private static final int AMDF_FREQUENCY = 4000;
    private static final int MAXIMUM_PITCH = 400;
    private static final int MINIMUM_PITCH = 65;
    private static final int SINC_FILTER_POINTS = 12;
    private static final int SINC_TABLE_SIZE = 601;
    private static final short[] sincTable = new short[]{(short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) -1, (short) -1, (short) -2, (short) -2, (short) -3, (short) -4, (short) -6, (short) -7, (short) -9, (short) -10, (short) -12, (short) -14, (short) -17, (short) -19, (short) -21, (short) -24, (short) -26, (short) -29, (short) -32, (short) -34, (short) -37, (short) -40, (short) -42, (short) -44, (short) -47, (short) -48, (short) -50, (short) -51, (short) -52, (short) -53, (short) -53, (short) -53, (short) -52, (short) -50, (short) -48, (short) -46, (short) -43, (short) -39, (short) -34, (short) -29, (short) -22, (short) -16, (short) -8, (short) 0, (short) 9, (short) 19, (short) 29, (short) 41, (short) 53, (short) 65, (short) 79, (short) 92, (short) 107, (short) 121, (short) 137, (short) 152, (short) 168, (short) 184, (short) 200, (short) 215, (short) 231, (short) 247, (short) 262, (short) 276, (short) 291, (short) 304, (short) 317, (short) 328, (short) 339, (short) 348, (short) 357, (short) 363, (short) 369, (short) 372, (short) 374, (short) 375, (short) 373, (short) 369, (short) 363, (short) 355, (short) 345, (short) 332, (short) 318, (short) 300, (short) 281, (short) 259, (short) 234, (short) 208, (short) 178, (short) 147, (short) 113, (short) 77, (short) 39, (short) 0, (short) -41, (short) -85, (short) -130, (short) -177, (short) -225, (short) -274, (short) -324, (short) -375, (short) -426, (short) -478, (short) -530, (short) -581, (short) -632, (short) -682, (short) -731, (short) -779, (short) -825, (short) -870, (short) -912, (short) -951, (short) -989, (short) -1023, (short) -1053, (short) -1080, (short) -1104, (short) -1123, (short) -1138, (short) -1149, (short) -1154, (short) -1155, (short) -1151, (short) -1141, (short) -1125, (short) -1105, (short) -1078, (short) -1046, (short) -1007, (short) -963, (short) -913, (short) -857, (short) -796, (short) -728, (short) -655, (short) -576, (short) -492, (short) -403, (short) -309, (short) -210, (short) -107, (short) 0, (short) 111, (short) 225, (short) 342, (short) 462, (short) 584, (short) 708, (short) 833, (short) 958, (short) 1084, (short) 1209, (short) 1333, (short) 1455, (short) 1575, (short) 1693, (short) 1807, (short) 1916, (short) 2022, (short) 2122, (short) 2216, (short) 2304, (short) 2384, (short) 2457, (short) 2522, (short) 2579, (short) 2625, (short) 2663, (short) 2689, (short) 2706, (short) 2711, (short) 2705, (short) 2687, (short) 2657, (short) 2614, (short) 2559, (short) 2491, (short) 2411, (short) 2317, (short) 2211, (short) 2092, (short) 1960, (short) 1815, (short) 1658, (short) 1489, (short) 1308, (short) 1115, (short) 912, (short) 698, (short) 474, (short) 241, (short) 0, (short) -249, (short) -506, (short) -769, (short) -1037, (short) -1310, (short) -1586, (short) -1864, (short) -2144, (short) -2424, (short) -2703, (short) -2980, (short) -3254, (short) -3523, (short) -3787, (short) -4043, (short) -4291, (short) -4529, (short) -4757, (short) -4972, (short) -5174, (short) -5360, (short) -5531, (short) -5685, (short) -5819, (short) -5935, (short) -6029, (short) -6101, (short) -6150, (short) -6175, (short) -6175, (short) -6149, (short) -6096, (short) -6015, (short) -5905, (short) -5767, (short) -5599, (short) -5401, (short) -5172, (short) -4912, (short) -4621, (short) -4298, (short) -3944, (short) -3558, (short) -3141, (short) -2693, (short) -2214, (short) -1705, (short) -1166, (short) -597, (short) 0, (short) 625, (short) 1277, (short) 1955, (short) 2658, (short) 3386, (short) 4135, (short) 4906, (short) 5697, (short) 6506, (short) 7332, (short) 8173, (short) 9027, (short) 9893, (short) 10769, (short) 11654, (short) 12544, (short) 13439, (short) 14335, (short) 15232, (short) 16128, (short) 17019, (short) 17904, (short) 18782, (short) 19649, (short) 20504, (short) 21345, (short) 22170, (short) 22977, (short) 23763, (short) 24527, (short) 25268, (short) 25982, (short) 26669, (short) 27327, (short) 27953, (short) 28547, (short) 29107, (short) 29632, (short) 30119, (short) 30569, (short) 30979, (short) 31349, (short) 31678, (short) 31964, (short) 32208, (short) 32408, (short) 32565, (short) 32677, (short) 32744, Short.MAX_VALUE, (short) 32744, (short) 32677, (short) 32565, (short) 32408, (short) 32208, (short) 31964, (short) 31678, (short) 31349, (short) 30979, (short) 30569, (short) 30119, (short) 29632, (short) 29107, (short) 28547, (short) 27953, (short) 27327, (short) 26669, (short) 25982, (short) 25268, (short) 24527, (short) 23763, (short) 22977, (short) 22170, (short) 21345, (short) 20504, (short) 19649, (short) 18782, (short) 17904, (short) 17019, (short) 16128, (short) 15232, (short) 14335, (short) 13439, (short) 12544, (short) 11654, (short) 10769, (short) 9893, (short) 9027, (short) 8173, (short) 7332, (short) 6506, (short) 5697, (short) 4906, (short) 4135, (short) 3386, (short) 2658, (short) 1955, (short) 1277, (short) 625, (short) 0, (short) -597, (short) -1166, (short) -1705, (short) -2214, (short) -2693, (short) -3141, (short) -3558, (short) -3944, (short) -4298, (short) -4621, (short) -4912, (short) -5172, (short) -5401, (short) -5599, (short) -5767, (short) -5905, (short) -6015, (short) -6096, (short) -6149, (short) -6175, (short) -6175, (short) -6150, (short) -6101, (short) -6029, (short) -5935, (short) -5819, (short) -5685, (short) -5531, (short) -5360, (short) -5174, (short) -4972, (short) -4757, (short) -4529, (short) -4291, (short) -4043, (short) -3787, (short) -3523, (short) -3254, (short) -2980, (short) -2703, (short) -2424, (short) -2144, (short) -1864, (short) -1586, (short) -1310, (short) -1037, (short) -769, (short) -506, (short) -249, (short) 0, (short) 241, (short) 474, (short) 698, (short) 912, (short) 1115, (short) 1308, (short) 1489, (short) 1658, (short) 1815, (short) 1960, (short) 2092, (short) 2211, (short) 2317, (short) 2411, (short) 2491, (short) 2559, (short) 2614, (short) 2657, (short) 2687, (short) 2705, (short) 2711, (short) 2706, (short) 2689, (short) 2663, (short) 2625, (short) 2579, (short) 2522, (short) 2457, (short) 2384, (short) 2304, (short) 2216, (short) 2122, (short) 2022, (short) 1916, (short) 1807, (short) 1693, (short) 1575, (short) 1455, (short) 1333, (short) 1209, (short) 1084, (short) 958, (short) 833, (short) 708, (short) 584, (short) 462, (short) 342, (short) 225, (short) 111, (short) 0, (short) -107, (short) -210, (short) -309, (short) -403, (short) -492, (short) -576, (short) -655, (short) -728, (short) -796, (short) -857, (short) -913, (short) -963, (short) -1007, (short) -1046, (short) -1078, (short) -1105, (short) -1125, (short) -1141, (short) -1151, (short) -1155, (short) -1154, (short) -1149, (short) -1138, (short) -1123, (short) -1104, (short) -1080, (short) -1053, (short) -1023, (short) -989, (short) -951, (short) -912, (short) -870, (short) -825, (short) -779, (short) -731, (short) -682, (short) -632, (short) -581, (short) -530, (short) -478, (short) -426, (short) -375, (short) -324, (short) -274, (short) -225, (short) -177, (short) -130, (short) -85, (short) -41, (short) 0, (short) 39, (short) 77, (short) 113, (short) 147, (short) 178, (short) 208, (short) 234, (short) 259, (short) 281, (short) 300, (short) 318, (short) 332, (short) 345, (short) 355, (short) 363, (short) 369, (short) 373, (short) 375, (short) 374, (short) 372, (short) 369, (short) 363, (short) 357, (short) 348, (short) 339, (short) 328, (short) 317, (short) 304, (short) 291, (short) 276, (short) 262, (short) 247, (short) 231, (short) 215, (short) 200, (short) 184, (short) 168, (short) 152, (short) 137, (short) 121, (short) 107, (short) 92, (short) 79, (short) 65, (short) 53, (short) 41, (short) 29, (short) 19, (short) 9, (short) 0, (short) -8, (short) -16, (short) -22, (short) -29, (short) -34, (short) -39, (short) -43, (short) -46, (short) -48, (short) -50, (short) -52, (short) -53, (short) -53, (short) -53, (short) -52, (short) -51, (short) -50, (short) -48, (short) -47, (short) -44, (short) -42, (short) -40, (short) -37, (short) -34, (short) -32, (short) -29, (short) -26, (short) -24, (short) -21, (short) -19, (short) -17, (short) -14, (short) -12, (short) -10, (short) -9, (short) -7, (short) -6, (short) -4, (short) -3, (short) -2, (short) -2, (short) -1, (short) -1, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0};
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
        int numSamples = this.maxRequiredFrameCount / skip;
        int samplesPerValue = this.channelCount * skip;
        position *= this.channelCount;
        for (int i = 0; i < numSamples; i++) {
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
                short sVal = samples[position + i];
                short pVal = samples[(position + period) + i];
                diff += sVal >= pVal ? sVal - pVal : pVal - sVal;
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

    private boolean previousPeriodBetter(int minDiff, int maxDiff, boolean preferNewPeriod) {
        if (minDiff == 0 || this.prevPeriod == 0) {
            return false;
        }
        if (preferNewPeriod) {
            if (maxDiff > minDiff * 3 || minDiff * 2 <= this.prevMinDiff * 3) {
                return false;
            }
        } else if (minDiff <= this.prevMinDiff) {
            return false;
        }
        return true;
    }

    private int findPitchPeriod(short[] samples, int position, boolean preferNewPeriod) {
        int period;
        int retPeriod;
        int skip = 1;
        if (this.inputSampleRateHz > AMDF_FREQUENCY && 1 == 0) {
            skip = this.inputSampleRateHz / AMDF_FREQUENCY;
        }
        if (this.channelCount == 1 && skip == 1) {
            period = findPitchPeriodInRange(samples, position, this.minPeriod, this.maxPeriod);
        } else {
            downSampleInput(samples, position, skip);
            period = findPitchPeriodInRange(this.downSampleBuffer, 0, this.minPeriod / skip, this.maxPeriod / skip);
            if (skip != 1) {
                period *= skip;
                int minP = period - (skip << 2);
                int maxP = period + (skip << 2);
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
        if (previousPeriodBetter(this.minDiff, this.maxDiff, preferNewPeriod)) {
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

    private void adjustPitch(int originalNumOutputSamples) {
        int position = 0;
        if (this.outputFrameCount != originalNumOutputSamples) {
            moveNewSamplesToPitchBuffer(originalNumOutputSamples);
            while (this.pitchFrameCount - position >= this.maxRequiredFrameCount) {
                int period = findPitchPeriod(this.pitchBuffer, position, false);
                int newPeriod = (int) (((float) period) / this.pitch);
                this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, newPeriod);
                if (this.pitch >= 1.0f) {
                    overlapAdd(newPeriod, this.channelCount, this.outputBuffer, this.outputFrameCount, this.pitchBuffer, position, this.pitchBuffer, (position + period) - newPeriod);
                } else {
                    int i = period;
                    overlapAddWithSeparation(i, this.channelCount, newPeriod - period, this.outputBuffer, this.outputFrameCount, this.pitchBuffer, position, this.pitchBuffer, position);
                }
                this.outputFrameCount += newPeriod;
                position += period;
            }
            removePitchFrames(position);
        }
    }

    private int findSincCoefficient(int i, int ratio, int width) {
        int left = (i * 50) + ((ratio * 50) / width);
        int right = left + 1;
        int position = (((i * 50) * width) + (ratio * 50)) - (left * width);
        return ((((width - position) * sincTable[left]) + (sincTable[right] * position)) << 1) / width;
    }

    private int getSign(int value) {
        return value >= 0 ? 1 : 0;
    }

    private short interpolate(short[] in, int inPos, int oldSampleRate, int newSampleRate) {
        int rightPosition = (this.oldRatePosition + 1) * newSampleRate;
        int ratio = rightPosition - (this.newRatePosition * oldSampleRate);
        int width = rightPosition - (this.oldRatePosition * newSampleRate);
        return (short) (((ratio * in[inPos]) + ((width - ratio) * in[this.channelCount + inPos])) / width);
    }

    private void adjustRate(float rate, int originalNumOutputSamples) {
        int newSampleRate = (int) (((float) this.inputSampleRateHz) / rate);
        int oldSampleRate = this.inputSampleRateHz;
        while (true) {
            if (newSampleRate <= MessagesController.UPDATE_MASK_CHAT_ADMINS && oldSampleRate <= MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                break;
            }
            newSampleRate >>= 1;
            oldSampleRate >>= 1;
        }
        moveNewSamplesToPitchBuffer(originalNumOutputSamples);
        int position = 0;
        while (position < this.pitchFrameCount - 1) {
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
            position++;
        }
        removePitchFrames(position);
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
            int numSamples = this.inputFrameCount;
            int position = 0;
            do {
                if (this.remainingInputToCopyFrameCount > 0) {
                    position += copyInputToOutput(position);
                } else {
                    int period = findPitchPeriod(this.inputBuffer, position, true);
                    if (((double) speed) > 1.0d) {
                        position += period + skipPitchPeriod(this.inputBuffer, position, speed, period);
                    } else {
                        position += insertPitchPeriod(this.inputBuffer, position, speed, period);
                    }
                }
            } while (this.maxRequiredFrameCount + position <= numSamples);
            removeProcessedInputFrames(position);
        }
    }

    private void processStreamInput() {
        int originalNumOutputSamples = this.outputFrameCount;
        float s = this.speed / this.pitch;
        float r = this.rate;
        if (null == null) {
            r *= this.pitch;
        }
        if (((double) s) > 1.00001d || ((double) s) < 0.99999d) {
            changeSpeed(s);
        } else {
            copyToOutput(this.inputBuffer, 0, this.inputFrameCount);
            this.inputFrameCount = 0;
        }
        if (null != null) {
            if (this.pitch != 1.0f) {
                adjustPitch(originalNumOutputSamples);
            }
        } else if (r != 1.0f) {
            adjustRate(r, originalNumOutputSamples);
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

    private static void overlapAddWithSeparation(int numSamples, int numChannels, int separation, short[] out, int outPos, short[] rampDown, int rampDownPos, short[] rampUp, int rampUpPos) {
        for (int i = 0; i < numChannels; i++) {
            int o = (outPos * numChannels) + i;
            int u = (rampUpPos * numChannels) + i;
            int d = (rampDownPos * numChannels) + i;
            for (int t = 0; t < numSamples + separation; t++) {
                if (t < separation) {
                    out[o] = (short) ((rampDown[d] * (numSamples - t)) / numSamples);
                    d += numChannels;
                } else if (t < numSamples) {
                    out[o] = (short) (((rampDown[d] * (numSamples - t)) + (rampUp[u] * (t - separation))) / numSamples);
                    d += numChannels;
                    u += numChannels;
                } else {
                    out[o] = (short) ((rampUp[u] * (t - separation)) / numSamples);
                    u += numChannels;
                }
                o += numChannels;
            }
        }
    }
}
