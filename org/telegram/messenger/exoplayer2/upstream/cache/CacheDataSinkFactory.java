package org.telegram.messenger.exoplayer2.upstream.cache;

import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSink.Factory;

public final class CacheDataSinkFactory
  implements DataSink.Factory
{
  private final int bufferSize;
  private final Cache cache;
  private final long maxCacheFileSize;
  
  public CacheDataSinkFactory(Cache paramCache, long paramLong)
  {
    this(paramCache, paramLong, 20480);
  }
  
  public CacheDataSinkFactory(Cache paramCache, long paramLong, int paramInt)
  {
    this.cache = paramCache;
    this.maxCacheFileSize = paramLong;
    this.bufferSize = paramInt;
  }
  
  public DataSink createDataSink()
  {
    return new CacheDataSink(this.cache, this.maxCacheFileSize, this.bufferSize);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSinkFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */