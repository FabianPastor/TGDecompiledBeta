package com.googlecode.mp4parser;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract interface DataSource
  extends Closeable
{
  public abstract void close()
    throws IOException;
  
  public abstract ByteBuffer map(long paramLong1, long paramLong2)
    throws IOException;
  
  public abstract long position()
    throws IOException;
  
  public abstract void position(long paramLong)
    throws IOException;
  
  public abstract int read(ByteBuffer paramByteBuffer)
    throws IOException;
  
  public abstract long size()
    throws IOException;
  
  public abstract long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
    throws IOException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/DataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */