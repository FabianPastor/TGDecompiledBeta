package org.telegram.messenger.exoplayer2.audio;

import java.nio.ShortBuffer;
import java.util.Arrays;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class Sonic {
    private static final int AMDF_FREQUENCY = 4000;
    private static final int MAXIMUM_PITCH = 400;
    private static final int MINIMUM_PITCH = 65;
    private final short[] downSampleBuffer = new short[this.maxRequired];
    private short[] inputBuffer;
    private int inputBufferSize = this.maxRequired;
    private final int inputSampleRateHz;
    private int maxDiff;
    private final int maxPeriod;
    private final int maxRequired = (2 * this.maxPeriod);
    private int minDiff;
    private final int minPeriod;
    private int newRatePosition;
    private final int numChannels;
    private int numInputSamples;
    private int numOutputSamples;
    private int numPitchSamples;
    private int oldRatePosition;
    private short[] outputBuffer;
    private int outputBufferSize;
    private final float pitch;
    private short[] pitchBuffer;
    private int pitchBufferSize;
    private int prevMinDiff;
    private int prevPeriod;
    private final float rate;
    private int remainingInputToCopy;
    private final float speed;

    public Sonic(int i, int i2, float f, float f2, int i3) {
        this.inputSampleRateHz = i;
        this.numChannels = i2;
        this.minPeriod = i / MAXIMUM_PITCH;
        this.maxPeriod = i / 65;
        this.inputBuffer = new short[(this.maxRequired * i2)];
        this.outputBufferSize = this.maxRequired;
        this.outputBuffer = new short[(this.maxRequired * i2)];
        this.pitchBufferSize = this.maxRequired;
        this.pitchBuffer = new short[(this.maxRequired * i2)];
        this.oldRatePosition = 0;
        this.newRatePosition = 0;
        this.prevPeriod = 0;
        this.speed = f;
        this.pitch = f2;
        this.rate = ((float) i) / ((float) i3);
    }

    public void queueInput(ShortBuffer shortBuffer) {
        int remaining = shortBuffer.remaining() / this.numChannels;
        int i = (this.numChannels * remaining) * 2;
        enlargeInputBufferIfNeeded(remaining);
        shortBuffer.get(this.inputBuffer, this.numInputSamples * this.numChannels, i / 2);
        this.numInputSamples += remaining;
        processStreamInput();
    }

    public void getOutput(ShortBuffer shortBuffer) {
        int min = Math.min(shortBuffer.remaining() / this.numChannels, this.numOutputSamples);
        shortBuffer.put(this.outputBuffer, 0, this.numChannels * min);
        this.numOutputSamples -= min;
        System.arraycopy(this.outputBuffer, min * this.numChannels, this.outputBuffer, 0, this.numOutputSamples * this.numChannels);
    }

    public void queueEndOfStream() {
        int i = this.numInputSamples;
        float f = this.rate * this.pitch;
        int i2 = this.numOutputSamples + ((int) ((((((float) i) / (this.speed / this.pitch)) + ((float) this.numPitchSamples)) / f) + 0.5f));
        enlargeInputBufferIfNeeded((this.maxRequired * 2) + i);
        for (int i3 = 0; i3 < (this.maxRequired * 2) * this.numChannels; i3++) {
            this.inputBuffer[(this.numChannels * i) + i3] = (short) 0;
        }
        this.numInputSamples += 2 * this.maxRequired;
        processStreamInput();
        if (this.numOutputSamples > i2) {
            this.numOutputSamples = i2;
        }
        this.numInputSamples = 0;
        this.remainingInputToCopy = 0;
        this.numPitchSamples = 0;
    }

    public int getSamplesAvailable() {
        return this.numOutputSamples;
    }

    private void enlargeOutputBufferIfNeeded(int i) {
        if (this.numOutputSamples + i > this.outputBufferSize) {
            this.outputBufferSize += (this.outputBufferSize / 2) + i;
            this.outputBuffer = Arrays.copyOf(this.outputBuffer, this.outputBufferSize * this.numChannels);
        }
    }

    private void enlargeInputBufferIfNeeded(int i) {
        if (this.numInputSamples + i > this.inputBufferSize) {
            this.inputBufferSize += (this.inputBufferSize / 2) + i;
            this.inputBuffer = Arrays.copyOf(this.inputBuffer, this.inputBufferSize * this.numChannels);
        }
    }

    private void removeProcessedInputSamples(int i) {
        int i2 = this.numInputSamples - i;
        System.arraycopy(this.inputBuffer, i * this.numChannels, this.inputBuffer, 0, this.numChannels * i2);
        this.numInputSamples = i2;
    }

    private void copyToOutput(short[] sArr, int i, int i2) {
        enlargeOutputBufferIfNeeded(i2);
        System.arraycopy(sArr, i * this.numChannels, this.outputBuffer, this.numOutputSamples * this.numChannels, this.numChannels * i2);
        this.numOutputSamples += i2;
    }

    private int copyInputToOutput(int i) {
        int min = Math.min(this.maxRequired, this.remainingInputToCopy);
        copyToOutput(this.inputBuffer, i, min);
        this.remainingInputToCopy -= min;
        return min;
    }

    private void downSampleInput(short[] sArr, int i, int i2) {
        int i3 = this.maxRequired / i2;
        int i4 = this.numChannels * i2;
        i *= this.numChannels;
        for (int i5 = 0; i5 < i3; i5++) {
            int i6 = 0;
            int i7 = i6;
            while (i6 < i4) {
                i7 += sArr[((i5 * i4) + i) + i6];
                i6++;
            }
            this.downSampleBuffer[i5] = (short) (i7 / i4);
        }
    }

    private int findPitchPeriodInRange(short[] sArr, int i, int i2, int i3) {
        i *= this.numChannels;
        int i4 = 1;
        int i5 = 0;
        int i6 = 255;
        int i7 = i5;
        while (i2 <= i3) {
            int i8 = 0;
            int i9 = i8;
            while (i8 < i2) {
                i9 += Math.abs(sArr[i + i8] - sArr[(i + i2) + i8]);
                i8++;
            }
            if (i9 * i7 < i4 * i2) {
                i7 = i2;
                i4 = i9;
            }
            if (i9 * i6 > i5 * i2) {
                i6 = i2;
                i5 = i9;
            }
            i2++;
        }
        this.minDiff = i4 / i7;
        this.maxDiff = i5 / i6;
        return i7;
    }

    private boolean previousPeriodBetter(int i, int i2, boolean z) {
        if (i != 0) {
            if (this.prevPeriod != 0) {
                if (z) {
                    if (i2 > i * 3 || i * 2 <= this.prevMinDiff * 3) {
                        return false;
                    }
                } else if (i <= this.prevMinDiff) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private int findPitchPeriod(short[] sArr, int i, boolean z) {
        int i2 = this.inputSampleRateHz > AMDF_FREQUENCY ? this.inputSampleRateHz / AMDF_FREQUENCY : 1;
        if (this.numChannels == 1 && i2 == 1) {
            sArr = findPitchPeriodInRange(sArr, i, this.minPeriod, this.maxPeriod);
        } else {
            downSampleInput(sArr, i, i2);
            int findPitchPeriodInRange = findPitchPeriodInRange(this.downSampleBuffer, 0, this.minPeriod / i2, this.maxPeriod / i2);
            if (i2 != 1) {
                findPitchPeriodInRange *= i2;
                i2 *= 4;
                int i3 = findPitchPeriodInRange - i2;
                findPitchPeriodInRange += i2;
                if (i3 < this.minPeriod) {
                    i3 = this.minPeriod;
                }
                if (findPitchPeriodInRange > this.maxPeriod) {
                    findPitchPeriodInRange = this.maxPeriod;
                }
                if (this.numChannels == 1) {
                    sArr = findPitchPeriodInRange(sArr, i, i3, findPitchPeriodInRange);
                } else {
                    downSampleInput(sArr, i, 1);
                    sArr = findPitchPeriodInRange(this.downSampleBuffer, 0, i3, findPitchPeriodInRange);
                }
            } else {
                sArr = findPitchPeriodInRange;
            }
        }
        i = previousPeriodBetter(this.minDiff, this.maxDiff, z) != 0 ? this.prevPeriod : sArr;
        this.prevMinDiff = this.minDiff;
        this.prevPeriod = sArr;
        return i;
    }

    private void moveNewSamplesToPitchBuffer(int i) {
        int i2 = this.numOutputSamples - i;
        if (this.numPitchSamples + i2 > this.pitchBufferSize) {
            this.pitchBufferSize += (this.pitchBufferSize / 2) + i2;
            this.pitchBuffer = Arrays.copyOf(this.pitchBuffer, this.pitchBufferSize * this.numChannels);
        }
        System.arraycopy(this.outputBuffer, this.numChannels * i, this.pitchBuffer, this.numPitchSamples * this.numChannels, this.numChannels * i2);
        this.numOutputSamples = i;
        this.numPitchSamples += i2;
    }

    private void removePitchSamples(int i) {
        if (i != 0) {
            System.arraycopy(this.pitchBuffer, this.numChannels * i, this.pitchBuffer, 0, (this.numPitchSamples - i) * this.numChannels);
            this.numPitchSamples -= i;
        }
    }

    private short interpolate(short[] sArr, int i, int i2, int i3) {
        short s = sArr[i];
        sArr = sArr[i + this.numChannels];
        i = this.newRatePosition * i2;
        int i4 = (this.oldRatePosition + 1) * i3;
        i = i4 - i;
        i4 -= this.oldRatePosition * i3;
        return (short) (((s * i) + ((i4 - i) * sArr)) / i4);
    }

    private void adjustRate(float f, int i) {
        if (this.numOutputSamples != i) {
            int i2 = (int) (((float) this.inputSampleRateHz) / f);
            int i3 = this.inputSampleRateHz;
            while (true) {
                if (i2 <= 16384) {
                    if (i3 <= MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                        break;
                    }
                }
                i2 /= 2;
                i3 /= 2;
            }
            moveNewSamplesToPitchBuffer(i);
            int i4 = 0;
            while (true) {
                boolean z = true;
                if (i4 < this.numPitchSamples - 1) {
                    while ((this.oldRatePosition + 1) * i2 > this.newRatePosition * i3) {
                        enlargeOutputBufferIfNeeded(1);
                        for (int i5 = 0; i5 < this.numChannels; i5++) {
                            this.outputBuffer[(this.numOutputSamples * this.numChannels) + i5] = interpolate(this.pitchBuffer, (this.numChannels * i4) + i5, i3, i2);
                        }
                        this.newRatePosition++;
                        this.numOutputSamples++;
                    }
                    this.oldRatePosition++;
                    if (this.oldRatePosition == i3) {
                        this.oldRatePosition = 0;
                        if (this.newRatePosition != i2) {
                            z = false;
                        }
                        Assertions.checkState(z);
                        this.newRatePosition = 0;
                    }
                    i4++;
                } else {
                    removePitchSamples(this.numPitchSamples - Float.MIN_VALUE);
                    return;
                }
            }
        }
    }

    private int skipPitchPeriod(short[] sArr, int i, float f, int i2) {
        int i3;
        if (f >= 2.0f) {
            i3 = (int) (((float) i2) / (f - 1.0f));
        } else {
            this.remainingInputToCopy = (int) ((((float) i2) * (2.0f - f)) / (f - 1.0f));
            i3 = i2;
        }
        enlargeOutputBufferIfNeeded(i3);
        overlapAdd(i3, this.numChannels, this.outputBuffer, this.numOutputSamples, sArr, i, sArr, i + i2);
        this.numOutputSamples += i3;
        return i3;
    }

    private int insertPitchPeriod(short[] sArr, int i, float f, int i2) {
        int i3;
        if (f < 0.5f) {
            i3 = (int) ((((float) i2) * f) / (1.0f - f));
        } else {
            this.remainingInputToCopy = (int) ((((float) i2) * ((2.0f * f) - 1.0f)) / (1.0f - f));
            i3 = i2;
        }
        int i4 = i2 + i3;
        enlargeOutputBufferIfNeeded(i4);
        System.arraycopy(sArr, this.numChannels * i, this.outputBuffer, this.numOutputSamples * this.numChannels, this.numChannels * i2);
        overlapAdd(i3, this.numChannels, this.outputBuffer, this.numOutputSamples + i2, sArr, i + i2, sArr, i);
        this.numOutputSamples += i4;
        return i3;
    }

    private void changeSpeed(float f) {
        if (this.numInputSamples >= this.maxRequired) {
            int i = this.numInputSamples;
            int i2 = 0;
            do {
                if (this.remainingInputToCopy > 0) {
                    i2 += copyInputToOutput(i2);
                } else {
                    int findPitchPeriod = findPitchPeriod(this.inputBuffer, i2, true);
                    if (((double) f) > 1.0d) {
                        i2 += findPitchPeriod + skipPitchPeriod(this.inputBuffer, i2, f, findPitchPeriod);
                    } else {
                        i2 += insertPitchPeriod(this.inputBuffer, i2, f, findPitchPeriod);
                    }
                }
            } while (this.maxRequired + i2 <= i);
            removeProcessedInputSamples(i2);
        }
    }

    private void processStreamInput() {
        int i = this.numOutputSamples;
        float f = this.speed / this.pitch;
        float f2 = this.rate * this.pitch;
        double d = (double) f;
        if (d <= 1.00001d) {
            if (d >= 0.99999d) {
                copyToOutput(this.inputBuffer, 0, this.numInputSamples);
                this.numInputSamples = 0;
                if (f2 != 1.0f) {
                    adjustRate(f2, i);
                }
            }
        }
        changeSpeed(f);
        if (f2 != 1.0f) {
            adjustRate(f2, i);
        }
    }

    private static void overlapAdd(int i, int i2, short[] sArr, int i3, short[] sArr2, int i4, short[] sArr3, int i5) {
        for (int i6 = 0; i6 < i2; i6++) {
            int i7 = (i4 * i2) + i6;
            int i8 = (i5 * i2) + i6;
            int i9 = (i3 * i2) + i6;
            for (int i10 = 0; i10 < i; i10++) {
                sArr[i9] = (short) (((sArr2[i7] * (i - i10)) + (sArr3[i8] * i10)) / i);
                i9 += i2;
                i7 += i2;
                i8 += i2;
            }
        }
    }
}
