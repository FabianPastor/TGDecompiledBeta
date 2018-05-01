package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class TeeDataSource
  implements DataSource
{
  private final DataSink dataSink;
  private final DataSource upstream;
  
  public TeeDataSource(DataSource paramDataSource, DataSink paramDataSink)
  {
    this.upstream = ((DataSource)Assertions.checkNotNull(paramDataSource));
    this.dataSink = ((DataSink)Assertions.checkNotNull(paramDataSink));
  }
  
  public void close()
    throws IOException
  {
    try
    {
      this.upstream.close();
      return;
    }
    finally
    {
      this.dataSink.close();
    }
  }
  
  public Uri getUri()
  {
    return this.upstream.getUri();
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    long l = this.upstream.open(paramDataSpec);
    DataSpec localDataSpec = paramDataSpec;
    if (paramDataSpec.length == -1L)
    {
      localDataSpec = paramDataSpec;
      if (l != -1L) {
        localDataSpec = new DataSpec(paramDataSpec.uri, paramDataSpec.absoluteStreamPosition, paramDataSpec.position, l, paramDataSpec.key, paramDataSpec.flags);
      }
    }
    this.dataSink.open(localDataSpec);
    return l;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt2 = this.upstream.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt2 > 0) {
      this.dataSink.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    return paramInt2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/TeeDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */