package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.exoplayer2.util.Util;

final class TrimmingAudioProcessor
  implements AudioProcessor
{
  private ByteBuffer buffer = EMPTY_BUFFER;
  private int channelCount = -1;
  private byte[] endBuffer;
  private int endBufferSize;
  private boolean inputEnded;
  private boolean isActive;
  private ByteBuffer outputBuffer = EMPTY_BUFFER;
  private int pendingTrimStartBytes;
  private int sampleRateHz;
  private int trimEndSamples;
  private int trimStartSamples;
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    boolean bool1 = true;
    if (paramInt3 != 2) {
      throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
    }
    this.channelCount = paramInt2;
    this.sampleRateHz = paramInt1;
    this.endBuffer = new byte[this.trimEndSamples * paramInt2 * 2];
    this.endBufferSize = 0;
    this.pendingTrimStartBytes = (this.trimStartSamples * paramInt2 * 2);
    boolean bool2 = this.isActive;
    if ((this.trimStartSamples != 0) || (this.trimEndSamples != 0))
    {
      bool3 = true;
      this.isActive = bool3;
      if (bool2 == this.isActive) {
        break label111;
      }
    }
    label111:
    for (boolean bool3 = bool1;; bool3 = false)
    {
      return bool3;
      bool3 = false;
      break;
    }
  }
  
  public void flush()
  {
    this.outputBuffer = EMPTY_BUFFER;
    this.inputEnded = false;
    this.pendingTrimStartBytes = 0;
    this.endBufferSize = 0;
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
    return 2;
  }
  
  public int getOutputSampleRateHz()
  {
    return this.sampleRateHz;
  }
  
  public boolean isActive()
  {
    return this.isActive;
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
    int k = j - i;
    int m = Math.min(k, this.pendingTrimStartBytes);
    this.pendingTrimStartBytes -= m;
    paramByteBuffer.position(i + m);
    if (this.pendingTrimStartBytes > 0) {
      return;
    }
    k -= m;
    m = this.endBufferSize + k - this.endBuffer.length;
    if (this.buffer.capacity() < m) {
      this.buffer = ByteBuffer.allocateDirect(m).order(ByteOrder.nativeOrder());
    }
    for (;;)
    {
      i = Util.constrainValue(m, 0, this.endBufferSize);
      this.buffer.put(this.endBuffer, 0, i);
      m = Util.constrainValue(m - i, 0, k);
      paramByteBuffer.limit(paramByteBuffer.position() + m);
      this.buffer.put(paramByteBuffer);
      paramByteBuffer.limit(j);
      j = k - m;
      this.endBufferSize -= i;
      System.arraycopy(this.endBuffer, i, this.endBuffer, 0, this.endBufferSize);
      paramByteBuffer.get(this.endBuffer, this.endBufferSize, j);
      this.endBufferSize += j;
      this.buffer.flip();
      this.outputBuffer = this.buffer;
      break;
      this.buffer.clear();
    }
  }
  
  public void reset()
  {
    flush();
    this.buffer = EMPTY_BUFFER;
    this.channelCount = -1;
    this.sampleRateHz = -1;
    this.endBuffer = null;
  }
  
  public void setTrimSampleCount(int paramInt1, int paramInt2)
  {
    this.trimStartSamples = paramInt1;
    this.trimEndSamples = paramInt2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/TrimmingAudioProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */