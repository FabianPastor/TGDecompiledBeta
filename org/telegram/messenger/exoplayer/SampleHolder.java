package org.telegram.messenger.exoplayer;

import java.nio.ByteBuffer;

public final class SampleHolder
{
  public static final int BUFFER_REPLACEMENT_MODE_DIRECT = 2;
  public static final int BUFFER_REPLACEMENT_MODE_DISABLED = 0;
  public static final int BUFFER_REPLACEMENT_MODE_NORMAL = 1;
  private final int bufferReplacementMode;
  public final CryptoInfo cryptoInfo = new CryptoInfo();
  public ByteBuffer data;
  public int flags;
  public int size;
  public long timeUs;
  
  public SampleHolder(int paramInt)
  {
    this.bufferReplacementMode = paramInt;
  }
  
  private ByteBuffer createReplacementBuffer(int paramInt)
  {
    if (this.bufferReplacementMode == 1) {
      return ByteBuffer.allocate(paramInt);
    }
    if (this.bufferReplacementMode == 2) {
      return ByteBuffer.allocateDirect(paramInt);
    }
    if (this.data == null) {}
    for (int i = 0;; i = this.data.capacity()) {
      throw new IllegalStateException("Buffer too small (" + i + " < " + paramInt + ")");
    }
  }
  
  public void clearData()
  {
    if (this.data != null) {
      this.data.clear();
    }
  }
  
  public void ensureSpaceForWrite(int paramInt)
    throws IllegalStateException
  {
    if (this.data == null) {
      this.data = createReplacementBuffer(paramInt);
    }
    int i;
    int j;
    do
    {
      return;
      i = this.data.capacity();
      j = this.data.position();
      paramInt = j + paramInt;
    } while (i >= paramInt);
    ByteBuffer localByteBuffer = createReplacementBuffer(paramInt);
    if (j > 0)
    {
      this.data.position(0);
      this.data.limit(j);
      localByteBuffer.put(this.data);
    }
    this.data = localByteBuffer;
  }
  
  public boolean isDecodeOnly()
  {
    return (this.flags & 0x8000000) != 0;
  }
  
  public boolean isEncrypted()
  {
    return (this.flags & 0x2) != 0;
  }
  
  public boolean isSyncFrame()
  {
    return (this.flags & 0x1) != 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/SampleHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */