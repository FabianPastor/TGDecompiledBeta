package org.telegram.messenger.exoplayer2.upstream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class RawResourceDataSource
  implements DataSource
{
  public static final String RAW_RESOURCE_SCHEME = "rawresource";
  private AssetFileDescriptor assetFileDescriptor;
  private long bytesRemaining;
  private InputStream inputStream;
  private final TransferListener<? super RawResourceDataSource> listener;
  private boolean opened;
  private final Resources resources;
  private Uri uri;
  
  public RawResourceDataSource(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RawResourceDataSource(Context paramContext, TransferListener<? super RawResourceDataSource> paramTransferListener)
  {
    this.resources = paramContext.getResources();
    this.listener = paramTransferListener;
  }
  
  public static Uri buildRawResourceUri(int paramInt)
  {
    return Uri.parse("rawresource:///" + paramInt);
  }
  
  /* Error */
  public void close()
    throws RawResourceDataSource.RawResourceDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 78	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:uri	Landroid/net/Uri;
    //   5: aload_0
    //   6: getfield 80	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:inputStream	Ljava/io/InputStream;
    //   9: ifnull +10 -> 19
    //   12: aload_0
    //   13: getfield 80	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:inputStream	Ljava/io/InputStream;
    //   16: invokevirtual 84	java/io/InputStream:close	()V
    //   19: aload_0
    //   20: aconst_null
    //   21: putfield 80	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:inputStream	Ljava/io/InputStream;
    //   24: aload_0
    //   25: getfield 86	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   28: ifnull +10 -> 38
    //   31: aload_0
    //   32: getfield 86	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   35: invokevirtual 89	android/content/res/AssetFileDescriptor:close	()V
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 86	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   43: aload_0
    //   44: getfield 91	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   47: ifeq +25 -> 72
    //   50: aload_0
    //   51: iconst_0
    //   52: putfield 91	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   55: aload_0
    //   56: getfield 47	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   59: ifnull +13 -> 72
    //   62: aload_0
    //   63: getfield 47	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   66: aload_0
    //   67: invokeinterface 97 2 0
    //   72: return
    //   73: astore_1
    //   74: new 8	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException
    //   77: astore_2
    //   78: aload_2
    //   79: aload_1
    //   80: invokespecial 100	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException:<init>	(Ljava/io/IOException;)V
    //   83: aload_2
    //   84: athrow
    //   85: astore_1
    //   86: aload_0
    //   87: aconst_null
    //   88: putfield 86	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   91: aload_0
    //   92: getfield 91	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   95: ifeq +25 -> 120
    //   98: aload_0
    //   99: iconst_0
    //   100: putfield 91	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   103: aload_0
    //   104: getfield 47	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   107: ifnull +13 -> 120
    //   110: aload_0
    //   111: getfield 47	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   114: aload_0
    //   115: invokeinterface 97 2 0
    //   120: aload_1
    //   121: athrow
    //   122: astore_2
    //   123: new 8	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException
    //   126: astore_1
    //   127: aload_1
    //   128: aload_2
    //   129: invokespecial 100	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException:<init>	(Ljava/io/IOException;)V
    //   132: aload_1
    //   133: athrow
    //   134: astore_1
    //   135: aload_0
    //   136: aconst_null
    //   137: putfield 80	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:inputStream	Ljava/io/InputStream;
    //   140: aload_0
    //   141: getfield 86	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   144: ifnull +10 -> 154
    //   147: aload_0
    //   148: getfield 86	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   151: invokevirtual 89	android/content/res/AssetFileDescriptor:close	()V
    //   154: aload_0
    //   155: aconst_null
    //   156: putfield 86	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   159: aload_0
    //   160: getfield 91	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   163: ifeq +25 -> 188
    //   166: aload_0
    //   167: iconst_0
    //   168: putfield 91	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   171: aload_0
    //   172: getfield 47	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   175: ifnull +13 -> 188
    //   178: aload_0
    //   179: getfield 47	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   182: aload_0
    //   183: invokeinterface 97 2 0
    //   188: aload_1
    //   189: athrow
    //   190: astore_2
    //   191: new 8	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException
    //   194: astore_1
    //   195: aload_1
    //   196: aload_2
    //   197: invokespecial 100	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException:<init>	(Ljava/io/IOException;)V
    //   200: aload_1
    //   201: athrow
    //   202: astore_1
    //   203: aload_0
    //   204: aconst_null
    //   205: putfield 86	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   208: aload_0
    //   209: getfield 91	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   212: ifeq +25 -> 237
    //   215: aload_0
    //   216: iconst_0
    //   217: putfield 91	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   220: aload_0
    //   221: getfield 47	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   224: ifnull +13 -> 237
    //   227: aload_0
    //   228: getfield 47	org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   231: aload_0
    //   232: invokeinterface 97 2 0
    //   237: aload_1
    //   238: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	239	0	this	RawResourceDataSource
    //   73	7	1	localIOException1	IOException
    //   85	36	1	localObject1	Object
    //   126	7	1	localRawResourceDataSourceException1	RawResourceDataSourceException
    //   134	55	1	localObject2	Object
    //   194	7	1	localRawResourceDataSourceException2	RawResourceDataSourceException
    //   202	36	1	localObject3	Object
    //   77	7	2	localRawResourceDataSourceException3	RawResourceDataSourceException
    //   122	7	2	localIOException2	IOException
    //   190	7	2	localIOException3	IOException
    // Exception table:
    //   from	to	target	type
    //   24	38	73	java/io/IOException
    //   24	38	85	finally
    //   74	85	85	finally
    //   5	19	122	java/io/IOException
    //   5	19	134	finally
    //   123	134	134	finally
    //   140	154	190	java/io/IOException
    //   140	154	202	finally
    //   191	202	202	finally
  }
  
  public Uri getUri()
  {
    return this.uri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws RawResourceDataSource.RawResourceDataSourceException
  {
    long l1 = -1L;
    try
    {
      this.uri = paramDataSpec.uri;
      if (!TextUtils.equals("rawresource", this.uri.getScheme()))
      {
        paramDataSpec = new org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException;
        paramDataSpec.<init>("URI must use scheme rawresource");
        throw paramDataSpec;
      }
    }
    catch (IOException paramDataSpec)
    {
      throw new RawResourceDataSourceException(paramDataSpec);
    }
    try
    {
      int i = Integer.parseInt(this.uri.getLastPathSegment());
      this.assetFileDescriptor = this.resources.openRawResourceFd(i);
      FileInputStream localFileInputStream = new java/io/FileInputStream;
      localFileInputStream.<init>(this.assetFileDescriptor.getFileDescriptor());
      this.inputStream = localFileInputStream;
      this.inputStream.skip(this.assetFileDescriptor.getStartOffset());
      if (this.inputStream.skip(paramDataSpec.position) < paramDataSpec.position)
      {
        paramDataSpec = new java/io/EOFException;
        paramDataSpec.<init>();
        throw paramDataSpec;
      }
    }
    catch (NumberFormatException paramDataSpec)
    {
      paramDataSpec = new org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException;
      paramDataSpec.<init>("Resource identifier must be an integer.");
      throw paramDataSpec;
    }
    if (paramDataSpec.length != -1L)
    {
      this.bytesRemaining = paramDataSpec.length;
      this.opened = true;
      if (this.listener != null) {
        this.listener.onTransferStart(this, paramDataSpec);
      }
      return this.bytesRemaining;
    }
    long l2 = this.assetFileDescriptor.getLength();
    if (l2 == -1L) {}
    for (;;)
    {
      this.bytesRemaining = l1;
      break;
      l1 = paramDataSpec.position;
      l1 = l2 - l1;
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws RawResourceDataSource.RawResourceDataSourceException
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
            throw new RawResourceDataSourceException(new EOFException());
            long l = Math.min(this.bytesRemaining, paramInt2);
            paramInt2 = (int)l;
          }
          paramInt1 = -1;
        }
        catch (IOException paramArrayOfByte)
        {
          throw new RawResourceDataSourceException(paramArrayOfByte);
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
  
  public static class RawResourceDataSourceException
    extends IOException
  {
    public RawResourceDataSourceException(IOException paramIOException)
    {
      super();
    }
    
    public RawResourceDataSourceException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/RawResourceDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */