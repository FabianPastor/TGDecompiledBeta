package org.telegram.messenger.exoplayer.upstream.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.telegram.messenger.exoplayer.upstream.DataSink;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class CacheDataSink
  implements DataSink
{
  private final Cache cache;
  private DataSpec dataSpec;
  private long dataSpecBytesWritten;
  private File file;
  private final long maxCacheFileSize;
  private FileOutputStream outputStream;
  private long outputStreamBytesWritten;
  
  public CacheDataSink(Cache paramCache, long paramLong)
  {
    this.cache = ((Cache)Assertions.checkNotNull(paramCache));
    this.maxCacheFileSize = paramLong;
  }
  
  /* Error */
  private void closeCurrentOutputStream()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 44	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:outputStream	Ljava/io/FileOutputStream;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 44	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:outputStream	Ljava/io/FileOutputStream;
    //   12: invokevirtual 49	java/io/FileOutputStream:flush	()V
    //   15: aload_0
    //   16: getfield 44	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:outputStream	Ljava/io/FileOutputStream;
    //   19: invokevirtual 53	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
    //   22: invokevirtual 58	java/io/FileDescriptor:sync	()V
    //   25: aload_0
    //   26: getfield 44	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:outputStream	Ljava/io/FileOutputStream;
    //   29: invokestatic 64	org/telegram/messenger/exoplayer/util/Util:closeQuietly	(Ljava/io/OutputStream;)V
    //   32: iconst_1
    //   33: ifeq +27 -> 60
    //   36: aload_0
    //   37: getfield 36	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:cache	Lorg/telegram/messenger/exoplayer/upstream/cache/Cache;
    //   40: aload_0
    //   41: getfield 66	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   44: invokeinterface 70 2 0
    //   49: aload_0
    //   50: aconst_null
    //   51: putfield 44	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:outputStream	Ljava/io/FileOutputStream;
    //   54: aload_0
    //   55: aconst_null
    //   56: putfield 66	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   59: return
    //   60: aload_0
    //   61: getfield 66	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   64: invokevirtual 76	java/io/File:delete	()Z
    //   67: pop
    //   68: goto -19 -> 49
    //   71: astore_1
    //   72: aload_0
    //   73: getfield 44	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:outputStream	Ljava/io/FileOutputStream;
    //   76: invokestatic 64	org/telegram/messenger/exoplayer/util/Util:closeQuietly	(Ljava/io/OutputStream;)V
    //   79: iconst_0
    //   80: ifeq +28 -> 108
    //   83: aload_0
    //   84: getfield 36	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:cache	Lorg/telegram/messenger/exoplayer/upstream/cache/Cache;
    //   87: aload_0
    //   88: getfield 66	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   91: invokeinterface 70 2 0
    //   96: aload_0
    //   97: aconst_null
    //   98: putfield 44	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:outputStream	Ljava/io/FileOutputStream;
    //   101: aload_0
    //   102: aconst_null
    //   103: putfield 66	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   106: aload_1
    //   107: athrow
    //   108: aload_0
    //   109: getfield 66	org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   112: invokevirtual 76	java/io/File:delete	()Z
    //   115: pop
    //   116: goto -20 -> 96
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	119	0	this	CacheDataSink
    //   71	36	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	25	71	finally
  }
  
  private void openNextOutputStream()
    throws FileNotFoundException
  {
    this.file = this.cache.startFile(this.dataSpec.key, this.dataSpec.absoluteStreamPosition + this.dataSpecBytesWritten, Math.min(this.dataSpec.length - this.dataSpecBytesWritten, this.maxCacheFileSize));
    this.outputStream = new FileOutputStream(this.file);
    this.outputStreamBytesWritten = 0L;
  }
  
  public void close()
    throws CacheDataSink.CacheDataSinkException
  {
    try
    {
      closeCurrentOutputStream();
      return;
    }
    catch (IOException localIOException)
    {
      throw new CacheDataSinkException(localIOException);
    }
  }
  
  public DataSink open(DataSpec paramDataSpec)
    throws CacheDataSink.CacheDataSinkException
  {
    if (paramDataSpec.length != -1L) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      try
      {
        this.dataSpec = paramDataSpec;
        this.dataSpecBytesWritten = 0L;
        openNextOutputStream();
        return this;
      }
      catch (FileNotFoundException paramDataSpec)
      {
        throw new CacheDataSinkException(paramDataSpec);
      }
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws CacheDataSink.CacheDataSinkException
  {
    int i = 0;
    while (i < paramInt2) {
      try
      {
        if (this.outputStreamBytesWritten == this.maxCacheFileSize)
        {
          closeCurrentOutputStream();
          openNextOutputStream();
        }
        int j = (int)Math.min(paramInt2 - i, this.maxCacheFileSize - this.outputStreamBytesWritten);
        this.outputStream.write(paramArrayOfByte, paramInt1 + i, j);
        i += j;
        this.outputStreamBytesWritten += j;
        this.dataSpecBytesWritten += j;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new CacheDataSinkException(paramArrayOfByte);
      }
    }
  }
  
  public static class CacheDataSinkException
    extends IOException
  {
    public CacheDataSinkException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/cache/CacheDataSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */