package org.telegram.messenger.exoplayer2.offline;

import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSink.Factory;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DummyDataSource;
import org.telegram.messenger.exoplayer2.upstream.FileDataSource;
import org.telegram.messenger.exoplayer2.upstream.PriorityDataSource;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;

public final class DownloaderConstructorHelper
{
  private final Cache cache;
  private final DataSource.Factory cacheReadDataSourceFactory;
  private final DataSink.Factory cacheWriteDataSinkFactory;
  private final PriorityTaskManager priorityTaskManager;
  private final DataSource.Factory upstreamDataSourceFactory;
  
  public DownloaderConstructorHelper(Cache paramCache, DataSource.Factory paramFactory)
  {
    this(paramCache, paramFactory, null, null, null);
  }
  
  public DownloaderConstructorHelper(Cache paramCache, DataSource.Factory paramFactory1, DataSource.Factory paramFactory2, DataSink.Factory paramFactory, PriorityTaskManager paramPriorityTaskManager)
  {
    Assertions.checkNotNull(paramFactory1);
    this.cache = paramCache;
    this.upstreamDataSourceFactory = paramFactory1;
    this.cacheReadDataSourceFactory = paramFactory2;
    this.cacheWriteDataSinkFactory = paramFactory;
    this.priorityTaskManager = paramPriorityTaskManager;
  }
  
  public CacheDataSource buildCacheDataSource(boolean paramBoolean)
  {
    if (this.cacheReadDataSourceFactory != null) {}
    for (Object localObject1 = this.cacheReadDataSourceFactory.createDataSource(); paramBoolean; localObject1 = new FileDataSource())
    {
      localObject1 = new CacheDataSource(this.cache, DummyDataSource.INSTANCE, (DataSource)localObject1, null, 1, null);
      return (CacheDataSource)localObject1;
    }
    Object localObject2;
    label70:
    Object localObject3;
    if (this.cacheWriteDataSinkFactory != null)
    {
      localObject2 = this.cacheWriteDataSinkFactory.createDataSink();
      localObject3 = this.upstreamDataSourceFactory.createDataSource();
      if (this.priorityTaskManager != null) {
        break label127;
      }
    }
    for (;;)
    {
      localObject1 = new CacheDataSource(this.cache, (DataSource)localObject3, (DataSource)localObject1, (DataSink)localObject2, 1, null);
      break;
      localObject2 = new CacheDataSink(this.cache, 2097152L);
      break label70;
      label127:
      localObject3 = new PriorityDataSource((DataSource)localObject3, this.priorityTaskManager, 64536);
    }
  }
  
  public Cache getCache()
  {
    return this.cache;
  }
  
  public PriorityTaskManager getPriorityTaskManager()
  {
    if (this.priorityTaskManager != null) {}
    for (PriorityTaskManager localPriorityTaskManager = this.priorityTaskManager;; localPriorityTaskManager = new PriorityTaskManager()) {
      return localPriorityTaskManager;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/offline/DownloaderConstructorHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */