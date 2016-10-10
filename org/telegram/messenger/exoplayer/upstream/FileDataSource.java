package org.telegram.messenger.exoplayer.upstream;

import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FileDataSource
  implements UriDataSource
{
  private long bytesRemaining;
  private RandomAccessFile file;
  private final TransferListener listener;
  private boolean opened;
  private String uriString;
  
  public FileDataSource()
  {
    this(null);
  }
  
  public FileDataSource(TransferListener paramTransferListener)
  {
    this.listener = paramTransferListener;
  }
  
  public void close()
    throws FileDataSource.FileDataSourceException
  {
    this.uriString = null;
    if (this.file != null) {}
    try
    {
      this.file.close();
      return;
    }
    catch (IOException localIOException)
    {
      throw new FileDataSourceException(localIOException);
    }
    finally
    {
      this.file = null;
      if (this.opened)
      {
        this.opened = false;
        if (this.listener != null) {
          this.listener.onTransferEnd();
        }
      }
    }
  }
  
  public String getUri()
  {
    return this.uriString;
  }
  
  public long open(DataSpec paramDataSpec)
    throws FileDataSource.FileDataSourceException
  {
    for (;;)
    {
      try
      {
        this.uriString = paramDataSpec.uri.toString();
        this.file = new RandomAccessFile(paramDataSpec.uri.getPath(), "r");
        this.file.seek(paramDataSpec.position);
        if (paramDataSpec.length == -1L)
        {
          l = this.file.length() - paramDataSpec.position;
          this.bytesRemaining = l;
          if (this.bytesRemaining >= 0L) {
            break;
          }
          throw new EOFException();
        }
      }
      catch (IOException paramDataSpec)
      {
        throw new FileDataSourceException(paramDataSpec);
      }
      long l = paramDataSpec.length;
    }
    this.opened = true;
    if (this.listener != null) {
      this.listener.onTransferStart();
    }
    return this.bytesRemaining;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws FileDataSource.FileDataSourceException
  {
    if (this.bytesRemaining == 0L) {
      paramInt1 = -1;
    }
    for (;;)
    {
      return paramInt1;
      try
      {
        paramInt2 = this.file.read(paramArrayOfByte, paramInt1, (int)Math.min(this.bytesRemaining, paramInt2));
        paramInt1 = paramInt2;
        if (paramInt2 <= 0) {
          continue;
        }
        this.bytesRemaining -= paramInt2;
        paramInt1 = paramInt2;
        if (this.listener == null) {
          continue;
        }
        this.listener.onBytesTransferred(paramInt2);
        return paramInt2;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new FileDataSourceException(paramArrayOfByte);
      }
    }
  }
  
  public static class FileDataSourceException
    extends IOException
  {
    public FileDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/FileDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */