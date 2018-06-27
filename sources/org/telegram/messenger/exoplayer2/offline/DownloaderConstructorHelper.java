package org.telegram.messenger.exoplayer2.offline;

import org.telegram.messenger.exoplayer2.upstream.DataSink;
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

public final class DownloaderConstructorHelper {
    private final Cache cache;
    private final Factory cacheReadDataSourceFactory;
    private final DataSink.Factory cacheWriteDataSinkFactory;
    private final PriorityTaskManager priorityTaskManager;
    private final Factory upstreamDataSourceFactory;

    public DownloaderConstructorHelper(Cache cache, Factory upstreamDataSourceFactory) {
        this(cache, upstreamDataSourceFactory, null, null, null);
    }

    public DownloaderConstructorHelper(Cache cache, Factory upstreamDataSourceFactory, Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory, PriorityTaskManager priorityTaskManager) {
        Assertions.checkNotNull(upstreamDataSourceFactory);
        this.cache = cache;
        this.upstreamDataSourceFactory = upstreamDataSourceFactory;
        this.cacheReadDataSourceFactory = cacheReadDataSourceFactory;
        this.cacheWriteDataSinkFactory = cacheWriteDataSinkFactory;
        this.priorityTaskManager = priorityTaskManager;
    }

    public Cache getCache() {
        return this.cache;
    }

    public PriorityTaskManager getPriorityTaskManager() {
        return this.priorityTaskManager != null ? this.priorityTaskManager : new PriorityTaskManager();
    }

    public CacheDataSource buildCacheDataSource(boolean offline) {
        DataSource cacheReadDataSource = this.cacheReadDataSourceFactory != null ? this.cacheReadDataSourceFactory.createDataSource() : new FileDataSource();
        if (offline) {
            return new CacheDataSource(this.cache, DummyDataSource.INSTANCE, cacheReadDataSource, null, 1, null);
        }
        DataSink cacheWriteDataSink = this.cacheWriteDataSinkFactory != null ? this.cacheWriteDataSinkFactory.createDataSink() : new CacheDataSink(this.cache, CacheDataSource.DEFAULT_MAX_CACHE_FILE_SIZE);
        DataSource upstream = this.upstreamDataSourceFactory.createDataSource();
        if (this.priorityTaskManager != null) {
            Object upstream2 = new PriorityDataSource(upstream, this.priorityTaskManager, -1000);
        }
        return new CacheDataSource(this.cache, upstream, cacheReadDataSource, cacheWriteDataSink, 1, null);
    }
}
