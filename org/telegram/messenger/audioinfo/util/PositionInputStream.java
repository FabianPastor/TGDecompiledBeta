package org.telegram.messenger.audioinfo.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PositionInputStream
  extends FilterInputStream
{
  private long position;
  private long positionMark;
  
  public PositionInputStream(InputStream paramInputStream)
  {
    this(paramInputStream, 0L);
  }
  
  public PositionInputStream(InputStream paramInputStream, long paramLong)
  {
    super(paramInputStream);
    this.position = paramLong;
  }
  
  public long getPosition()
  {
    return this.position;
  }
  
  public void mark(int paramInt)
  {
    try
    {
      this.positionMark = this.position;
      super.mark(paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int read()
    throws IOException
  {
    int i = super.read();
    if (i >= 0) {
      this.position += 1L;
    }
    return i;
  }
  
  public final int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    long l = this.position;
    paramInt1 = super.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt1 > 0) {
      this.position = (paramInt1 + l);
    }
    return paramInt1;
  }
  
  public void reset()
    throws IOException
  {
    try
    {
      super.reset();
      this.position = this.positionMark;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    long l = this.position;
    paramLong = super.skip(paramLong);
    this.position = (l + paramLong);
    return paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/util/PositionInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */