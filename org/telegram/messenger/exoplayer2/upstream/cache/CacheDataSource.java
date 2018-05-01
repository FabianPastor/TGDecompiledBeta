package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSourceException;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.FileDataSource;
import org.telegram.messenger.exoplayer2.upstream.TeeDataSource;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class CacheDataSource
  implements DataSource
{
  public static final long DEFAULT_MAX_CACHE_FILE_SIZE = 2097152L;
  public static final int FLAG_BLOCK_ON_CACHE = 1;
  public static final int FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS = 4;
  public static final int FLAG_IGNORE_CACHE_ON_ERROR = 2;
  private static final long MIN_READ_BEFORE_CHECKING_CACHE = 102400L;
  private final boolean blockOnCache;
  private long bytesRemaining;
  private final Cache cache;
  private final DataSource cacheReadDataSource;
  private final DataSource cacheWriteDataSource;
  private long checkCachePosition;
  private DataSource currentDataSource;
  private boolean currentDataSpecLengthUnset;
  private CacheSpan currentHoleSpan;
  private boolean currentRequestIgnoresCache;
  private final EventListener eventListener;
  private int flags;
  private final boolean ignoreCacheForUnsetLengthRequests;
  private final boolean ignoreCacheOnError;
  private String key;
  private long readPosition;
  private boolean seenCacheError;
  private long totalCachedBytesRead;
  private final DataSource upstreamDataSource;
  private Uri uri;
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource)
  {
    this(paramCache, paramDataSource, 0, 2097152L);
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource, int paramInt)
  {
    this(paramCache, paramDataSource, paramInt, 2097152L);
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource, int paramInt, long paramLong)
  {
    this(paramCache, paramDataSource, new FileDataSource(), new CacheDataSink(paramCache, paramLong), paramInt, null);
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource1, DataSource paramDataSource2, DataSink paramDataSink, int paramInt, EventListener paramEventListener)
  {
    this.cache = paramCache;
    this.cacheReadDataSource = paramDataSource2;
    boolean bool2;
    if ((paramInt & 0x1) != 0)
    {
      bool2 = true;
      this.blockOnCache = bool2;
      if ((paramInt & 0x2) == 0) {
        break label103;
      }
      bool2 = true;
      label43:
      this.ignoreCacheOnError = bool2;
      if ((paramInt & 0x4) == 0) {
        break label109;
      }
      bool2 = bool1;
      label60:
      this.ignoreCacheForUnsetLengthRequests = bool2;
      this.upstreamDataSource = paramDataSource1;
      if (paramDataSink == null) {
        break label115;
      }
    }
    label103:
    label109:
    label115:
    for (this.cacheWriteDataSource = new TeeDataSource(paramDataSource1, paramDataSink);; this.cacheWriteDataSource = null)
    {
      this.eventListener = paramEventListener;
      return;
      bool2 = false;
      break;
      bool2 = false;
      break label43;
      bool2 = false;
      break label60;
    }
  }
  
  /* Error */
  private void closeCurrentSource()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 100	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentDataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 100	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentDataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   12: invokeinterface 103 1 0
    //   17: aload_0
    //   18: aconst_null
    //   19: putfield 100	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentDataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   22: aload_0
    //   23: iconst_0
    //   24: putfield 105	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentDataSpecLengthUnset	Z
    //   27: aload_0
    //   28: getfield 107	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentHoleSpan	Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;
    //   31: ifnull -24 -> 7
    //   34: aload_0
    //   35: getfield 76	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:cache	Lorg/telegram/messenger/exoplayer2/upstream/cache/Cache;
    //   38: aload_0
    //   39: getfield 107	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentHoleSpan	Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;
    //   42: invokeinterface 113 2 0
    //   47: aload_0
    //   48: aconst_null
    //   49: putfield 107	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentHoleSpan	Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;
    //   52: goto -45 -> 7
    //   55: astore_1
    //   56: aload_0
    //   57: aconst_null
    //   58: putfield 100	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentDataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   61: aload_0
    //   62: iconst_0
    //   63: putfield 105	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentDataSpecLengthUnset	Z
    //   66: aload_0
    //   67: getfield 107	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentHoleSpan	Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;
    //   70: ifnull +21 -> 91
    //   73: aload_0
    //   74: getfield 76	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:cache	Lorg/telegram/messenger/exoplayer2/upstream/cache/Cache;
    //   77: aload_0
    //   78: getfield 107	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentHoleSpan	Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;
    //   81: invokeinterface 113 2 0
    //   86: aload_0
    //   87: aconst_null
    //   88: putfield 107	org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource:currentHoleSpan	Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;
    //   91: aload_1
    //   92: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	this	CacheDataSource
    //   55	37	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	17	55	finally
  }
  
  private void handleBeforeThrow(IOException paramIOException)
  {
    if ((this.currentDataSource == this.cacheReadDataSource) || ((paramIOException instanceof Cache.CacheException))) {
      this.seenCacheError = true;
    }
  }
  
  private static boolean isCausedByPositionOutOfRange(IOException paramIOException)
  {
    if (paramIOException != null) {
      if ((!(paramIOException instanceof DataSourceException)) || (((DataSourceException)paramIOException).reason != 0)) {}
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      paramIOException = paramIOException.getCause();
      break;
    }
  }
  
  private boolean isWritingToCache()
  {
    if (this.currentDataSource == this.cacheWriteDataSource) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void notifyBytesRead()
  {
    if ((this.eventListener != null) && (this.totalCachedBytesRead > 0L))
    {
      this.eventListener.onCachedBytesRead(this.cache.getCacheSpace(), this.totalCachedBytesRead);
      this.totalCachedBytesRead = 0L;
    }
  }
  
  private void openNextSource(boolean paramBoolean)
    throws IOException
  {
    CacheSpan localCacheSpan1;
    Object localObject;
    DataSpec localDataSpec;
    label47:
    long l1;
    if (this.currentRequestIgnoresCache)
    {
      localCacheSpan1 = null;
      if (localCacheSpan1 != null) {
        break label166;
      }
      localObject = this.upstreamDataSource;
      localDataSpec = new DataSpec(this.uri, this.readPosition, this.bytesRemaining, this.key, this.flags);
      if ((this.currentRequestIgnoresCache) || (localObject != this.upstreamDataSource)) {
        break label371;
      }
      l1 = this.readPosition + 102400L;
      label72:
      this.checkCachePosition = l1;
      if (!paramBoolean) {
        break label388;
      }
      if (this.currentDataSource != this.upstreamDataSource) {
        break label379;
      }
    }
    CacheSpan localCacheSpan2;
    label166:
    label351:
    label371:
    label379:
    for (paramBoolean = true;; paramBoolean = false)
    {
      Assertions.checkState(paramBoolean);
      if (localObject != this.upstreamDataSource) {
        break label384;
      }
      return;
      if (this.blockOnCache) {
        try
        {
          localCacheSpan1 = this.cache.startReadWrite(this.key, this.readPosition);
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new InterruptedIOException();
        }
      }
      localCacheSpan2 = this.cache.startReadWriteNonBlocking(this.key, this.readPosition);
      break;
      long l3;
      if (localCacheSpan2.isCached)
      {
        localObject = Uri.fromFile(localCacheSpan2.file);
        long l2 = this.readPosition - localCacheSpan2.position;
        l3 = localCacheSpan2.length - l2;
        l1 = l3;
        if (this.bytesRemaining != -1L) {
          l1 = Math.min(l3, this.bytesRemaining);
        }
        localDataSpec = new DataSpec((Uri)localObject, this.readPosition, l2, l1, this.key, this.flags);
        localObject = this.cacheReadDataSource;
        break label47;
      }
      if (localCacheSpan2.isOpenEnded()) {
        l1 = this.bytesRemaining;
      }
      for (;;)
      {
        localDataSpec = new DataSpec(this.uri, this.readPosition, l1, this.key, this.flags);
        if (this.cacheWriteDataSource == null) {
          break label351;
        }
        localObject = this.cacheWriteDataSource;
        break;
        l3 = localCacheSpan2.length;
        l1 = l3;
        if (this.bytesRemaining != -1L) {
          l1 = Math.min(l3, this.bytesRemaining);
        }
      }
      localObject = this.upstreamDataSource;
      this.cache.releaseHoleSpan(localCacheSpan2);
      localCacheSpan2 = null;
      break label47;
      l1 = Long.MAX_VALUE;
      break label72;
    }
    for (;;)
    {
      try
      {
        label384:
        closeCurrentSource();
        label388:
        if ((localCacheSpan2 != null) && (localCacheSpan2.isHoleSpan())) {
          this.currentHoleSpan = localCacheSpan2;
        }
        this.currentDataSource = ((DataSource)localObject);
        if (localDataSpec.length == -1L)
        {
          paramBoolean = true;
          this.currentDataSpecLengthUnset = paramBoolean;
          l1 = ((DataSource)localObject).open(localDataSpec);
          if ((!this.currentDataSpecLengthUnset) || (l1 == -1L)) {
            break;
          }
          setBytesRemaining(l1);
        }
      }
      catch (Throwable localThrowable)
      {
        if (localCacheSpan2.isHoleSpan()) {
          this.cache.releaseHoleSpan(localCacheSpan2);
        }
        throw localThrowable;
      }
      paramBoolean = false;
    }
  }
  
  private void setBytesRemaining(long paramLong)
    throws IOException
  {
    this.bytesRemaining = paramLong;
    if (isWritingToCache()) {
      this.cache.setContentLength(this.key, this.readPosition + paramLong);
    }
  }
  
  public void close()
    throws IOException
  {
    this.uri = null;
    notifyBytesRead();
    try
    {
      closeCurrentSource();
      return;
    }
    catch (IOException localIOException)
    {
      handleBeforeThrow(localIOException);
      throw localIOException;
    }
  }
  
  public Uri getUri()
  {
    if (this.currentDataSource == this.upstreamDataSource) {}
    for (Uri localUri = this.currentDataSource.getUri();; localUri = this.uri) {
      return localUri;
    }
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    boolean bool1 = false;
    try
    {
      this.uri = paramDataSpec.uri;
      this.flags = paramDataSpec.flags;
      this.key = CacheUtil.getKey(paramDataSpec);
      this.readPosition = paramDataSpec.position;
      boolean bool2;
      if ((!this.ignoreCacheOnError) || (!this.seenCacheError))
      {
        bool2 = bool1;
        if (paramDataSpec.length == -1L)
        {
          bool2 = bool1;
          if (!this.ignoreCacheForUnsetLengthRequests) {}
        }
      }
      else
      {
        bool2 = true;
      }
      this.currentRequestIgnoresCache = bool2;
      if ((paramDataSpec.length != -1L) || (this.currentRequestIgnoresCache)) {
        this.bytesRemaining = paramDataSpec.length;
      }
      do
      {
        do
        {
          openNextSource(false);
          return this.bytesRemaining;
          this.bytesRemaining = this.cache.getContentLength(this.key);
        } while (this.bytesRemaining == -1L);
        this.bytesRemaining -= paramDataSpec.position;
      } while (this.bytesRemaining > 0L);
      paramDataSpec = new org/telegram/messenger/exoplayer2/upstream/DataSourceException;
      paramDataSpec.<init>(0);
      throw paramDataSpec;
    }
    catch (IOException paramDataSpec)
    {
      handleBeforeThrow(paramDataSpec);
      throw paramDataSpec;
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    if (paramInt2 == 0) {}
    for (;;)
    {
      return i;
      if (this.bytesRemaining == 0L)
      {
        i = -1;
        continue;
      }
      try
      {
        if (this.readPosition >= this.checkCachePosition) {
          openNextSource(true);
        }
        j = this.currentDataSource.read(paramArrayOfByte, paramInt1, paramInt2);
        if (j != -1)
        {
          if (this.currentDataSource == this.cacheReadDataSource) {
            this.totalCachedBytesRead += j;
          }
          this.readPosition += j;
          i = j;
          if (this.bytesRemaining == -1L) {
            continue;
          }
          this.bytesRemaining -= j;
          i = j;
        }
      }
      catch (IOException paramArrayOfByte)
      {
        int j;
        if ((this.currentDataSpecLengthUnset) && (isCausedByPositionOutOfRange(paramArrayOfByte)))
        {
          setBytesRemaining(0L);
          i = -1;
          continue;
          if (this.currentDataSpecLengthUnset)
          {
            setBytesRemaining(0L);
            i = j;
            continue;
          }
          if (this.bytesRemaining <= 0L)
          {
            i = j;
            if (this.bytesRemaining != -1L) {
              continue;
            }
          }
          closeCurrentSource();
          openNextSource(false);
          i = read(paramArrayOfByte, paramInt1, paramInt2);
          continue;
        }
        handleBeforeThrow(paramArrayOfByte);
        throw paramArrayOfByte;
      }
    }
  }
  
  public static abstract interface EventListener
  {
    public abstract void onCachedBytesRead(long paramLong1, long paramLong2);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */