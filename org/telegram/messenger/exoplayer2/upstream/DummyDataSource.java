package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;

public final class DummyDataSource
  implements DataSource
{
  public static final DataSource.Factory FACTORY = new DataSource.Factory()
  {
    public DataSource createDataSource()
    {
      return new DummyDataSource(null);
    }
  };
  public static final DummyDataSource INSTANCE = new DummyDataSource();
  
  public void close()
    throws IOException
  {}
  
  public Uri getUri()
  {
    return null;
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    throw new IOException("Dummy source");
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/DummyDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */