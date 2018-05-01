package org.telegram.messenger.exoplayer2.upstream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

public final class ContentDataSource
  implements DataSource
{
  private AssetFileDescriptor assetFileDescriptor;
  private long bytesRemaining;
  private FileInputStream inputStream;
  private final TransferListener<? super ContentDataSource> listener;
  private boolean opened;
  private final ContentResolver resolver;
  private Uri uri;
  
  public ContentDataSource(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ContentDataSource(Context paramContext, TransferListener<? super ContentDataSource> paramTransferListener)
  {
    this.resolver = paramContext.getContentResolver();
    this.listener = paramTransferListener;
  }
  
  /* Error */
  public void close()
    throws ContentDataSource.ContentDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 50	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:uri	Landroid/net/Uri;
    //   5: aload_0
    //   6: getfield 52	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/FileInputStream;
    //   9: ifnull +10 -> 19
    //   12: aload_0
    //   13: getfield 52	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/FileInputStream;
    //   16: invokevirtual 56	java/io/FileInputStream:close	()V
    //   19: aload_0
    //   20: aconst_null
    //   21: putfield 52	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/FileInputStream;
    //   24: aload_0
    //   25: getfield 58	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   28: ifnull +10 -> 38
    //   31: aload_0
    //   32: getfield 58	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   35: invokevirtual 61	android/content/res/AssetFileDescriptor:close	()V
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 58	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   43: aload_0
    //   44: getfield 63	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:opened	Z
    //   47: ifeq +25 -> 72
    //   50: aload_0
    //   51: iconst_0
    //   52: putfield 63	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:opened	Z
    //   55: aload_0
    //   56: getfield 43	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   59: ifnull +13 -> 72
    //   62: aload_0
    //   63: getfield 43	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   66: aload_0
    //   67: invokeinterface 69 2 0
    //   72: return
    //   73: astore_1
    //   74: new 8	org/telegram/messenger/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   77: astore_2
    //   78: aload_2
    //   79: aload_1
    //   80: invokespecial 72	org/telegram/messenger/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   83: aload_2
    //   84: athrow
    //   85: astore_2
    //   86: aload_0
    //   87: aconst_null
    //   88: putfield 58	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   91: aload_0
    //   92: getfield 63	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:opened	Z
    //   95: ifeq +25 -> 120
    //   98: aload_0
    //   99: iconst_0
    //   100: putfield 63	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:opened	Z
    //   103: aload_0
    //   104: getfield 43	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   107: ifnull +13 -> 120
    //   110: aload_0
    //   111: getfield 43	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   114: aload_0
    //   115: invokeinterface 69 2 0
    //   120: aload_2
    //   121: athrow
    //   122: astore_1
    //   123: new 8	org/telegram/messenger/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   126: astore_2
    //   127: aload_2
    //   128: aload_1
    //   129: invokespecial 72	org/telegram/messenger/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   132: aload_2
    //   133: athrow
    //   134: astore_2
    //   135: aload_0
    //   136: aconst_null
    //   137: putfield 52	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/FileInputStream;
    //   140: aload_0
    //   141: getfield 58	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   144: ifnull +10 -> 154
    //   147: aload_0
    //   148: getfield 58	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   151: invokevirtual 61	android/content/res/AssetFileDescriptor:close	()V
    //   154: aload_0
    //   155: aconst_null
    //   156: putfield 58	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   159: aload_0
    //   160: getfield 63	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:opened	Z
    //   163: ifeq +25 -> 188
    //   166: aload_0
    //   167: iconst_0
    //   168: putfield 63	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:opened	Z
    //   171: aload_0
    //   172: getfield 43	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   175: ifnull +13 -> 188
    //   178: aload_0
    //   179: getfield 43	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   182: aload_0
    //   183: invokeinterface 69 2 0
    //   188: aload_2
    //   189: athrow
    //   190: astore_2
    //   191: new 8	org/telegram/messenger/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   194: astore_1
    //   195: aload_1
    //   196: aload_2
    //   197: invokespecial 72	org/telegram/messenger/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   200: aload_1
    //   201: athrow
    //   202: astore_2
    //   203: aload_0
    //   204: aconst_null
    //   205: putfield 58	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   208: aload_0
    //   209: getfield 63	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:opened	Z
    //   212: ifeq +25 -> 237
    //   215: aload_0
    //   216: iconst_0
    //   217: putfield 63	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:opened	Z
    //   220: aload_0
    //   221: getfield 43	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   224: ifnull +13 -> 237
    //   227: aload_0
    //   228: getfield 43	org/telegram/messenger/exoplayer2/upstream/ContentDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   231: aload_0
    //   232: invokeinterface 69 2 0
    //   237: aload_2
    //   238: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	239	0	this	ContentDataSource
    //   73	7	1	localIOException1	IOException
    //   122	7	1	localIOException2	IOException
    //   194	7	1	localContentDataSourceException1	ContentDataSourceException
    //   77	7	2	localContentDataSourceException2	ContentDataSourceException
    //   85	36	2	localObject1	Object
    //   126	7	2	localContentDataSourceException3	ContentDataSourceException
    //   134	55	2	localObject2	Object
    //   190	7	2	localIOException3	IOException
    //   202	36	2	localObject3	Object
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
    throws ContentDataSource.ContentDataSourceException
  {
    try
    {
      this.uri = paramDataSpec.uri;
      this.assetFileDescriptor = this.resolver.openAssetFileDescriptor(this.uri, "r");
      if (this.assetFileDescriptor == null)
      {
        paramDataSpec = new java/io/FileNotFoundException;
        localObject = new java/lang/StringBuilder;
        ((StringBuilder)localObject).<init>();
        paramDataSpec.<init>("Could not open file descriptor for: " + this.uri);
        throw paramDataSpec;
      }
    }
    catch (IOException paramDataSpec)
    {
      throw new ContentDataSourceException(paramDataSpec);
    }
    Object localObject = new java/io/FileInputStream;
    ((FileInputStream)localObject).<init>(this.assetFileDescriptor.getFileDescriptor());
    this.inputStream = ((FileInputStream)localObject);
    long l1 = this.assetFileDescriptor.getStartOffset();
    l1 = this.inputStream.skip(paramDataSpec.position + l1) - l1;
    if (l1 != paramDataSpec.position)
    {
      paramDataSpec = new java/io/EOFException;
      paramDataSpec.<init>();
      throw paramDataSpec;
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
      long l2 = this.assetFileDescriptor.getLength();
      if (l2 == -1L)
      {
        localObject = this.inputStream.getChannel();
        l1 = ((FileChannel)localObject).size();
        if (l1 == 0L) {}
        for (l1 = -1L;; l1 -= ((FileChannel)localObject).position())
        {
          this.bytesRemaining = l1;
          break;
        }
      }
      this.bytesRemaining = (l2 - l1);
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ContentDataSource.ContentDataSourceException
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
            throw new ContentDataSourceException(new EOFException());
            long l = Math.min(this.bytesRemaining, paramInt2);
            paramInt2 = (int)l;
          }
          paramInt1 = -1;
        }
        catch (IOException paramArrayOfByte)
        {
          throw new ContentDataSourceException(paramArrayOfByte);
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
  
  public static class ContentDataSourceException
    extends IOException
  {
    public ContentDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/ContentDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */