package org.telegram.messenger.exoplayer2.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class ReusableBufferedOutputStream
  extends BufferedOutputStream
{
  private boolean closed;
  
  public ReusableBufferedOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }
  
  public ReusableBufferedOutputStream(OutputStream paramOutputStream, int paramInt)
  {
    super(paramOutputStream, paramInt);
  }
  
  public void close()
    throws IOException
  {
    this.closed = true;
    Object localObject1 = null;
    try
    {
      flush();
      try
      {
        this.out.close();
        localObject2 = localObject1;
      }
      catch (Throwable localThrowable2)
      {
        for (;;)
        {
          Object localObject2 = localThrowable1;
          if (localThrowable1 == null) {
            localObject2 = localThrowable2;
          }
        }
      }
      if (localObject2 != null) {
        Util.sneakyThrow((Throwable)localObject2);
      }
      return;
    }
    catch (Throwable localThrowable1)
    {
      for (;;) {}
    }
  }
  
  public void reset(OutputStream paramOutputStream)
  {
    Assertions.checkState(this.closed);
    this.out = paramOutputStream;
    this.count = 0;
    this.closed = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */