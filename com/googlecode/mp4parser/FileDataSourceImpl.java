package com.googlecode.mp4parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;

public class FileDataSourceImpl
  implements DataSource
{
  FileChannel fc;
  String filename;
  
  public FileDataSourceImpl(File paramFile)
    throws FileNotFoundException
  {
    this.fc = new FileInputStream(paramFile).getChannel();
    this.filename = paramFile.getName();
  }
  
  public FileDataSourceImpl(FileChannel paramFileChannel)
  {
    this.fc = paramFileChannel;
    this.filename = "unknown";
  }
  
  public void close()
    throws IOException
  {
    this.fc.close();
  }
  
  public ByteBuffer map(long paramLong1, long paramLong2)
    throws IOException
  {
    return this.fc.map(FileChannel.MapMode.READ_ONLY, paramLong1, paramLong2);
  }
  
  public long position()
    throws IOException
  {
    return this.fc.position();
  }
  
  public void position(long paramLong)
    throws IOException
  {
    this.fc.position(paramLong);
  }
  
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    return this.fc.read(paramByteBuffer);
  }
  
  public long size()
    throws IOException
  {
    return this.fc.size();
  }
  
  public String toString()
  {
    return this.filename;
  }
  
  public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    return this.fc.transferTo(paramLong1, paramLong2, paramWritableByteChannel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/FileDataSourceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */