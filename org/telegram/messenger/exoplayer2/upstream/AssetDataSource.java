package org.telegram.messenger.exoplayer2.upstream;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class AssetDataSource
  implements DataSource
{
  private final AssetManager assetManager;
  private long bytesRemaining;
  private InputStream inputStream;
  private final TransferListener<? super AssetDataSource> listener;
  private boolean opened;
  private Uri uri;
  
  public AssetDataSource(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AssetDataSource(Context paramContext, TransferListener<? super AssetDataSource> paramTransferListener)
  {
    this.assetManager = paramContext.getAssets();
    this.listener = paramTransferListener;
  }
  
  public void close()
    throws AssetDataSource.AssetDataSourceException
  {
    this.uri = null;
    try
    {
      if (this.inputStream != null) {
        this.inputStream.close();
      }
      return;
    }
    catch (IOException localIOException)
    {
      AssetDataSourceException localAssetDataSourceException = new org/telegram/messenger/exoplayer2/upstream/AssetDataSource$AssetDataSourceException;
      localAssetDataSourceException.<init>(localIOException);
      throw localAssetDataSourceException;
    }
    finally
    {
      this.inputStream = null;
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
    throws AssetDataSource.AssetDataSourceException
  {
    for (;;)
    {
      String str1;
      try
      {
        this.uri = paramDataSpec.uri;
        str1 = this.uri.getPath();
        if (str1.startsWith("/android_asset/"))
        {
          str2 = str1.substring(15);
          this.inputStream = this.assetManager.open(str2, 1);
          if (this.inputStream.skip(paramDataSpec.position) >= paramDataSpec.position) {
            break;
          }
          paramDataSpec = new java/io/EOFException;
          paramDataSpec.<init>();
          throw paramDataSpec;
        }
      }
      catch (IOException paramDataSpec)
      {
        throw new AssetDataSourceException(paramDataSpec);
      }
      String str2 = str1;
      if (str1.startsWith("/")) {
        str2 = str1.substring(1);
      }
    }
    if (paramDataSpec.length != -1L) {
      this.bytesRemaining = paramDataSpec.length;
    }
    for (;;)
    {
      this.opened = true;
      if (this.listener != null) {
        this.listener.onTransferStart(this, paramDataSpec);
      }
      return this.bytesRemaining;
      this.bytesRemaining = this.inputStream.available();
      if (this.bytesRemaining == 2147483647L) {
        this.bytesRemaining = -1L;
      }
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws AssetDataSource.AssetDataSourceException
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
      }
      else
      {
        try
        {
          if (this.bytesRemaining == -1L) {}
          for (;;)
          {
            paramInt2 = this.inputStream.read(paramArrayOfByte, paramInt1, paramInt2);
            if (paramInt2 != -1) {
              break label108;
            }
            if (this.bytesRemaining == -1L) {
              break;
            }
            throw new AssetDataSourceException(new EOFException());
            long l = Math.min(this.bytesRemaining, paramInt2);
            paramInt2 = (int)l;
          }
          paramInt1 = -1;
        }
        catch (IOException paramArrayOfByte)
        {
          throw new AssetDataSourceException(paramArrayOfByte);
        }
        continue;
        label108:
        if (this.bytesRemaining != -1L) {
          this.bytesRemaining -= paramInt2;
        }
        paramInt1 = paramInt2;
        if (this.listener != null)
        {
          this.listener.onBytesTransferred(this, paramInt2);
          paramInt1 = paramInt2;
        }
      }
    }
  }
  
  public static final class AssetDataSourceException
    extends IOException
  {
    public AssetDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/AssetDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */