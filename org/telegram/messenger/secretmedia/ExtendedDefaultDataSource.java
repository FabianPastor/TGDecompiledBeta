package org.telegram.messenger.secretmedia;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.exoplayer2.upstream.AssetDataSource;
import org.telegram.messenger.exoplayer2.upstream.ContentDataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.DefaultHttpDataSource;
import org.telegram.messenger.exoplayer2.upstream.FileDataSource;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ExtendedDefaultDataSource
  implements DataSource
{
  private static final String SCHEME_ASSET = "asset";
  private static final String SCHEME_CONTENT = "content";
  private final DataSource assetDataSource;
  private final DataSource baseDataSource;
  private final DataSource contentDataSource;
  private DataSource dataSource;
  private final DataSource encryptedFileDataSource;
  private final DataSource fileDataSource;
  private TransferListener<? super DataSource> listener;
  
  public ExtendedDefaultDataSource(Context paramContext, TransferListener<? super DataSource> paramTransferListener, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this(paramContext, paramTransferListener, new DefaultHttpDataSource(paramString, null, paramTransferListener, paramInt1, paramInt2, paramBoolean, null));
  }
  
  public ExtendedDefaultDataSource(Context paramContext, TransferListener<? super DataSource> paramTransferListener, String paramString, boolean paramBoolean)
  {
    this(paramContext, paramTransferListener, paramString, 8000, 8000, paramBoolean);
  }
  
  public ExtendedDefaultDataSource(Context paramContext, TransferListener<? super DataSource> paramTransferListener, DataSource paramDataSource)
  {
    this.baseDataSource = ((DataSource)Assertions.checkNotNull(paramDataSource));
    this.fileDataSource = new FileDataSource(paramTransferListener);
    this.encryptedFileDataSource = new EncryptedFileDataSource(paramTransferListener);
    this.assetDataSource = new AssetDataSource(paramContext, paramTransferListener);
    this.contentDataSource = new ContentDataSource(paramContext, paramTransferListener);
    this.listener = paramTransferListener;
  }
  
  public void close()
    throws IOException
  {
    if (this.dataSource != null) {}
    try
    {
      this.dataSource.close();
      return;
    }
    finally
    {
      this.dataSource = null;
    }
  }
  
  public Uri getUri()
  {
    if (this.dataSource == null) {}
    for (Uri localUri = null;; localUri = this.dataSource.getUri()) {
      return localUri;
    }
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    boolean bool;
    String str;
    if (this.dataSource == null)
    {
      bool = true;
      Assertions.checkState(bool);
      str = paramDataSpec.uri.getScheme();
      if (!Util.isLocalFileUri(paramDataSpec.uri)) {
        break label107;
      }
      if (!paramDataSpec.uri.getPath().startsWith("/android_asset/")) {
        break label70;
      }
      this.dataSource = this.assetDataSource;
    }
    for (;;)
    {
      return this.dataSource.open(paramDataSpec);
      bool = false;
      break;
      label70:
      if (paramDataSpec.uri.getPath().endsWith(".enc"))
      {
        this.dataSource = this.encryptedFileDataSource;
      }
      else
      {
        this.dataSource = this.fileDataSource;
        continue;
        label107:
        if ("tg".equals(str)) {
          this.dataSource = FileLoader.getStreamLoadOperation(this.listener);
        } else if ("asset".equals(str)) {
          this.dataSource = this.assetDataSource;
        } else if ("content".equals(str)) {
          this.dataSource = this.contentDataSource;
        } else {
          this.dataSource = this.baseDataSource;
        }
      }
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return this.dataSource.read(paramArrayOfByte, paramInt1, paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/secretmedia/ExtendedDefaultDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */