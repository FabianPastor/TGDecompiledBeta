package org.telegram.messenger.exoplayer2.util;

public final class FlacStreamInfo
{
  public final int bitsPerSample;
  public final int channels;
  public final int maxBlockSize;
  public final int maxFrameSize;
  public final int minBlockSize;
  public final int minFrameSize;
  public final int sampleRate;
  public final long totalSamples;
  
  public FlacStreamInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, long paramLong)
  {
    this.minBlockSize = paramInt1;
    this.maxBlockSize = paramInt2;
    this.minFrameSize = paramInt3;
    this.maxFrameSize = paramInt4;
    this.sampleRate = paramInt5;
    this.channels = paramInt6;
    this.bitsPerSample = paramInt7;
    this.totalSamples = paramLong;
  }
  
  public FlacStreamInfo(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte = new ParsableBitArray(paramArrayOfByte);
    paramArrayOfByte.setPosition(paramInt * 8);
    this.minBlockSize = paramArrayOfByte.readBits(16);
    this.maxBlockSize = paramArrayOfByte.readBits(16);
    this.minFrameSize = paramArrayOfByte.readBits(24);
    this.maxFrameSize = paramArrayOfByte.readBits(24);
    this.sampleRate = paramArrayOfByte.readBits(20);
    this.channels = (paramArrayOfByte.readBits(3) + 1);
    this.bitsPerSample = (paramArrayOfByte.readBits(5) + 1);
    this.totalSamples = ((paramArrayOfByte.readBits(4) & 0xF) << 32 | paramArrayOfByte.readBits(32) & 0xFFFFFFFF);
  }
  
  public int bitRate()
  {
    return this.bitsPerSample * this.sampleRate;
  }
  
  public long durationUs()
  {
    return this.totalSamples * 1000000L / this.sampleRate;
  }
  
  public int maxDecodedFrameSize()
  {
    return this.maxBlockSize * this.channels * (this.bitsPerSample / 8);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/FlacStreamInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */