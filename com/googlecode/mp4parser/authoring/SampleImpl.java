package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class SampleImpl
  implements Sample
{
  private ByteBuffer[] data;
  private final long offset;
  private final Container parent;
  private final long size;
  
  public SampleImpl(long paramLong1, long paramLong2, Container paramContainer)
  {
    this.offset = paramLong1;
    this.size = paramLong2;
    this.data = null;
    this.parent = paramContainer;
  }
  
  public SampleImpl(long paramLong1, long paramLong2, ByteBuffer paramByteBuffer)
  {
    this.offset = paramLong1;
    this.size = paramLong2;
    this.data = new ByteBuffer[] { paramByteBuffer };
    this.parent = null;
  }
  
  public SampleImpl(ByteBuffer paramByteBuffer)
  {
    this.offset = -1L;
    this.size = paramByteBuffer.limit();
    this.data = new ByteBuffer[] { paramByteBuffer };
    this.parent = null;
  }
  
  public SampleImpl(ByteBuffer[] paramArrayOfByteBuffer)
  {
    this.offset = -1L;
    int j = 0;
    int k = paramArrayOfByteBuffer.length;
    int i = 0;
    for (;;)
    {
      if (i >= k)
      {
        this.size = j;
        this.data = paramArrayOfByteBuffer;
        this.parent = null;
        return;
      }
      j += paramArrayOfByteBuffer[i].remaining();
      i += 1;
    }
  }
  
  public ByteBuffer asByteBuffer()
  {
    ensureData();
    ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[CastUtils.l2i(this.size)]);
    ByteBuffer[] arrayOfByteBuffer = this.data;
    int j = arrayOfByteBuffer.length;
    int i = 0;
    for (;;)
    {
      if (i >= j)
      {
        localByteBuffer.rewind();
        return localByteBuffer;
      }
      localByteBuffer.put(arrayOfByteBuffer[i].duplicate());
      i += 1;
    }
  }
  
  protected void ensureData()
  {
    if (this.data != null) {
      return;
    }
    if (this.parent == null) {
      throw new RuntimeException("Missing parent container, can't read sample " + this);
    }
    try
    {
      this.data = new ByteBuffer[] { this.parent.getByteBuffer(this.offset, this.size) };
      return;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException("couldn't read sample " + this, localIOException);
    }
  }
  
  public long getSize()
  {
    return this.size;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SampleImpl");
    localStringBuilder.append("{offset=").append(this.offset);
    localStringBuilder.append("{size=").append(this.size);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void writeTo(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    ensureData();
    ByteBuffer[] arrayOfByteBuffer = this.data;
    int j = arrayOfByteBuffer.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      paramWritableByteChannel.write(arrayOfByteBuffer[i].duplicate());
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/SampleImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */