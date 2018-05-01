package org.telegram.messenger.exoplayer2.text;

import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;

public abstract class SimpleSubtitleDecoder
  extends SimpleDecoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException>
  implements SubtitleDecoder
{
  private final String name;
  
  protected SimpleSubtitleDecoder(String paramString)
  {
    super(new SubtitleInputBuffer[2], new SubtitleOutputBuffer[2]);
    this.name = paramString;
    setInitialInputBufferSize(1024);
  }
  
  protected final SubtitleInputBuffer createInputBuffer()
  {
    return new SubtitleInputBuffer();
  }
  
  protected final SubtitleOutputBuffer createOutputBuffer()
  {
    return new SimpleSubtitleOutputBuffer(this);
  }
  
  protected final SubtitleDecoderException createUnexpectedDecodeException(Throwable paramThrowable)
  {
    return new SubtitleDecoderException("Unexpected decode error", paramThrowable);
  }
  
  protected abstract Subtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws SubtitleDecoderException;
  
  protected final SubtitleDecoderException decode(SubtitleInputBuffer paramSubtitleInputBuffer, SubtitleOutputBuffer paramSubtitleOutputBuffer, boolean paramBoolean)
  {
    try
    {
      Object localObject = paramSubtitleInputBuffer.data;
      localObject = decode(((ByteBuffer)localObject).array(), ((ByteBuffer)localObject).limit(), paramBoolean);
      paramSubtitleOutputBuffer.setContent(paramSubtitleInputBuffer.timeUs, (Subtitle)localObject, paramSubtitleInputBuffer.subsampleOffsetUs);
      paramSubtitleOutputBuffer.clearFlag(Integer.MIN_VALUE);
      paramSubtitleInputBuffer = null;
    }
    catch (SubtitleDecoderException paramSubtitleInputBuffer)
    {
      for (;;) {}
    }
    return paramSubtitleInputBuffer;
  }
  
  public final String getName()
  {
    return this.name;
  }
  
  protected final void releaseOutputBuffer(SubtitleOutputBuffer paramSubtitleOutputBuffer)
  {
    super.releaseOutputBuffer(paramSubtitleOutputBuffer);
  }
  
  public void setPositionUs(long paramLong) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/SimpleSubtitleDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */