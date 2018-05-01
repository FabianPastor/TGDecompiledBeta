package com.googlecode.mp4parser;

import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class MemoryDataSourceImpl
  implements DataSource
{
  ByteBuffer data;
  
  public MemoryDataSourceImpl(ByteBuffer paramByteBuffer)
  {
    this.data = paramByteBuffer;
  }
  
  public MemoryDataSourceImpl(byte[] paramArrayOfByte)
  {
    this.data = ByteBuffer.wrap(paramArrayOfByte);
  }
  
  public void close()
    throws IOException
  {}
  
  public ByteBuffer map(long paramLong1, long paramLong2)
    throws IOException
  {
    int i = this.data.position();
    this.data.position(CastUtils.l2i(paramLong1));
    ByteBuffer localByteBuffer = this.data.slice();
    localByteBuffer.limit(CastUtils.l2i(paramLong2));
    this.data.position(i);
    return localByteBuffer;
  }
  
  public long position()
    throws IOException
  {
    return this.data.position();
  }
  
  public void position(long paramLong)
    throws IOException
  {
    this.data.position(CastUtils.l2i(paramLong));
  }
  
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if ((this.data.remaining() == 0) && (paramByteBuffer.remaining() != 0)) {
      return -1;
    }
    int i = Math.min(paramByteBuffer.remaining(), this.data.remaining());
    if (paramByteBuffer.hasArray())
    {
      paramByteBuffer.put(this.data.array(), this.data.position(), i);
      this.data.position(this.data.position() + i);
      return i;
    }
    byte[] arrayOfByte = new byte[i];
    this.data.get(arrayOfByte);
    paramByteBuffer.put(arrayOfByte);
    return i;
  }
  
  public long size()
    throws IOException
  {
    return this.data.capacity();
  }
  
  public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    return paramWritableByteChannel.write((ByteBuffer)((ByteBuffer)this.data.position(CastUtils.l2i(paramLong1))).slice().limit(CastUtils.l2i(paramLong2)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/MemoryDataSourceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */