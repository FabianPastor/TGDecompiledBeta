package org.telegram.messenger.exoplayer2.upstream;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class DataSourceInputStream
  extends InputStream
{
  private boolean closed = false;
  private final DataSource dataSource;
  private final DataSpec dataSpec;
  private boolean opened = false;
  private final byte[] singleByteArray;
  private long totalBytesRead;
  
  public DataSourceInputStream(DataSource paramDataSource, DataSpec paramDataSpec)
  {
    this.dataSource = paramDataSource;
    this.dataSpec = paramDataSpec;
    this.singleByteArray = new byte[1];
  }
  
  private void checkOpened()
    throws IOException
  {
    if (!this.opened)
    {
      this.dataSource.open(this.dataSpec);
      this.opened = true;
    }
  }
  
  public long bytesRead()
  {
    return this.totalBytesRead;
  }
  
  public void close()
    throws IOException
  {
    if (!this.closed)
    {
      this.dataSource.close();
      this.closed = true;
    }
  }
  
  public void open()
    throws IOException
  {
    checkOpened();
  }
  
  public int read()
    throws IOException
  {
    int i = -1;
    if (read(this.singleByteArray) == -1) {}
    for (;;)
    {
      return i;
      i = this.singleByteArray[0] & 0xFF;
    }
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    boolean bool;
    if (!this.closed)
    {
      bool = true;
      Assertions.checkState(bool);
      checkOpened();
      paramInt1 = this.dataSource.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 != -1) {
        break label47;
      }
      paramInt1 = -1;
    }
    for (;;)
    {
      return paramInt1;
      bool = false;
      break;
      label47:
      this.totalBytesRead += paramInt1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/DataSourceInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */