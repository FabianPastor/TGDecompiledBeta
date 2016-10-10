package com.googlecode.mp4parser.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ByteBufferByteChannel
  implements ByteChannel
{
  ByteBuffer byteBuffer;
  
  public ByteBufferByteChannel(ByteBuffer paramByteBuffer)
  {
    this.byteBuffer = paramByteBuffer;
  }
  
  public void close()
    throws IOException
  {}
  
  public boolean isOpen()
  {
    return true;
  }
  
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    int i = paramByteBuffer.remaining();
    if (this.byteBuffer.remaining() <= 0) {
      return -1;
    }
    paramByteBuffer.put((ByteBuffer)this.byteBuffer.duplicate().limit(this.byteBuffer.position() + paramByteBuffer.remaining()));
    this.byteBuffer.position(this.byteBuffer.position() + i);
    return i;
  }
  
  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    int i = paramByteBuffer.remaining();
    this.byteBuffer.put(paramByteBuffer);
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/ByteBufferByteChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */