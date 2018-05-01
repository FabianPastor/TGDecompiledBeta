package org.telegram.messenger.exoplayer2.offline;

import org.telegram.messenger.exoplayer2.C0542C;
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

    public DownloaderConstructorHelper(Cache cache, Factory factory) {
        this(cache, factory, null, null, null);
    }

    public DownloaderConstructorHelper(Cache cache, Factory factory, Factory factory2, DataSink.Factory factory3, PriorityTaskManager priorityTaskManager) {
        Assertions.checkNotNull(factory);
        this.cache = cache;
        this.upstreamDataSourceFactory = factory;
        this.cacheReadDataSourceFactory = factory2;
        this.cacheWriteDataSinkFactory = factory3;
        this.priorityTaskManager = priorityTaskManager;
    }

    public Cache getCache() {
        return this.cache;
    }

    public PriorityTaskManager getPriorityTaskManager() {
        return this.priorityTaskManager != null ? this.priorityTaskManager : new PriorityTaskManager();
    }

    public CacheDataSource buildCacheDataSource(boolean z) {
        DataSource createDataSource = this.cacheReadDataSourceFactory != null ? this.cacheReadDataSourceFactory.createDataSource() : new FileDataSource();
        if (z) {
            return new CacheDataSource(this.cache, DummyDataSource.INSTANCE, createDataSource, null, 1, null);
        }
        DataSink createDataSink = this.cacheWriteDataSinkFactory ? this.cacheWriteDataSinkFactory.createDataSink() : new CacheDataSink(this.cache, CacheDataSource.DEFAULT_MAX_CACHE_FILE_SIZE);
        z = this.upstreamDataSourceFactory.createDataSource();
        return new CacheDataSource(this.cache, this.priorityTaskManager == null ? z : new PriorityDataSource(z, this.priorityTaskManager, C0542C.PRIORITY_DOWNLOAD), createDataSource, createDataSink, 1, null);
    }
}
