package org.telegram.messenger.exoplayer2.upstream.cache;

import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSink.Factory;

public final class CacheDataSinkFactory implements Factory {
    private final Cache cache;
    private final long maxCacheFileSize;

    public CacheDataSinkFactory(Cache cache, long maxCacheFileSize) {
        this.cache = cache;
        this.maxCacheFileSize = maxCacheFileSize;
    }

    public DataSink createDataSink() {
        return new CacheDataSink(this.cache, this.maxCacheFileSize);
    }
}
