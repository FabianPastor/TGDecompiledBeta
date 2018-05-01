package org.telegram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.PositionInputStream;

public class MP3Input
  extends PositionInputStream
{
  public MP3Input(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
  }
  
  public MP3Input(InputStream paramInputStream, long paramLong)
  {
    super(paramInputStream, paramLong);
  }
  
  public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    while (i < paramInt2)
    {
      int j = read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j > 0) {
        i += j;
      } else {
        throw new EOFException();
      }
    }
  }
  
  public void skipFully(long paramLong)
    throws IOException
  {
    long l1 = 0L;
    while (l1 < paramLong)
    {
      long l2 = skip(paramLong - l1);
      if (l2 > 0L) {
        l1 += l2;
      } else {
        throw new EOFException();
      }
    }
  }
  
  public String toString()
  {
    return "mp3[pos=" + getPosition() + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/mp3/MP3Input.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */