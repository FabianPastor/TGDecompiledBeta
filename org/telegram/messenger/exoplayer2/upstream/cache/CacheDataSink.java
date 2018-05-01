package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ReusableBufferedOutputStream;

public final class CacheDataSink
  implements DataSink
{
  public static final int DEFAULT_BUFFER_SIZE = 20480;
  private final int bufferSize;
  private ReusableBufferedOutputStream bufferedOutputStream;
  private final Cache cache;
  private DataSpec dataSpec;
  private long dataSpecBytesWritten;
  private File file;
  private final long maxCacheFileSize;
  private OutputStream outputStream;
  private long outputStreamBytesWritten;
  private FileOutputStream underlyingFileOutputStream;
  
  public CacheDataSink(Cache paramCache, long paramLong)
  {
    this(paramCache, paramLong, 20480);
  }
  
  public CacheDataSink(Cache paramCache, long paramLong, int paramInt)
  {
    this.cache = ((Cache)Assertions.checkNotNull(paramCache));
    this.maxCacheFileSize = paramLong;
    this.bufferSize = paramInt;
  }
  
  /* Error */
  private void closeCurrentOutputStream()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 57	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:outputStream	Ljava/io/OutputStream;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 57	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:outputStream	Ljava/io/OutputStream;
    //   12: invokevirtual 62	java/io/OutputStream:flush	()V
    //   15: aload_0
    //   16: getfield 64	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:underlyingFileOutputStream	Ljava/io/FileOutputStream;
    //   19: invokevirtual 70	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
    //   22: invokevirtual 75	java/io/FileDescriptor:sync	()V
    //   25: aload_0
    //   26: getfield 57	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:outputStream	Ljava/io/OutputStream;
    //   29: invokestatic 81	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   32: aload_0
    //   33: aconst_null
    //   34: putfield 57	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:outputStream	Ljava/io/OutputStream;
    //   37: aload_0
    //   38: getfield 83	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   41: astore_1
    //   42: aload_0
    //   43: aconst_null
    //   44: putfield 83	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   47: iconst_1
    //   48: ifeq +16 -> 64
    //   51: aload_0
    //   52: getfield 48	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:cache	Lorg/telegram/messenger/exoplayer2/upstream/cache/Cache;
    //   55: aload_1
    //   56: invokeinterface 87 2 0
    //   61: goto -54 -> 7
    //   64: aload_1
    //   65: invokevirtual 93	java/io/File:delete	()Z
    //   68: pop
    //   69: goto -62 -> 7
    //   72: astore_2
    //   73: aload_0
    //   74: getfield 57	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:outputStream	Ljava/io/OutputStream;
    //   77: invokestatic 81	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   80: aload_0
    //   81: aconst_null
    //   82: putfield 57	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:outputStream	Ljava/io/OutputStream;
    //   85: aload_0
    //   86: getfield 83	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   89: astore_1
    //   90: aload_0
    //   91: aconst_null
    //   92: putfield 83	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:file	Ljava/io/File;
    //   95: iconst_0
    //   96: ifeq +15 -> 111
    //   99: aload_0
    //   100: getfield 48	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink:cache	Lorg/telegram/messenger/exoplayer2/upstream/cache/Cache;
    //   103: aload_1
    //   104: invokeinterface 87 2 0
    //   109: aload_2
    //   110: athrow
    //   111: aload_1
    //   112: invokevirtual 93	java/io/File:delete	()Z
    //   115: pop
    //   116: goto -7 -> 109
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	119	0	this	CacheDataSink
    //   41	71	1	localFile	File
    //   72	38	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	25	72	finally
  }
  
  private void openNextOutputStream()
    throws IOException
  {
    long l;
    if (this.dataSpec.length == -1L)
    {
      l = this.maxCacheFileSize;
      this.file = this.cache.startFile(this.dataSpec.key, this.dataSpec.absoluteStreamPosition + this.dataSpecBytesWritten, l);
      this.underlyingFileOutputStream = new FileOutputStream(this.file);
      if (this.bufferSize <= 0) {
        break label151;
      }
      if (this.bufferedOutputStream != null) {
        break label137;
      }
      this.bufferedOutputStream = new ReusableBufferedOutputStream(this.underlyingFileOutputStream, this.bufferSize);
    }
    label100:
    label137:
    label151:
    for (this.outputStream = this.bufferedOutputStream;; this.outputStream = this.underlyingFileOutputStream)
    {
      this.outputStreamBytesWritten = 0L;
      return;
      l = Math.min(this.dataSpec.length - this.dataSpecBytesWritten, this.maxCacheFileSize);
      break;
      this.bufferedOutputStream.reset(this.underlyingFileOutputStream);
      break label100;
    }
  }
  
  public void close()
    throws CacheDataSink.CacheDataSinkException
  {
    if (this.dataSpec == null) {}
    for (;;)
    {
      return;
      try
      {
        closeCurrentOutputStream();
      }
      catch (IOException localIOException)
      {
        throw new CacheDataSinkException(localIOException);
      }
    }
  }
  
  public void open(DataSpec paramDataSpec)
    throws CacheDataSink.CacheDataSinkException
  {
    if ((paramDataSpec.length == -1L) && (!paramDataSpec.isFlagSet(2))) {
      this.dataSpec = null;
    }
    for (;;)
    {
      return;
      this.dataSpec = paramDataSpec;
      this.dataSpecBytesWritten = 0L;
      try
      {
        openNextOutputStream();
      }
      catch (IOException paramDataSpec)
      {
        throw new CacheDataSinkException(paramDataSpec);
      }
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws CacheDataSink.CacheDataSinkException
  {
    if (this.dataSpec == null) {
      return;
    }
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
    extends Cache.CacheException
  {
    public CacheDataSinkException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */