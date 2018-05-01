package org.telegram.messenger.exoplayer2.upstream.cache;

import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSink.Factory;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.FileDataSourceFactory;

public final class CacheDataSourceFactory
  implements DataSource.Factory
{
  private final Cache cache;
  private final DataSource.Factory cacheReadDataSourceFactory;
  private final DataSink.Factory cacheWriteDataSinkFactory;
  private final CacheDataSource.EventListener eventListener;
  private final int flags;
  private final DataSource.Factory upstreamFactory;
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory)
  {
    this(paramCache, paramFactory, 0);
  }
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory, int paramInt)
  {
    this(paramCache, paramFactory, paramInt, 2097152L);
  }
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory, int paramInt, long paramLong)
  {
    this(paramCache, paramFactory, new FileDataSourceFactory(), new CacheDataSinkFactory(paramCache, paramLong), paramInt, null);
  }
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory1, DataSource.Factory paramFactory2, DataSink.Factory paramFactory, int paramInt, CacheDataSource.EventListener paramEventListener)
  {
    this.cache = paramCache;
    this.upstreamFactory = paramFactory1;
    this.cacheReadDataSourceFactory = paramFactory2;
    this.cacheWriteDataSinkFactory = paramFactory;
    this.flags = paramInt;
    this.eventListener = paramEventListener;
  }
  
  public CacheDataSource createDataSource()
  {
    Cache localCache = this.cache;
    DataSource localDataSource1 = this.upstreamFactory.createDataSource();
    DataSource localDataSource2 = this.cacheReadDataSourceFactory.createDataSource();
    if (this.cacheWriteDataSinkFactory != null) {}
    for (DataSink localDataSink = this.cacheWriteDataSinkFactory.createDataSink();; localDataSink = null) {
      return new CacheDataSource(localCache, localDataSource1, localDataSource2, localDataSink, this.flags, this.eventListener);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CacheDataSourceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */