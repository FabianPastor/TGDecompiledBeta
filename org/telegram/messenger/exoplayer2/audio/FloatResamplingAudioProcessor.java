package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class FloatResamplingAudioProcessor
  implements AudioProcessor
{
  private static final int FLOAT_NAN_AS_INT = Float.floatToIntBits(NaN.0F);
  private static final double PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR = 4.656612875245797E-10D;
  private ByteBuffer buffer = EMPTY_BUFFER;
  private int channelCount = -1;
  private boolean inputEnded;
  private ByteBuffer outputBuffer = EMPTY_BUFFER;
  private int sampleRateHz = -1;
  private int sourceEncoding = 0;
  
  private static void writePcm32BitFloat(int paramInt, ByteBuffer paramByteBuffer)
  {
    int i = Float.floatToIntBits((float)(4.656612875245797E-10D * paramInt));
    paramInt = i;
    if (i == FLOAT_NAN_AS_INT) {
      paramInt = Float.floatToIntBits(0.0F);
    }
    paramByteBuffer.putInt(paramInt);
  }
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    if (!Util.isEncodingHighResolutionIntegerPcm(paramInt3)) {
      throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
    }
    if ((this.sampleRateHz == paramInt1) && (this.channelCount == paramInt2) && (this.sourceEncoding == paramInt3)) {}
    for (boolean bool = false;; bool = true)
    {
      return bool;
      this.sampleRateHz = paramInt1;
      this.channelCount = paramInt2;
      this.sourceEncoding = paramInt3;
    }
  }
  
  public void flush()
  {
    this.outputBuffer = EMPTY_BUFFER;
    this.inputEnded = false;
  }
  
  public ByteBuffer getOutput()
  {
    ByteBuffer localByteBuffer = this.outputBuffer;
    this.outputBuffer = EMPTY_BUFFER;
    return localByteBuffer;
  }
  
  public int getOutputChannelCount()
  {
    return this.channelCount;
  }
  
  public int getOutputEncoding()
  {
    return 4;
  }
  
  public int getOutputSampleRateHz()
  {
    return this.sampleRateHz;
  }
  
  public boolean isActive()
  {
    return Util.isEncodingHighResolutionIntegerPcm(this.sourceEncoding);
  }
  
  public boolean isEnded()
  {
    if ((this.inputEnded) && (this.outputBuffer == EMPTY_BUFFER)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void queueEndOfStream()
  {
    this.inputEnded = true;
  }
  
  public void queueInput(ByteBuffer paramByteBuffer)
  {
    Assertions.checkState(isActive());
    int i;
    int j;
    int k;
    int m;
    if (this.sourceEncoding == NUM)
    {
      i = 1;
      j = paramByteBuffer.position();
      k = paramByteBuffer.limit();
      m = k - j;
      if (i == 0) {
        break label148;
      }
      label39:
      if (this.buffer.capacity() >= m) {
        break label159;
      }
      this.buffer = ByteBuffer.allocateDirect(m).order(ByteOrder.nativeOrder());
    }
    for (;;)
    {
      if (i == 0) {
        break label170;
      }
      while (j < k)
      {
        writePcm32BitFloat(paramByteBuffer.get(j) & 0xFF | (paramByteBuffer.get(j + 1) & 0xFF) << 8 | (paramByteBuffer.get(j + 2) & 0xFF) << 16 | (paramByteBuffer.get(j + 3) & 0xFF) << 24, this.buffer);
        j += 4;
      }
      i = 0;
      break;
      label148:
      m = m / 3 * 4;
      break label39;
      label159:
      this.buffer.clear();
    }
    label170:
    while (j < k)
    {
      writePcm32BitFloat((paramByteBuffer.get(j) & 0xFF) << 8 | (paramByteBuffer.get(j + 1) & 0xFF) << 16 | (paramByteBuffer.get(j + 2) & 0xFF) << 24, this.buffer);
      j += 3;
    }
    paramByteBuffer.position(paramByteBuffer.limit());
    this.buffer.flip();
    this.outputBuffer = this.buffer;
  }
  
  public void reset()
  {
    flush();
    this.buffer = EMPTY_BUFFER;
    this.sampleRateHz = -1;
    this.channelCount = -1;
    this.sourceEncoding = 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/FloatResamplingAudioProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */