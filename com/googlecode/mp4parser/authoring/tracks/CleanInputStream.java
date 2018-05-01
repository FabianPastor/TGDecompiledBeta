package com.googlecode.mp4parser.authoring.tracks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CleanInputStream
  extends FilterInputStream
{
  int prev = -1;
  int prevprev = -1;
  
  public CleanInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public int read()
    throws IOException
  {
    int j = super.read();
    int i = j;
    if (j == 3)
    {
      i = j;
      if (this.prevprev == 0)
      {
        i = j;
        if (this.prev == 0)
        {
          this.prevprev = -1;
          this.prev = -1;
          i = super.read();
        }
      }
    }
    this.prevprev = this.prev;
    this.prev = i;
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    int j;
    if (paramInt2 == 0) {
      j = 0;
    }
    int i;
    for (;;)
    {
      return j;
      i = read();
      if (i == -1) {
        return -1;
      }
      paramArrayOfByte[paramInt1] = ((byte)i);
      i = 1;
      j = i;
      if (i < paramInt2) {
        try
        {
          int k = read();
          j = i;
          if (k != -1)
          {
            paramArrayOfByte[(paramInt1 + i)] = ((byte)k);
            i += 1;
          }
        }
        catch (IOException paramArrayOfByte) {}
      }
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/CleanInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */