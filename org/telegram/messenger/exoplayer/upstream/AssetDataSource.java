package org.telegram.messenger.exoplayer.upstream;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class AssetDataSource
  implements UriDataSource
{
  private final AssetManager assetManager;
  private long bytesRemaining;
  private InputStream inputStream;
  private final TransferListener listener;
  private boolean opened;
  private String uriString;
  
  public AssetDataSource(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AssetDataSource(Context paramContext, TransferListener paramTransferListener)
  {
    this.assetManager = paramContext.getAssets();
    this.listener = paramTransferListener;
  }
  
  public void close()
    throws AssetDataSource.AssetDataSourceException
  {
    this.uriString = null;
    if (this.inputStream != null) {}
    try
    {
      this.inputStream.close();
      return;
    }
    catch (IOException localIOException)
    {
      throw new AssetDataSourceException(localIOException);
    }
    finally
    {
      this.inputStream = null;
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
    throws AssetDataSource.AssetDataSourceException
  {
    for (;;)
    {
      String str2;
      try
      {
        this.uriString = paramDataSpec.uri.toString();
        str2 = paramDataSpec.uri.getPath();
        if (str2.startsWith("/android_asset/"))
        {
          str1 = str2.substring(15);
          this.uriString = paramDataSpec.uri.toString();
          this.inputStream = this.assetManager.open(str1, 1);
          if (this.inputStream.skip(paramDataSpec.position) >= paramDataSpec.position) {
            break;
          }
          throw new EOFException();
        }
      }
      catch (IOException paramDataSpec)
      {
        throw new AssetDataSourceException(paramDataSpec);
      }
      String str1 = str2;
      if (str2.startsWith("/")) {
        str1 = str2.substring(1);
      }
    }
    if (paramDataSpec.length != -1L) {
      this.bytesRemaining = paramDataSpec.length;
    }
    for (;;)
    {
      this.opened = true;
      if (this.listener != null) {
        this.listener.onTransferStart();
      }
      return this.bytesRemaining;
      this.bytesRemaining = this.inputStream.available();
      if (this.bytesRemaining == 2147483647L) {
        this.bytesRemaining = -1L;
      }
    }
  }
  
  /* Error */
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws AssetDataSource.AssetDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 116	org/telegram/messenger/exoplayer/upstream/AssetDataSource:bytesRemaining	J
    //   4: lconst_0
    //   5: lcmp
    //   6: ifne +7 -> 13
    //   9: iconst_m1
    //   10: istore_2
    //   11: iload_2
    //   12: ireturn
    //   13: aload_0
    //   14: getfield 116	org/telegram/messenger/exoplayer/upstream/AssetDataSource:bytesRemaining	J
    //   17: ldc2_w 113
    //   20: lcmp
    //   21: ifne +63 -> 84
    //   24: aload_0
    //   25: getfield 47	org/telegram/messenger/exoplayer/upstream/AssetDataSource:inputStream	Ljava/io/InputStream;
    //   28: aload_1
    //   29: iload_2
    //   30: iload_3
    //   31: invokevirtual 129	java/io/InputStream:read	([BII)I
    //   34: istore_3
    //   35: iload_3
    //   36: istore_2
    //   37: iload_3
    //   38: ifle -27 -> 11
    //   41: aload_0
    //   42: getfield 116	org/telegram/messenger/exoplayer/upstream/AssetDataSource:bytesRemaining	J
    //   45: ldc2_w 113
    //   48: lcmp
    //   49: ifeq +14 -> 63
    //   52: aload_0
    //   53: aload_0
    //   54: getfield 116	org/telegram/messenger/exoplayer/upstream/AssetDataSource:bytesRemaining	J
    //   57: iload_3
    //   58: i2l
    //   59: lsub
    //   60: putfield 116	org/telegram/messenger/exoplayer/upstream/AssetDataSource:bytesRemaining	J
    //   63: iload_3
    //   64: istore_2
    //   65: aload_0
    //   66: getfield 40	org/telegram/messenger/exoplayer/upstream/AssetDataSource:listener	Lorg/telegram/messenger/exoplayer/upstream/TransferListener;
    //   69: ifnull -58 -> 11
    //   72: aload_0
    //   73: getfield 40	org/telegram/messenger/exoplayer/upstream/AssetDataSource:listener	Lorg/telegram/messenger/exoplayer/upstream/TransferListener;
    //   76: iload_3
    //   77: invokeinterface 133 2 0
    //   82: iload_3
    //   83: ireturn
    //   84: aload_0
    //   85: getfield 116	org/telegram/messenger/exoplayer/upstream/AssetDataSource:bytesRemaining	J
    //   88: iload_3
    //   89: i2l
    //   90: invokestatic 139	java/lang/Math:min	(JJ)J
    //   93: lstore 4
    //   95: lload 4
    //   97: l2i
    //   98: istore_3
    //   99: goto -75 -> 24
    //   102: astore_1
    //   103: new 8	org/telegram/messenger/exoplayer/upstream/AssetDataSource$AssetDataSourceException
    //   106: dup
    //   107: aload_1
    //   108: invokespecial 61	org/telegram/messenger/exoplayer/upstream/AssetDataSource$AssetDataSourceException:<init>	(Ljava/io/IOException;)V
    //   111: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	112	0	this	AssetDataSource
    //   0	112	1	paramArrayOfByte	byte[]
    //   0	112	2	paramInt1	int
    //   0	112	3	paramInt2	int
    //   93	3	4	l	long
    // Exception table:
    //   from	to	target	type
    //   13	24	102	java/io/IOException
    //   24	35	102	java/io/IOException
    //   84	95	102	java/io/IOException
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/AssetDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */