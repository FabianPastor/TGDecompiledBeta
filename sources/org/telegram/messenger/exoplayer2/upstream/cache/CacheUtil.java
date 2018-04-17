package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager.PriorityTooLowException;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CacheUtil {
    public static final int DEFAULT_BUFFER_SIZE_BYTES = 131072;

    public static class CachingCounters {
        public volatile long alreadyCachedBytes;
        public volatile long contentLength = -1;
        public volatile long newlyCachedBytes;

        public long totalCachedBytes() {
            return this.alreadyCachedBytes + this.newlyCachedBytes;
        }
    }

    public static String generateKey(Uri uri) {
        return uri.toString();
    }

    public static String getKey(DataSpec dataSpec) {
        return dataSpec.key != null ? dataSpec.key : generateKey(dataSpec.uri);
    }

    public static void getCached(DataSpec dataSpec, Cache cache, CachingCounters counters) {
        long left;
        Cache cache2;
        DataSpec dataSpec2 = dataSpec;
        CachingCounters cachingCounters = counters;
        String key = getKey(dataSpec);
        long start = dataSpec2.absoluteStreamPosition;
        if (dataSpec2.length != -1) {
            left = dataSpec2.length;
            cache2 = cache;
        } else {
            cache2 = cache;
            left = cache2.getContentLength(key);
        }
        cachingCounters.contentLength = left;
        cachingCounters.alreadyCachedBytes = 0;
        cachingCounters.newlyCachedBytes = 0;
        long start2 = start;
        long left2 = left;
        while (left2 != 0) {
            start = cache2.getCachedLength(key, start2, left2 != -1 ? left2 : Long.MAX_VALUE);
            if (start > 0) {
                cachingCounters.alreadyCachedBytes += start;
            } else {
                start = -start;
                if (start == Long.MAX_VALUE) {
                    return;
                }
            }
            left2 -= left2 == -1 ? 0 : start;
            start2 += start;
        }
    }

    public static void cache(DataSpec dataSpec, Cache cache, DataSource upstream, CachingCounters counters) throws IOException, InterruptedException {
        cache(dataSpec, cache, new CacheDataSource(cache, upstream), new byte[131072], null, 0, counters, false);
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheDataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters, boolean enableEOFException) throws IOException, InterruptedException {
        DataSpec dataSpec2 = dataSpec;
        Cache cache2 = cache;
        CachingCounters cachingCounters = counters;
        Assertions.checkNotNull(dataSource);
        Assertions.checkNotNull(buffer);
        if (cachingCounters != null) {
            getCached(dataSpec2, cache2, cachingCounters);
        } else {
            cachingCounters = new CachingCounters();
        }
        CachingCounters counters2 = cachingCounters;
        String key = getKey(dataSpec);
        long start = dataSpec2.absoluteStreamPosition;
        long left = dataSpec2.length != -1 ? dataSpec2.length : cache2.getContentLength(key);
        long start2 = start;
        while (true) {
            long left2 = left;
            long j = 0;
            if (left2 != 0) {
                long blockLength;
                start = cache2.getCachedLength(key, start2, left2 != -1 ? left2 : Long.MAX_VALUE);
                if (start <= 0) {
                    long blockLength2 = -start;
                    blockLength = blockLength2;
                    if (readAndDiscard(dataSpec2, start2, blockLength2, dataSource, buffer, priorityTaskManager, priority, counters2) < blockLength) {
                        break;
                    }
                }
                blockLength = start;
                start = start2 + blockLength;
                if (left2 != -1) {
                    j = blockLength;
                }
                start2 = start;
                left = left2 - j;
            } else {
                return;
            }
        }
        if (enableEOFException && left2 != -1) {
            throw new EOFException();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long readAndDiscard(DataSpec dataSpec, long absoluteStreamPosition, long length, DataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters) throws IOException, InterruptedException {
        long totalRead;
        DataSpec dataSpec2;
        Throwable th;
        DataSource dataSource2 = dataSource;
        byte[] bArr = buffer;
        CachingCounters cachingCounters = counters;
        DataSpec dataSpec3 = dataSpec;
        loop0:
        while (true) {
            if (priorityTaskManager != null) {
                priorityTaskManager.proceed(priority);
            }
            try {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                dataSpec3 = new DataSpec(dataSpec3.uri, dataSpec3.postBody, absoluteStreamPosition, (dataSpec3.position + absoluteStreamPosition) - dataSpec3.absoluteStreamPosition, -1, dataSpec3.key, dataSpec3.flags | 2);
                try {
                    long resolvedLength = dataSource2.open(dataSpec3);
                    long j = -1;
                    if (cachingCounters.contentLength == -1 && resolvedLength != -1) {
                        cachingCounters.contentLength = dataSpec3.absoluteStreamPosition + resolvedLength;
                    }
                    totalRead = 0;
                    while (totalRead != length) {
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                        int read = dataSource2.read(bArr, 0, length != j ? (int) Math.min((long) bArr.length, length - totalRead) : bArr.length);
                        if (read == -1) {
                            break;
                        }
                        long totalRead2 = totalRead + ((long) read);
                        dataSpec2 = dataSpec3;
                        try {
                            cachingCounters.newlyCachedBytes += (long) read;
                            totalRead = totalRead2;
                            j = -1;
                            dataSpec3 = dataSpec2;
                        } catch (PriorityTooLowException e) {
                            dataSpec3 = dataSpec2;
                        } catch (Throwable th2) {
                            th = th2;
                            dataSpec3 = dataSpec2;
                        }
                    }
                    break loop0;
                } catch (PriorityTooLowException e2) {
                    dataSpec2 = dataSpec3;
                } catch (Throwable th22) {
                    dataSpec2 = dataSpec3;
                    th = th22;
                }
            } catch (PriorityTooLowException e3) {
            } catch (Throwable th222) {
                th = th222;
            }
            Util.closeQuietly(dataSource);
        }
        if (cachingCounters.contentLength == -1) {
            cachingCounters.contentLength = dataSpec3.absoluteStreamPosition + totalRead;
        }
        dataSpec2 = dataSpec3;
        Util.closeQuietly(dataSource);
        return totalRead;
        Util.closeQuietly(dataSource);
        throw th;
    }

    public static void remove(Cache cache, String key) {
        for (CacheSpan cachedSpan : cache.getCachedSpans(key)) {
            try {
                cache.removeSpan(cachedSpan);
            } catch (CacheException e) {
            }
        }
    }

    private CacheUtil() {
    }
}
