package org.telegram.messenger.audioinfo.util;

import java.io.IOException;
import java.io.InputStream;

public class RangeInputStream
  extends PositionInputStream
{
  private final long endPosition;
  
  public RangeInputStream(InputStream paramInputStream, long paramLong1, long paramLong2)
    throws IOException
  {
    super(paramInputStream, paramLong1);
    this.endPosition = (paramLong1 + paramLong2);
  }
  
  public long getRemainingLength()
  {
    return this.endPosition - getPosition();
  }
  
  public int read()
    throws IOException
  {
    if (getPosition() == this.endPosition) {}
    for (int i = -1;; i = super.read()) {
      return i;
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt2;
    if (getPosition() + paramInt2 > this.endPosition)
    {
      paramInt2 = (int)(this.endPosition - getPosition());
      i = paramInt2;
      if (paramInt2 != 0) {}
    }
    for (paramInt1 = -1;; paramInt1 = super.read(paramArrayOfByte, paramInt1, i)) {
      return paramInt1;
    }
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    long l = paramLong;
    if (getPosition() + paramLong > this.endPosition) {
      l = (int)(this.endPosition - getPosition());
    }
    return super.skip(l);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/util/RangeInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */