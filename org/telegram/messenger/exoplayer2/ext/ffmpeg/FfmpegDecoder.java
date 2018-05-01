package org.telegram.messenger.exoplayer2.ext.ffmpeg;

import java.nio.ByteBuffer;
import java.util.List;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class FfmpegDecoder
  extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FfmpegDecoderException>
{
  private static final int OUTPUT_BUFFER_SIZE_16BIT = 49152;
  private static final int OUTPUT_BUFFER_SIZE_32BIT = 98304;
  private volatile int channelCount;
  private final String codecName;
  private final int encoding;
  private final byte[] extraData;
  private boolean hasOutputFormat;
  private long nativeContext;
  private final int outputBufferSize;
  private volatile int sampleRate;
  
  public FfmpegDecoder(int paramInt1, int paramInt2, int paramInt3, String paramString, List<byte[]> paramList, boolean paramBoolean)
    throws FfmpegDecoderException
  {
    super(new DecoderInputBuffer[paramInt1], new SimpleOutputBuffer[paramInt2]);
    this.codecName = FfmpegLibrary.getCodecName(paramString);
    this.extraData = getExtraData(paramString, paramList);
    if (paramBoolean)
    {
      paramInt1 = 4;
      this.encoding = paramInt1;
      if (!paramBoolean) {
        break label99;
      }
    }
    label99:
    for (paramInt1 = 98304;; paramInt1 = 49152)
    {
      this.outputBufferSize = paramInt1;
      this.nativeContext = ffmpegInitialize(this.codecName, this.extraData, paramBoolean);
      if (this.nativeContext != 0L) {
        break label105;
      }
      throw new FfmpegDecoderException("Initialization failed.");
      paramInt1 = 2;
      break;
    }
    label105:
    setInitialInputBufferSize(paramInt3);
  }
  
  private native int ffmpegDecode(long paramLong, ByteBuffer paramByteBuffer1, int paramInt1, ByteBuffer paramByteBuffer2, int paramInt2);
  
  private native int ffmpegGetChannelCount(long paramLong);
  
  private native int ffmpegGetSampleRate(long paramLong);
  
  private native long ffmpegInitialize(String paramString, byte[] paramArrayOfByte, boolean paramBoolean);
  
  private native void ffmpegRelease(long paramLong);
  
  private native long ffmpegReset(long paramLong, byte[] paramArrayOfByte);
  
  private static byte[] getExtraData(String paramString, List<byte[]> paramList)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        paramString = null;
      }
      break;
    }
    for (;;)
    {
      return paramString;
      if (!paramString.equals("audio/mp4a-latm")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("audio/alac")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("audio/opus")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("audio/vorbis")) {
        break;
      }
      i = 3;
      break;
      paramString = (byte[])paramList.get(0);
      continue;
      byte[] arrayOfByte = (byte[])paramList.get(0);
      paramList = (byte[])paramList.get(1);
      paramString = new byte[arrayOfByte.length + paramList.length + 6];
      paramString[0] = ((byte)(byte)(arrayOfByte.length >> 8));
      paramString[1] = ((byte)(byte)(arrayOfByte.length & 0xFF));
      System.arraycopy(arrayOfByte, 0, paramString, 2, arrayOfByte.length);
      paramString[(arrayOfByte.length + 2)] = ((byte)0);
      paramString[(arrayOfByte.length + 3)] = ((byte)0);
      paramString[(arrayOfByte.length + 4)] = ((byte)(byte)(paramList.length >> 8));
      paramString[(arrayOfByte.length + 5)] = ((byte)(byte)(paramList.length & 0xFF));
      System.arraycopy(paramList, 0, paramString, arrayOfByte.length + 6, paramList.length);
    }
  }
  
  protected DecoderInputBuffer createInputBuffer()
  {
    return new DecoderInputBuffer(2);
  }
  
  protected SimpleOutputBuffer createOutputBuffer()
  {
    return new SimpleOutputBuffer(this);
  }
  
  protected FfmpegDecoderException createUnexpectedDecodeException(Throwable paramThrowable)
  {
    return new FfmpegDecoderException("Unexpected decode error", paramThrowable);
  }
  
  protected FfmpegDecoderException decode(DecoderInputBuffer paramDecoderInputBuffer, SimpleOutputBuffer paramSimpleOutputBuffer, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.nativeContext = ffmpegReset(this.nativeContext, this.extraData);
      if (this.nativeContext == 0L) {
        paramDecoderInputBuffer = new FfmpegDecoderException("Error resetting (see logcat).");
      }
    }
    for (;;)
    {
      return paramDecoderInputBuffer;
      ByteBuffer localByteBuffer = paramDecoderInputBuffer.data;
      int i = localByteBuffer.limit();
      paramDecoderInputBuffer = paramSimpleOutputBuffer.init(paramDecoderInputBuffer.timeUs, this.outputBufferSize);
      i = ffmpegDecode(this.nativeContext, localByteBuffer, i, paramDecoderInputBuffer, this.outputBufferSize);
      if (i < 0)
      {
        paramDecoderInputBuffer = new FfmpegDecoderException("Error decoding (see logcat). Code: " + i);
      }
      else
      {
        if (!this.hasOutputFormat)
        {
          this.channelCount = ffmpegGetChannelCount(this.nativeContext);
          this.sampleRate = ffmpegGetSampleRate(this.nativeContext);
          if ((this.sampleRate == 0) && ("alac".equals(this.codecName)))
          {
            paramDecoderInputBuffer = new ParsableByteArray(this.extraData);
            paramDecoderInputBuffer.setPosition(this.extraData.length - 4);
            this.sampleRate = paramDecoderInputBuffer.readUnsignedIntToInt();
          }
          this.hasOutputFormat = true;
        }
        paramSimpleOutputBuffer.data.position(0);
        paramSimpleOutputBuffer.data.limit(i);
        paramDecoderInputBuffer = null;
      }
    }
  }
  
  public int getChannelCount()
  {
    return this.channelCount;
  }
  
  public int getEncoding()
  {
    return this.encoding;
  }
  
  public String getName()
  {
    return "ffmpeg" + FfmpegLibrary.getVersion() + "-" + this.codecName;
  }
  
  public int getSampleRate()
  {
    return this.sampleRate;
  }
  
  public void release()
  {
    super.release();
    ffmpegRelease(this.nativeContext);
    this.nativeContext = 0L;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ext/ffmpeg/FfmpegDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */