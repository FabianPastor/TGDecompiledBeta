package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.NavigableSet;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager.PriorityTooLowException;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CacheUtil
{
  public static final int DEFAULT_BUFFER_SIZE_BYTES = 131072;
  
  public static void cache(DataSpec paramDataSpec, Cache paramCache, DataSource paramDataSource, CachingCounters paramCachingCounters)
    throws IOException, InterruptedException
  {
    cache(paramDataSpec, paramCache, new CacheDataSource(paramCache, paramDataSource), new byte[131072], null, 0, paramCachingCounters, false);
  }
  
  public static void cache(DataSpec paramDataSpec, Cache paramCache, CacheDataSource paramCacheDataSource, byte[] paramArrayOfByte, PriorityTaskManager paramPriorityTaskManager, int paramInt, CachingCounters paramCachingCounters, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    Assertions.checkNotNull(paramCacheDataSource);
    Assertions.checkNotNull(paramArrayOfByte);
    String str;
    long l1;
    long l2;
    label51:
    long l3;
    if (paramCachingCounters != null)
    {
      getCached(paramDataSpec, paramCache, paramCachingCounters);
      str = getKey(paramDataSpec);
      l1 = paramDataSpec.absoluteStreamPosition;
      if (paramDataSpec.length == -1L) {
        break label133;
      }
      l2 = paramDataSpec.length;
      if (l2 == 0L) {
        return;
      }
      if (l2 == -1L) {
        break label146;
      }
      l3 = l2;
      label71:
      l3 = paramCache.getCachedLength(str, l1, l3);
      if (l3 <= 0L) {
        break label154;
      }
    }
    label133:
    label146:
    label154:
    long l4;
    do
    {
      l1 += l3;
      if (l2 == -1L) {
        l3 = 0L;
      }
      l2 -= l3;
      break label51;
      paramCachingCounters = new CachingCounters();
      break;
      l2 = paramCache.getContentLength(str);
      break label51;
      l3 = Long.MAX_VALUE;
      break label71;
      l4 = -l3;
      l3 = l4;
    } while (readAndDiscard(paramDataSpec, l1, l4, paramCacheDataSource, paramArrayOfByte, paramPriorityTaskManager, paramInt, paramCachingCounters) >= l4);
    if ((paramBoolean) && (l2 != -1L)) {
      throw new EOFException();
    }
  }
  
  public static String generateKey(Uri paramUri)
  {
    return paramUri.toString();
  }
  
  public static void getCached(DataSpec paramDataSpec, Cache paramCache, CachingCounters paramCachingCounters)
  {
    String str = getKey(paramDataSpec);
    long l1 = paramDataSpec.absoluteStreamPosition;
    long l2;
    label44:
    long l3;
    if (paramDataSpec.length != -1L)
    {
      l2 = paramDataSpec.length;
      paramCachingCounters.contentLength = l2;
      paramCachingCounters.alreadyCachedBytes = 0L;
      paramCachingCounters.newlyCachedBytes = 0L;
      if (l2 == 0L) {
        return;
      }
      if (l2 == -1L) {
        break label136;
      }
      l3 = l2;
      label64:
      l3 = paramCache.getCachedLength(str, l1, l3);
      if (l3 <= 0L) {
        break label144;
      }
      paramCachingCounters.alreadyCachedBytes += l3;
    }
    label136:
    label144:
    long l4;
    do
    {
      l1 += l3;
      if (l2 == -1L) {
        l3 = 0L;
      }
      l2 -= l3;
      break label44;
      l2 = paramCache.getContentLength(str);
      break;
      l3 = Long.MAX_VALUE;
      break label64;
      l4 = -l3;
      l3 = l4;
    } while (l4 != Long.MAX_VALUE);
  }
  
  public static String getKey(DataSpec paramDataSpec)
  {
    if (paramDataSpec.key != null) {}
    for (paramDataSpec = paramDataSpec.key;; paramDataSpec = generateKey(paramDataSpec.uri)) {
      return paramDataSpec;
    }
  }
  
  private static long readAndDiscard(DataSpec paramDataSpec, long paramLong1, long paramLong2, DataSource paramDataSource, byte[] paramArrayOfByte, PriorityTaskManager paramPriorityTaskManager, int paramInt, CachingCounters paramCachingCounters)
    throws IOException, InterruptedException
  {
    for (;;)
    {
      if (paramPriorityTaskManager != null) {
        paramPriorityTaskManager.proceed(paramInt);
      }
      try
      {
        if (Thread.interrupted())
        {
          InterruptedException localInterruptedException = new java/lang/InterruptedException;
          localInterruptedException.<init>();
          throw localInterruptedException;
        }
      }
      catch (PriorityTaskManager.PriorityTooLowException localPriorityTooLowException)
      {
        for (;;)
        {
          Util.closeQuietly(paramDataSource);
          break;
          DataSpec localDataSpec = new DataSpec(paramDataSpec.uri, paramDataSpec.postBody, paramLong1, paramDataSpec.position + paramLong1 - paramDataSpec.absoluteStreamPosition, -1L, paramDataSpec.key, paramDataSpec.flags | 0x2);
          try
          {
            l = paramDataSource.open(localDataSpec);
            if ((paramCachingCounters.contentLength == -1L) && (l != -1L)) {
              paramCachingCounters.contentLength = (localDataSpec.absoluteStreamPosition + l);
            }
            l = 0L;
            if (l != paramLong2) {
              if (Thread.interrupted())
              {
                paramDataSpec = new java/lang/InterruptedException;
                paramDataSpec.<init>();
                throw paramDataSpec;
              }
            }
          }
          catch (PriorityTaskManager.PriorityTooLowException paramDataSpec)
          {
            long l;
            paramDataSpec = paramDataSpec;
            paramDataSpec = localDataSpec;
            continue;
            if (paramLong2 != -1L) {}
            for (int i = (int)Math.min(paramArrayOfByte.length, paramLong2 - l);; i = paramArrayOfByte.length)
            {
              i = paramDataSource.read(paramArrayOfByte, 0, i);
              if (i != -1) {
                break;
              }
              if (paramCachingCounters.contentLength == -1L) {
                paramCachingCounters.contentLength = (localDataSpec.absoluteStreamPosition + l);
              }
              Util.closeQuietly(paramDataSource);
              return l;
            }
            l += i;
            paramCachingCounters.newlyCachedBytes += i;
          }
          finally {}
        }
        Util.closeQuietly(paramDataSource);
        throw paramDataSpec;
      }
      finally
      {
        for (;;) {}
      }
    }
  }
  
  public static void remove(Cache paramCache, String paramString)
  {
    paramString = paramCache.getCachedSpans(paramString).iterator();
    while (paramString.hasNext())
    {
      CacheSpan localCacheSpan = (CacheSpan)paramString.next();
      try
      {
        paramCache.removeSpan(localCacheSpan);
      }
      catch (Cache.CacheException localCacheException) {}
    }
  }
  
  public static class CachingCounters
  {
    public volatile long alreadyCachedBytes;
    public volatile long contentLength = -1L;
    public volatile long newlyCachedBytes;
    
    public long totalCachedBytes()
    {
      return this.alreadyCachedBytes + this.newlyCachedBytes;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CacheUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */