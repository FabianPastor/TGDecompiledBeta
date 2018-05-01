package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SonicAudioProcessor
  implements AudioProcessor
{
  private static final float CLOSE_THRESHOLD = 0.01F;
  public static final float MAXIMUM_PITCH = 8.0F;
  public static final float MAXIMUM_SPEED = 8.0F;
  public static final float MINIMUM_PITCH = 0.1F;
  public static final float MINIMUM_SPEED = 0.1F;
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
  private float pitch = 1.0F;
  private int sampleRateHz = -1;
  private ShortBuffer shortBuffer = this.buffer.asShortBuffer();
  private Sonic sonic;
  private float speed = 1.0F;
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    if (paramInt3 != 2) {
      throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
    }
    if (this.pendingOutputSampleRateHz == -1)
    {
      paramInt3 = paramInt1;
      if ((this.sampleRateHz != paramInt1) || (this.channelCount != paramInt2) || (this.outputSampleRateHz != paramInt3)) {
        break label64;
      }
    }
    for (boolean bool = false;; bool = true)
    {
      return bool;
      paramInt3 = this.pendingOutputSampleRateHz;
      break;
      label64:
      this.sampleRateHz = paramInt1;
      this.channelCount = paramInt2;
      this.outputSampleRateHz = paramInt3;
    }
  }
  
  public void flush()
  {
    this.sonic = new Sonic(this.sampleRateHz, this.channelCount, this.speed, this.pitch, this.outputSampleRateHz);
    this.outputBuffer = EMPTY_BUFFER;
    this.inputBytes = 0L;
    this.outputBytes = 0L;
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
    return 2;
  }
  
  public int getOutputSampleRateHz()
  {
    return this.outputSampleRateHz;
  }
  
  public boolean isActive()
  {
    if ((Math.abs(this.speed - 1.0F) >= 0.01F) || (Math.abs(this.pitch - 1.0F) >= 0.01F) || (this.outputSampleRateHz != this.sampleRateHz)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isEnded()
  {
    if ((this.inputEnded) && ((this.sonic == null) || (this.sonic.getSamplesAvailable() == 0))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void queueEndOfStream()
  {
    this.sonic.queueEndOfStream();
    this.inputEnded = true;
  }
  
  public void queueInput(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer.hasRemaining())
    {
      ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
      i = paramByteBuffer.remaining();
      this.inputBytes += i;
      this.sonic.queueInput(localShortBuffer);
      paramByteBuffer.position(paramByteBuffer.position() + i);
    }
    int i = this.sonic.getSamplesAvailable() * this.channelCount * 2;
    if (i > 0)
    {
      if (this.buffer.capacity() >= i) {
        break label142;
      }
      this.buffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
      this.shortBuffer = this.buffer.asShortBuffer();
    }
    for (;;)
    {
      this.sonic.getOutput(this.shortBuffer);
      this.outputBytes += i;
      this.buffer.limit(i);
      this.outputBuffer = this.buffer;
      return;
      label142:
      this.buffer.clear();
      this.shortBuffer.clear();
    }
  }
  
  public void reset()
  {
    this.sonic = null;
    this.buffer = EMPTY_BUFFER;
    this.shortBuffer = this.buffer.asShortBuffer();
    this.outputBuffer = EMPTY_BUFFER;
    this.channelCount = -1;
    this.sampleRateHz = -1;
    this.outputSampleRateHz = -1;
    this.inputBytes = 0L;
    this.outputBytes = 0L;
    this.inputEnded = false;
    this.pendingOutputSampleRateHz = -1;
  }
  
  public long scaleDurationForSpeedup(long paramLong)
  {
    if (this.outputBytes >= 1024L) {
      if (this.outputSampleRateHz == this.sampleRateHz) {
        paramLong = Util.scaleLargeTimestamp(paramLong, this.inputBytes, this.outputBytes);
      }
    }
    for (;;)
    {
      return paramLong;
      long l1 = this.inputBytes;
      long l2 = this.outputSampleRateHz;
      long l3 = this.outputBytes;
      paramLong = Util.scaleLargeTimestamp(paramLong, l2 * l1, this.sampleRateHz * l3);
      continue;
      paramLong = (this.speed * paramLong);
    }
  }
  
  public void setOutputSampleRateHz(int paramInt)
  {
    this.pendingOutputSampleRateHz = paramInt;
  }
  
  public float setPitch(float paramFloat)
  {
    this.pitch = Util.constrainValue(paramFloat, 0.1F, 8.0F);
    return paramFloat;
  }
  
  public float setSpeed(float paramFloat)
  {
    this.speed = Util.constrainValue(paramFloat, 0.1F, 8.0F);
    return this.speed;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/SonicAudioProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */