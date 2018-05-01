package org.telegram.messenger.exoplayer2.decoder;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;

public class DecoderInputBuffer
  extends Buffer
{
  public static final int BUFFER_REPLACEMENT_MODE_DIRECT = 2;
  public static final int BUFFER_REPLACEMENT_MODE_DISABLED = 0;
  public static final int BUFFER_REPLACEMENT_MODE_NORMAL = 1;
  private final int bufferReplacementMode;
  public final CryptoInfo cryptoInfo = new CryptoInfo();
  public ByteBuffer data;
  public long timeUs;
  
  public DecoderInputBuffer(int paramInt)
  {
    this.bufferReplacementMode = paramInt;
  }
  
  private ByteBuffer createReplacementByteBuffer(int paramInt)
  {
    if (this.bufferReplacementMode == 1) {}
    for (ByteBuffer localByteBuffer = ByteBuffer.allocate(paramInt);; localByteBuffer = ByteBuffer.allocateDirect(paramInt))
    {
      return localByteBuffer;
      if (this.bufferReplacementMode != 2) {
        break;
      }
    }
    if (this.data == null) {}
    for (int i = 0;; i = this.data.capacity()) {
      throw new IllegalStateException("Buffer too small (" + i + " < " + paramInt + ")");
    }
  }
  
  public static DecoderInputBuffer newFlagsOnlyInstance()
  {
    return new DecoderInputBuffer(0);
  }
  
  public void clear()
  {
    super.clear();
    if (this.data != null) {
      this.data.clear();
    }
  }
  
  public void ensureSpaceForWrite(int paramInt)
    throws IllegalStateException
  {
    if (this.data == null) {
      this.data = createReplacementByteBuffer(paramInt);
    }
    for (;;)
    {
      return;
      int i = this.data.capacity();
      int j = this.data.position();
      paramInt = j + paramInt;
      if (i < paramInt)
      {
        ByteBuffer localByteBuffer = createReplacementByteBuffer(paramInt);
        if (j > 0)
        {
          this.data.position(0);
          this.data.limit(j);
          localByteBuffer.put(this.data);
        }
        this.data = localByteBuffer;
      }
    }
  }
  
  public final void flip()
  {
    this.data.flip();
  }
  
  public final boolean isEncrypted()
  {
    return getFlag(NUM);
  }
  
  public final boolean isFlagsOnly()
  {
    if ((this.data == null) && (this.bufferReplacementMode == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface BufferReplacementMode {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */