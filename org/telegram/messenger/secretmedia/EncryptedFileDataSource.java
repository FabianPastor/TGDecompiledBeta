package org.telegram.messenger.secretmedia;

import android.net.Uri;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;

public final class EncryptedFileDataSource
  implements DataSource
{
  private long bytesRemaining;
  private RandomAccessFile file;
  private int fileOffset;
  private byte[] iv = new byte[16];
  private byte[] key = new byte[32];
  private final TransferListener<? super EncryptedFileDataSource> listener;
  private boolean opened;
  private Uri uri;
  
  public EncryptedFileDataSource()
  {
    this(null);
  }
  
  public EncryptedFileDataSource(TransferListener<? super EncryptedFileDataSource> paramTransferListener)
  {
    this.listener = paramTransferListener;
  }
  
  public void close()
    throws EncryptedFileDataSource.EncryptedFileDataSourceException
  {
    this.uri = null;
    this.fileOffset = 0;
    try
    {
      if (this.file != null) {
        this.file.close();
      }
      return;
    }
    catch (IOException localIOException)
    {
      EncryptedFileDataSourceException localEncryptedFileDataSourceException = new org/telegram/messenger/secretmedia/EncryptedFileDataSource$EncryptedFileDataSourceException;
      localEncryptedFileDataSourceException.<init>(localIOException);
      throw localEncryptedFileDataSourceException;
    }
    finally
    {
      this.file = null;
      if (this.opened)
      {
        this.opened = false;
        if (this.listener != null) {
          this.listener.onTransferEnd(this);
        }
      }
    }
  }
  
  public Uri getUri()
  {
    return this.uri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws EncryptedFileDataSource.EncryptedFileDataSourceException
  {
    for (;;)
    {
      try
      {
        this.uri = paramDataSpec.uri;
        File localFile1 = new java/io/File;
        localFile1.<init>(paramDataSpec.uri.getPath());
        String str = localFile1.getName();
        Object localObject1 = new java/io/File;
        File localFile2 = FileLoader.getInternalCacheDir();
        Object localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((File)localObject1).<init>(localFile2, str + ".key");
        localObject2 = new java/io/RandomAccessFile;
        ((RandomAccessFile)localObject2).<init>((File)localObject1, "r");
        ((RandomAccessFile)localObject2).read(this.key);
        ((RandomAccessFile)localObject2).read(this.iv);
        ((RandomAccessFile)localObject2).close();
        localObject1 = new java/io/RandomAccessFile;
        ((RandomAccessFile)localObject1).<init>(localFile1, "r");
        this.file = ((RandomAccessFile)localObject1);
        this.file.seek(paramDataSpec.position);
        this.fileOffset = ((int)paramDataSpec.position);
        if (paramDataSpec.length == -1L)
        {
          l = this.file.length() - paramDataSpec.position;
          this.bytesRemaining = l;
          if (this.bytesRemaining >= 0L) {
            break;
          }
          paramDataSpec = new java/io/EOFException;
          paramDataSpec.<init>();
          throw paramDataSpec;
        }
      }
      catch (IOException paramDataSpec)
      {
        throw new EncryptedFileDataSourceException(paramDataSpec);
      }
      long l = paramDataSpec.length;
    }
    this.opened = true;
    if (this.listener != null) {
      this.listener.onTransferStart(this, paramDataSpec);
    }
    return this.bytesRemaining;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws EncryptedFileDataSource.EncryptedFileDataSourceException
  {
    if (paramInt2 == 0) {
      paramInt1 = 0;
    }
    for (;;)
    {
      return paramInt1;
      if (this.bytesRemaining == 0L)
      {
        paramInt1 = -1;
        continue;
      }
      try
      {
        paramInt2 = this.file.read(paramArrayOfByte, paramInt1, (int)Math.min(this.bytesRemaining, paramInt2));
        Utilities.aesCtrDecryptionByteArray(paramArrayOfByte, this.key, this.iv, paramInt1, paramInt2, this.fileOffset);
        this.fileOffset += paramInt2;
        paramInt1 = paramInt2;
        if (paramInt2 <= 0) {
          continue;
        }
        this.bytesRemaining -= paramInt2;
        paramInt1 = paramInt2;
        if (this.listener == null) {
          continue;
        }
        this.listener.onBytesTransferred(this, paramInt2);
        paramInt1 = paramInt2;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new EncryptedFileDataSourceException(paramArrayOfByte);
      }
    }
  }
  
  public static class EncryptedFileDataSourceException
    extends IOException
  {
    public EncryptedFileDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/secretmedia/EncryptedFileDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */