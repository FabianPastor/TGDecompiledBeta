package org.telegram.messenger.exoplayer2.audio;

import java.nio.ShortBuffer;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class Sonic
{
  private static final int AMDF_FREQUENCY = 4000;
  private static final int MAXIMUM_PITCH = 400;
  private static final int MINIMUM_PITCH = 65;
  private final short[] downSampleBuffer;
  private short[] inputBuffer;
  private int inputBufferSize;
  private final int inputSampleRateHz;
  private int maxDiff;
  private final int maxPeriod;
  private final int maxRequired;
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
  
  public Sonic(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, int paramInt3)
  {
    this.inputSampleRateHz = paramInt1;
    this.numChannels = paramInt2;
    this.minPeriod = (paramInt1 / 400);
    this.maxPeriod = (paramInt1 / 65);
    this.maxRequired = (this.maxPeriod * 2);
    this.downSampleBuffer = new short[this.maxRequired];
    this.inputBufferSize = this.maxRequired;
    this.inputBuffer = new short[this.maxRequired * paramInt2];
    this.outputBufferSize = this.maxRequired;
    this.outputBuffer = new short[this.maxRequired * paramInt2];
    this.pitchBufferSize = this.maxRequired;
    this.pitchBuffer = new short[this.maxRequired * paramInt2];
    this.oldRatePosition = 0;
    this.newRatePosition = 0;
    this.prevPeriod = 0;
    this.speed = paramFloat1;
    this.pitch = paramFloat2;
    this.rate = (paramInt1 / paramInt3);
  }
  
  private void adjustRate(float paramFloat, int paramInt)
  {
    if (this.numOutputSamples == paramInt) {}
    for (;;)
    {
      return;
      int i = (int)(this.inputSampleRateHz / paramFloat);
      int j = this.inputSampleRateHz;
      while ((i > 16384) || (j > 16384))
      {
        i /= 2;
        j /= 2;
      }
      moveNewSamplesToPitchBuffer(paramInt);
      paramInt = 0;
      if (paramInt < this.numPitchSamples - 1)
      {
        while ((this.oldRatePosition + 1) * i > this.newRatePosition * j)
        {
          enlargeOutputBufferIfNeeded(1);
          for (int k = 0; k < this.numChannels; k++) {
            this.outputBuffer[(this.numOutputSamples * this.numChannels + k)] = interpolate(this.pitchBuffer, this.numChannels * paramInt + k, j, i);
          }
          this.newRatePosition += 1;
          this.numOutputSamples += 1;
        }
        this.oldRatePosition += 1;
        if (this.oldRatePosition == j)
        {
          this.oldRatePosition = 0;
          if (this.newRatePosition != i) {
            break label221;
          }
        }
        label221:
        for (boolean bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          this.newRatePosition = 0;
          paramInt++;
          break;
        }
      }
      removePitchSamples(this.numPitchSamples - 1);
    }
  }
  
  private void changeSpeed(float paramFloat)
  {
    if (this.numInputSamples < this.maxRequired) {
      return;
    }
    int i = this.numInputSamples;
    int j = 0;
    label19:
    int k;
    if (this.remainingInputToCopy > 0) {
      k = j + copyInputToOutput(j);
    }
    for (;;)
    {
      j = k;
      if (this.maxRequired + k <= i) {
        break label19;
      }
      removeProcessedInputSamples(k);
      break;
      k = findPitchPeriod(this.inputBuffer, j, true);
      if (paramFloat > 1.0D) {
        k = j + (skipPitchPeriod(this.inputBuffer, j, paramFloat, k) + k);
      } else {
        k = j + insertPitchPeriod(this.inputBuffer, j, paramFloat, k);
      }
    }
  }
  
  private int copyInputToOutput(int paramInt)
  {
    int i = Math.min(this.maxRequired, this.remainingInputToCopy);
    copyToOutput(this.inputBuffer, paramInt, i);
    this.remainingInputToCopy -= i;
    return i;
  }
  
  private void copyToOutput(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    enlargeOutputBufferIfNeeded(paramInt2);
    System.arraycopy(paramArrayOfShort, this.numChannels * paramInt1, this.outputBuffer, this.numOutputSamples * this.numChannels, this.numChannels * paramInt2);
    this.numOutputSamples += paramInt2;
  }
  
  private void downSampleInput(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    int i = this.maxRequired / paramInt2;
    int j = this.numChannels * paramInt2;
    int k = this.numChannels;
    for (paramInt2 = 0; paramInt2 < i; paramInt2++)
    {
      int m = 0;
      for (int n = 0; n < j; n++) {
        m += paramArrayOfShort[(paramInt2 * j + paramInt1 * k + n)];
      }
      n = m / j;
      this.downSampleBuffer[paramInt2] = ((short)(short)n);
    }
  }
  
  private void enlargeInputBufferIfNeeded(int paramInt)
  {
    if (this.numInputSamples + paramInt > this.inputBufferSize)
    {
      this.inputBufferSize += this.inputBufferSize / 2 + paramInt;
      this.inputBuffer = Arrays.copyOf(this.inputBuffer, this.inputBufferSize * this.numChannels);
    }
  }
  
  private void enlargeOutputBufferIfNeeded(int paramInt)
  {
    if (this.numOutputSamples + paramInt > this.outputBufferSize)
    {
      this.outputBufferSize += this.outputBufferSize / 2 + paramInt;
      this.outputBuffer = Arrays.copyOf(this.outputBuffer, this.outputBufferSize * this.numChannels);
    }
  }
  
  private int findPitchPeriod(short[] paramArrayOfShort, int paramInt, boolean paramBoolean)
  {
    int i;
    int j;
    if (this.inputSampleRateHz > 4000)
    {
      i = this.inputSampleRateHz / 4000;
      if ((this.numChannels != 1) || (i != 1)) {
        break label93;
      }
      j = findPitchPeriodInRange(paramArrayOfShort, paramInt, this.minPeriod, this.maxPeriod);
      label50:
      if (!previousPeriodBetter(this.minDiff, this.maxDiff, paramBoolean)) {
        break label247;
      }
    }
    label93:
    label247:
    for (paramInt = this.prevPeriod;; paramInt = j)
    {
      this.prevMinDiff = this.minDiff;
      this.prevPeriod = j;
      return paramInt;
      i = 1;
      break;
      downSampleInput(paramArrayOfShort, paramInt, i);
      int k = findPitchPeriodInRange(this.downSampleBuffer, 0, this.minPeriod / i, this.maxPeriod / i);
      j = k;
      if (i == 1) {
        break label50;
      }
      j = k * i;
      int m = j - i * 4;
      k = j + i * 4;
      j = m;
      if (m < this.minPeriod) {
        j = this.minPeriod;
      }
      i = k;
      if (k > this.maxPeriod) {
        i = this.maxPeriod;
      }
      if (this.numChannels == 1)
      {
        j = findPitchPeriodInRange(paramArrayOfShort, paramInt, j, i);
        break label50;
      }
      downSampleInput(paramArrayOfShort, paramInt, 1);
      j = findPitchPeriodInRange(this.downSampleBuffer, 0, j, i);
      break label50;
    }
  }
  
  private int findPitchPeriodInRange(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    int j = 255;
    int k = 1;
    int m = 0;
    int n = paramInt1 * this.numChannels;
    paramInt1 = paramInt2;
    while (paramInt1 <= paramInt3)
    {
      paramInt2 = 0;
      for (int i1 = 0; i1 < paramInt1; i1++) {
        paramInt2 += Math.abs(paramArrayOfShort[(n + i1)] - paramArrayOfShort[(n + paramInt1 + i1)]);
      }
      int i2 = i;
      i1 = k;
      if (paramInt2 * i < k * paramInt1)
      {
        i1 = paramInt2;
        i2 = paramInt1;
      }
      k = m;
      int i3 = j;
      if (paramInt2 * j > m * paramInt1)
      {
        i3 = paramInt1;
        k = paramInt2;
      }
      paramInt1++;
      i = i2;
      m = k;
      k = i1;
      j = i3;
    }
    this.minDiff = (k / i);
    this.maxDiff = (m / j);
    return i;
  }
  
  private int insertPitchPeriod(short[] paramArrayOfShort, int paramInt1, float paramFloat, int paramInt2)
  {
    int i;
    if (paramFloat < 0.5F) {
      i = (int)(paramInt2 * paramFloat / (1.0F - paramFloat));
    }
    for (;;)
    {
      enlargeOutputBufferIfNeeded(paramInt2 + i);
      System.arraycopy(paramArrayOfShort, this.numChannels * paramInt1, this.outputBuffer, this.numOutputSamples * this.numChannels, this.numChannels * paramInt2);
      overlapAdd(i, this.numChannels, this.outputBuffer, this.numOutputSamples + paramInt2, paramArrayOfShort, paramInt1 + paramInt2, paramArrayOfShort, paramInt1);
      this.numOutputSamples += paramInt2 + i;
      return i;
      i = paramInt2;
      this.remainingInputToCopy = ((int)(paramInt2 * (2.0F * paramFloat - 1.0F) / (1.0F - paramFloat)));
    }
  }
  
  private short interpolate(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramArrayOfShort[paramInt1];
    paramInt1 = paramArrayOfShort[(this.numChannels + paramInt1)];
    int j = this.newRatePosition;
    int k = this.oldRatePosition;
    int m = (this.oldRatePosition + 1) * paramInt3;
    paramInt2 = m - j * paramInt2;
    paramInt3 = m - k * paramInt3;
    return (short)((paramInt2 * i + (paramInt3 - paramInt2) * paramInt1) / paramInt3);
  }
  
  private void moveNewSamplesToPitchBuffer(int paramInt)
  {
    int i = this.numOutputSamples - paramInt;
    if (this.numPitchSamples + i > this.pitchBufferSize)
    {
      this.pitchBufferSize += this.pitchBufferSize / 2 + i;
      this.pitchBuffer = Arrays.copyOf(this.pitchBuffer, this.pitchBufferSize * this.numChannels);
    }
    System.arraycopy(this.outputBuffer, this.numChannels * paramInt, this.pitchBuffer, this.numPitchSamples * this.numChannels, this.numChannels * i);
    this.numOutputSamples = paramInt;
    this.numPitchSamples += i;
  }
  
  private static void overlapAdd(int paramInt1, int paramInt2, short[] paramArrayOfShort1, int paramInt3, short[] paramArrayOfShort2, int paramInt4, short[] paramArrayOfShort3, int paramInt5)
  {
    for (int i = 0; i < paramInt2; i++)
    {
      int j = paramInt3 * paramInt2 + i;
      int k = paramInt5 * paramInt2 + i;
      int m = paramInt4 * paramInt2 + i;
      for (int n = 0; n < paramInt1; n++)
      {
        paramArrayOfShort1[j] = ((short)(short)((paramArrayOfShort2[m] * (paramInt1 - n) + paramArrayOfShort3[k] * n) / paramInt1));
        j += paramInt2;
        m += paramInt2;
        k += paramInt2;
      }
    }
  }
  
  private boolean previousPeriodBetter(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramInt1 != 0)
    {
      if (this.prevPeriod != 0) {
        break label25;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label25:
      if (paramBoolean)
      {
        bool2 = bool1;
        if (paramInt2 <= paramInt1 * 3)
        {
          bool2 = bool1;
          if (paramInt1 * 2 <= this.prevMinDiff * 3) {}
        }
      }
      else
      {
        while (paramInt1 > this.prevMinDiff)
        {
          bool2 = true;
          break;
        }
        bool2 = bool1;
      }
    }
  }
  
  private void processStreamInput()
  {
    int i = this.numOutputSamples;
    float f1 = this.speed / this.pitch;
    float f2 = this.rate * this.pitch;
    if ((f1 > 1.00001D) || (f1 < 0.99999D)) {
      changeSpeed(f1);
    }
    for (;;)
    {
      if (f2 != 1.0F) {
        adjustRate(f2, i);
      }
      return;
      copyToOutput(this.inputBuffer, 0, this.numInputSamples);
      this.numInputSamples = 0;
    }
  }
  
  private void removePitchSamples(int paramInt)
  {
    if (paramInt == 0) {}
    for (;;)
    {
      return;
      System.arraycopy(this.pitchBuffer, this.numChannels * paramInt, this.pitchBuffer, 0, (this.numPitchSamples - paramInt) * this.numChannels);
      this.numPitchSamples -= paramInt;
    }
  }
  
  private void removeProcessedInputSamples(int paramInt)
  {
    int i = this.numInputSamples - paramInt;
    System.arraycopy(this.inputBuffer, this.numChannels * paramInt, this.inputBuffer, 0, this.numChannels * i);
    this.numInputSamples = i;
  }
  
  private int skipPitchPeriod(short[] paramArrayOfShort, int paramInt1, float paramFloat, int paramInt2)
  {
    int i;
    if (paramFloat >= 2.0F) {
      i = (int)(paramInt2 / (paramFloat - 1.0F));
    }
    for (;;)
    {
      enlargeOutputBufferIfNeeded(i);
      overlapAdd(i, this.numChannels, this.outputBuffer, this.numOutputSamples, paramArrayOfShort, paramInt1, paramArrayOfShort, paramInt1 + paramInt2);
      this.numOutputSamples += i;
      return i;
      i = paramInt2;
      this.remainingInputToCopy = ((int)(paramInt2 * (2.0F - paramFloat) / (paramFloat - 1.0F)));
    }
  }
  
  public void getOutput(ShortBuffer paramShortBuffer)
  {
    int i = Math.min(paramShortBuffer.remaining() / this.numChannels, this.numOutputSamples);
    paramShortBuffer.put(this.outputBuffer, 0, this.numChannels * i);
    this.numOutputSamples -= i;
    System.arraycopy(this.outputBuffer, this.numChannels * i, this.outputBuffer, 0, this.numOutputSamples * this.numChannels);
  }
  
  public int getSamplesAvailable()
  {
    return this.numOutputSamples;
  }
  
  public void queueEndOfStream()
  {
    int i = this.numInputSamples;
    float f1 = this.speed / this.pitch;
    float f2 = this.rate;
    float f3 = this.pitch;
    int j = this.numOutputSamples + (int)((i / f1 + this.numPitchSamples) / (f2 * f3) + 0.5F);
    enlargeInputBufferIfNeeded(this.maxRequired * 2 + i);
    for (int k = 0; k < this.maxRequired * 2 * this.numChannels; k++) {
      this.inputBuffer[(this.numChannels * i + k)] = ((short)0);
    }
    this.numInputSamples += this.maxRequired * 2;
    processStreamInput();
    if (this.numOutputSamples > j) {
      this.numOutputSamples = j;
    }
    this.numInputSamples = 0;
    this.remainingInputToCopy = 0;
    this.numPitchSamples = 0;
  }
  
  public void queueInput(ShortBuffer paramShortBuffer)
  {
    int i = paramShortBuffer.remaining() / this.numChannels;
    int j = this.numChannels;
    enlargeInputBufferIfNeeded(i);
    paramShortBuffer.get(this.inputBuffer, this.numInputSamples * this.numChannels, j * i * 2 / 2);
    this.numInputSamples += i;
    processStreamInput();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/Sonic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */