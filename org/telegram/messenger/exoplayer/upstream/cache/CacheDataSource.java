package org.telegram.messenger.exoplayer.upstream.cache;

import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InterruptedIOException;
import org.telegram.messenger.exoplayer.upstream.DataSink;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.upstream.FileDataSource;
import org.telegram.messenger.exoplayer.upstream.TeeDataSource;

public final class CacheDataSource
  implements DataSource
{
  private static final String TAG = "CacheDataSource";
  private final boolean blockOnCache;
  private long bytesRemaining;
  private final Cache cache;
  private final DataSource cacheReadDataSource;
  private final DataSource cacheWriteDataSource;
  private DataSource currentDataSource;
  private final EventListener eventListener;
  private int flags;
  private boolean ignoreCache;
  private final boolean ignoreCacheOnError;
  private String key;
  private CacheSpan lockedSpan;
  private long readPosition;
  private long totalCachedBytesRead;
  private final DataSource upstreamDataSource;
  private Uri uri;
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource1, DataSource paramDataSource2, DataSink paramDataSink, boolean paramBoolean1, boolean paramBoolean2, EventListener paramEventListener)
  {
    this.cache = paramCache;
    this.cacheReadDataSource = paramDataSource2;
    this.blockOnCache = paramBoolean1;
    this.ignoreCacheOnError = paramBoolean2;
    this.upstreamDataSource = paramDataSource1;
    if (paramDataSink != null) {}
    for (this.cacheWriteDataSource = new TeeDataSource(paramDataSource1, paramDataSink);; this.cacheWriteDataSource = null)
    {
      this.eventListener = paramEventListener;
      return;
    }
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramCache, paramDataSource, paramBoolean1, paramBoolean2, Long.MAX_VALUE);
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    this(paramCache, paramDataSource, new FileDataSource(), new CacheDataSink(paramCache, paramLong), paramBoolean1, paramBoolean2, null);
  }
  
  private void closeCurrentSource()
    throws IOException
  {
    if (this.currentDataSource == null) {}
    for (;;)
    {
      return;
      try
      {
        this.currentDataSource.close();
        this.currentDataSource = null;
        return;
      }
      finally
      {
        if (this.lockedSpan != null)
        {
          this.cache.releaseHoleSpan(this.lockedSpan);
          this.lockedSpan = null;
        }
      }
    }
  }
  
  private void handleBeforeThrow(IOException paramIOException)
  {
    if ((this.ignoreCacheOnError) && ((this.currentDataSource == this.cacheReadDataSource) || ((paramIOException instanceof CacheDataSink.CacheDataSinkException)))) {
      this.ignoreCache = true;
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
  
  private void openNextSource()
    throws IOException
  {
    Object localObject1;
    if (this.ignoreCache)
    {
      localObject1 = null;
      if (localObject1 != null) {
        break label151;
      }
      this.currentDataSource = this.upstreamDataSource;
      localObject1 = new DataSpec(this.uri, this.readPosition, this.bytesRemaining, this.key, this.flags);
    }
    label151:
    Object localObject3;
    long l1;
    for (;;)
    {
      this.currentDataSource.open((DataSpec)localObject1);
      return;
      if (this.bytesRemaining == -1L)
      {
        Log.w("CacheDataSource", "Cache bypassed due to unbounded length.");
        localObject1 = null;
        break;
      }
      if (this.blockOnCache) {
        try
        {
          localObject1 = this.cache.startReadWrite(this.key, this.readPosition);
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new InterruptedIOException();
        }
      }
      localObject2 = this.cache.startReadWriteNonBlocking(this.key, this.readPosition);
      break;
      if (!((CacheSpan)localObject2).isCached) {
        break label231;
      }
      localObject3 = Uri.fromFile(((CacheSpan)localObject2).file);
      l1 = this.readPosition - ((CacheSpan)localObject2).position;
      long l2 = Math.min(((CacheSpan)localObject2).length - l1, this.bytesRemaining);
      localObject2 = new DataSpec((Uri)localObject3, this.readPosition, l1, l2, this.key, this.flags);
      this.currentDataSource = this.cacheReadDataSource;
    }
    label231:
    this.lockedSpan = ((CacheSpan)localObject2);
    if (((CacheSpan)localObject2).isOpenEnded())
    {
      l1 = this.bytesRemaining;
      label250:
      localObject3 = new DataSpec(this.uri, this.readPosition, l1, this.key, this.flags);
      if (this.cacheWriteDataSource == null) {
        break label318;
      }
    }
    label318:
    for (Object localObject2 = this.cacheWriteDataSource;; localObject2 = this.upstreamDataSource)
    {
      this.currentDataSource = ((DataSource)localObject2);
      localObject2 = localObject3;
      break;
      l1 = Math.min(((CacheSpan)localObject2).length, this.bytesRemaining);
      break label250;
    }
  }
  
  public void close()
    throws IOException
  {
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
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    try
    {
      this.uri = paramDataSpec.uri;
      this.flags = paramDataSpec.flags;
      this.key = paramDataSpec.key;
      this.readPosition = paramDataSpec.position;
      this.bytesRemaining = paramDataSpec.length;
      openNextSource();
      long l = paramDataSpec.length;
      return l;
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
    int i;
    try
    {
      i = this.currentDataSource.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i >= 0)
      {
        if (this.currentDataSource == this.cacheReadDataSource) {
          this.totalCachedBytesRead += i;
        }
        this.readPosition += i;
        if (this.bytesRemaining != -1L)
        {
          this.bytesRemaining -= i;
          return i;
        }
      }
      else
      {
        closeCurrentSource();
        if ((this.bytesRemaining > 0L) && (this.bytesRemaining != -1L))
        {
          openNextSource();
          paramInt1 = read(paramArrayOfByte, paramInt1, paramInt2);
          return paramInt1;
        }
      }
    }
    catch (IOException paramArrayOfByte)
    {
      handleBeforeThrow(paramArrayOfByte);
      throw paramArrayOfByte;
    }
    return i;
  }
  
  public static abstract interface EventListener
  {
    public abstract void onCachedBytesRead(long paramLong1, long paramLong2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/cache/CacheDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */