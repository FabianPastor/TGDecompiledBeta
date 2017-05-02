package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.IOException;
import java.util.NavigableSet;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager.PriorityTooLowException;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CacheUtil {

    public static class CachingCounters {
        public long alreadyCachedBytes;
        public long downloadedBytes;
    }

    public static String generateKey(Uri uri) {
        return uri.toString();
    }

    public static String getKey(DataSpec dataSpec) {
        return dataSpec.key != null ? dataSpec.key : generateKey(dataSpec.uri);
    }

    public static CachingCounters getCached(DataSpec dataSpec, Cache cache, CachingCounters counters) {
        Exception e;
        try {
            return internalCache(dataSpec, cache, null, null, null, 0, counters);
        } catch (IOException e2) {
            e = e2;
            throw new IllegalStateException(e);
        } catch (InterruptedException e3) {
            e = e3;
            throw new IllegalStateException(e);
        }
    }

    public static CachingCounters cache(DataSpec dataSpec, Cache cache, CacheDataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters) throws IOException, InterruptedException {
        Assertions.checkNotNull(dataSource);
        Assertions.checkNotNull(buffer);
        return internalCache(dataSpec, cache, dataSource, buffer, priorityTaskManager, priority, counters);
    }

    private static CachingCounters internalCache(DataSpec dataSpec, Cache cache, CacheDataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters) throws IOException, InterruptedException {
        long start = dataSpec.position;
        long left = dataSpec.length;
        String key = getKey(dataSpec);
        if (left == -1) {
            left = cache.getContentLength(key);
            if (left == -1) {
                left = Long.MAX_VALUE;
            }
        }
        if (counters == null) {
            counters = new CachingCounters();
        } else {
            counters.alreadyCachedBytes = 0;
            counters.downloadedBytes = 0;
        }
        while (left > 0) {
            long blockLength = cache.getCachedBytes(key, start, left);
            if (blockLength <= 0) {
                blockLength = -blockLength;
                if (dataSource != null && buffer != null) {
                    long j;
                    Uri uri = dataSpec.uri;
                    if (blockLength == Long.MAX_VALUE) {
                        j = -1;
                    } else {
                        j = blockLength;
                    }
                    long read = readAndDiscard(new DataSpec(uri, start, j, key), dataSource, buffer, priorityTaskManager, priority);
                    counters.downloadedBytes += read;
                    if (read < blockLength) {
                        break;
                    }
                } else if (blockLength == Long.MAX_VALUE) {
                    counters.downloadedBytes = -1;
                    break;
                } else {
                    counters.downloadedBytes += blockLength;
                }
            } else {
                counters.alreadyCachedBytes += blockLength;
            }
            start += blockLength;
            if (left != Long.MAX_VALUE) {
                left -= blockLength;
            }
        }
        return counters;
    }

    private static long readAndDiscard(DataSpec dataSpec, DataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority) throws IOException, InterruptedException {
        while (true) {
            if (priorityTaskManager != null) {
                priorityTaskManager.proceed(priority);
            }
            try {
                dataSource.open(dataSpec);
                long totalRead = 0;
                while (!Thread.interrupted()) {
                    int read = dataSource.read(buffer, 0, buffer.length);
                    if (read == -1) {
                        Util.closeQuietly(dataSource);
                        return totalRead;
                    }
                    totalRead += (long) read;
                }
                throw new InterruptedException();
            } catch (PriorityTooLowException e) {
                Util.closeQuietly(dataSource);
            } catch (Throwable th) {
                Util.closeQuietly(dataSource);
            }
        }
    }

    public static void remove(Cache cache, String key) {
        NavigableSet<CacheSpan> cachedSpans = cache.getCachedSpans(key);
        if (cachedSpans != null) {
            for (CacheSpan cachedSpan : cachedSpans) {
                try {
                    cache.removeSpan(cachedSpan);
                } catch (CacheException e) {
                }
            }
        }
    }

    private CacheUtil() {
    }
}
