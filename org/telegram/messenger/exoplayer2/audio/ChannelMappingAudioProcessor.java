package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

final class ChannelMappingAudioProcessor
  implements AudioProcessor
{
  private boolean active;
  private ByteBuffer buffer = EMPTY_BUFFER;
  private int channelCount = -1;
  private boolean inputEnded;
  private ByteBuffer outputBuffer = EMPTY_BUFFER;
  private int[] outputChannels;
  private int[] pendingOutputChannels;
  private int sampleRateHz = -1;
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    int i;
    if (!Arrays.equals(this.pendingOutputChannels, this.outputChannels))
    {
      i = 1;
      this.outputChannels = this.pendingOutputChannels;
      if (this.outputChannels != null) {
        break label46;
      }
      this.active = false;
    }
    for (;;)
    {
      return i;
      i = 0;
      break;
      label46:
      if (paramInt3 != 2) {
        throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
      }
      if ((i == 0) && (this.sampleRateHz == paramInt1) && (this.channelCount == paramInt2))
      {
        i = 0;
      }
      else
      {
        this.sampleRateHz = paramInt1;
        this.channelCount = paramInt2;
        if (paramInt2 != this.outputChannels.length) {}
        int j;
        for (i = 1;; i = 0)
        {
          this.active = i;
          j = 0;
          if (j >= this.outputChannels.length) {
            break label199;
          }
          k = this.outputChannels[j];
          if (k < paramInt2) {
            break;
          }
          throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
        }
        i = this.active;
        if (k != j) {}
        for (int k = 1;; k = 0)
        {
          this.active = (k | i);
          j++;
          break;
        }
        label199:
        i = 1;
      }
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
    if (this.outputChannels == null) {}
    for (int i = this.channelCount;; i = this.outputChannels.length) {
      return i;
    }
  }
  
  public int getOutputEncoding()
  {
    return 2;
  }
  
  public int getOutputSampleRateHz()
  {
    return this.sampleRateHz;
  }
  
  public boolean isActive()
  {
    return this.active;
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
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    int k = (j - i) / (this.channelCount * 2);
    k = this.outputChannels.length * k * 2;
    if (this.buffer.capacity() < k) {
      this.buffer = ByteBuffer.allocateDirect(k).order(ByteOrder.nativeOrder());
    }
    while (i < j)
    {
      int[] arrayOfInt = this.outputChannels;
      int m = arrayOfInt.length;
      k = 0;
      for (;;)
      {
        if (k < m)
        {
          int n = arrayOfInt[k];
          this.buffer.putShort(paramByteBuffer.getShort(n * 2 + i));
          k++;
          continue;
          this.buffer.clear();
          break;
        }
      }
      i += this.channelCount * 2;
    }
    paramByteBuffer.position(j);
    this.buffer.flip();
    this.outputBuffer = this.buffer;
  }
  
  public void reset()
  {
    flush();
    this.buffer = EMPTY_BUFFER;
    this.channelCount = -1;
    this.sampleRateHz = -1;
    this.outputChannels = null;
    this.active = false;
  }
  
  public void setChannelMap(int[] paramArrayOfInt)
  {
    this.pendingOutputChannels = paramArrayOfInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/ChannelMappingAudioProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */