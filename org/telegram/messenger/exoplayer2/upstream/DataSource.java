package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;

public abstract interface DataSource
{
  public abstract void close()
    throws IOException;
  
  public abstract Uri getUri();
  
  public abstract long open(DataSpec paramDataSpec)
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public static abstract interface Factory
  {
    public abstract DataSource createDataSource();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/DataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */