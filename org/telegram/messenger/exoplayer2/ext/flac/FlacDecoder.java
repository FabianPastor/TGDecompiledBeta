package org.telegram.messenger.exoplayer2.ext.flac;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.telegram.messenger.exoplayer2.util.FlacStreamInfo;

final class FlacDecoder
  extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FlacDecoderException>
{
  private final FlacDecoderJni decoderJni;
  private final int maxOutputBufferSize;
  
  public FlacDecoder(int paramInt1, int paramInt2, List<byte[]> paramList)
    throws FlacDecoderException
  {
    super(new DecoderInputBuffer[paramInt1], new SimpleOutputBuffer[paramInt2]);
    if (paramList.size() != 1) {
      throw new FlacDecoderException("Initialization data must be of length 1");
    }
    this.decoderJni = new FlacDecoderJni();
    this.decoderJni.setData(ByteBuffer.wrap((byte[])paramList.get(0)));
    try
    {
      paramList = this.decoderJni.decodeMetadata();
      if (paramList == null) {
        throw new FlacDecoderException("Metadata decoding failed");
      }
    }
    catch (InterruptedException paramList)
    {
      throw new IllegalStateException(paramList);
      setInitialInputBufferSize(paramList.maxFrameSize);
      this.maxOutputBufferSize = paramList.maxDecodedFrameSize();
      return;
    }
    catch (IOException paramList)
    {
      for (;;) {}
    }
  }
  
  protected DecoderInputBuffer createInputBuffer()
  {
    return new DecoderInputBuffer(1);
  }
  
  protected SimpleOutputBuffer createOutputBuffer()
  {
    return new SimpleOutputBuffer(this);
  }
  
  protected FlacDecoderException createUnexpectedDecodeException(Throwable paramThrowable)
  {
    return new FlacDecoderException("Unexpected decode error", paramThrowable);
  }
  
  protected FlacDecoderException decode(DecoderInputBuffer paramDecoderInputBuffer, SimpleOutputBuffer paramSimpleOutputBuffer, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.decoderJni.flush();
    }
    this.decoderJni.setData(paramDecoderInputBuffer.data);
    paramDecoderInputBuffer = paramSimpleOutputBuffer.init(paramDecoderInputBuffer.timeUs, this.maxOutputBufferSize);
    try
    {
      i = this.decoderJni.decodeSample(paramDecoderInputBuffer);
      if (i >= 0) {
        break label72;
      }
      paramDecoderInputBuffer = new FlacDecoderException("Frame decoding failed");
    }
    catch (InterruptedException paramDecoderInputBuffer)
    {
      for (;;)
      {
        int i;
        throw new IllegalStateException(paramDecoderInputBuffer);
        paramDecoderInputBuffer.position(0);
        paramDecoderInputBuffer.limit(i);
        paramDecoderInputBuffer = null;
      }
    }
    catch (IOException paramDecoderInputBuffer)
    {
      for (;;) {}
    }
    return paramDecoderInputBuffer;
  }
  
  public String getName()
  {
    return "libflac";
  }
  
  public void release()
  {
    super.release();
    this.decoderJni.release();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ext/flac/FlacDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */