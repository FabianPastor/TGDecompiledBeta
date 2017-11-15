package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.EOFException;
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
        String key = getKey(dataSpec);
        long start = dataSpec.absoluteStreamPosition;
        long left = dataSpec.length != -1 ? dataSpec.length : cache.getContentLength(key);
        counters.contentLength = left;
        counters.alreadyCachedBytes = 0;
        counters.newlyCachedBytes = 0;
        while (left != 0) {
            long blockLength = cache.getCachedBytes(key, start, left != -1 ? left : Long.MAX_VALUE);
            if (blockLength > 0) {
                counters.alreadyCachedBytes += blockLength;
            } else {
                blockLength = -blockLength;
                if (blockLength == Long.MAX_VALUE) {
                    return;
                }
            }
            start += blockLength;
            if (left == -1) {
                blockLength = 0;
            }
            left -= blockLength;
        }
    }

    public static void cache(DataSpec dataSpec, Cache cache, DataSource upstream, CachingCounters counters) throws IOException, InterruptedException {
        cache(dataSpec, cache, new CacheDataSource(cache, upstream), new byte[131072], null, 0, counters, false);
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheDataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters, boolean enableEOFException) throws IOException, InterruptedException {
        Assertions.checkNotNull(dataSource);
        Assertions.checkNotNull(buffer);
        if (counters != null) {
            getCached(dataSpec, cache, counters);
        } else {
            counters = new CachingCounters();
        }
        String key = getKey(dataSpec);
        long start = dataSpec.absoluteStreamPosition;
        long left = dataSpec.length != -1 ? dataSpec.length : cache.getContentLength(key);
        while (left != 0) {
            long blockLength = cache.getCachedBytes(key, start, left != -1 ? left : Long.MAX_VALUE);
            if (blockLength <= 0) {
                blockLength = -blockLength;
                if (readAndDiscard(dataSpec, start, blockLength, dataSource, buffer, priorityTaskManager, priority, counters) < blockLength) {
                    if (enableEOFException && left != -1) {
                        throw new EOFException();
                    }
                    return;
                }
            }
            start += blockLength;
            if (left == -1) {
                blockLength = 0;
            }
            left -= blockLength;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long readAndDiscard(DataSpec dataSpec, long absoluteStreamPosition, long length, DataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters) throws IOException, InterruptedException {
        long totalRead;
        Throwable th;
        DataSpec dataSpec2 = dataSpec;
        loop0:
        while (true) {
            if (priorityTaskManager != null) {
                priorityTaskManager.proceed(priority);
            }
            try {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                DataSpec dataSpec3 = new DataSpec(dataSpec2.uri, dataSpec2.postBody, absoluteStreamPosition, (dataSpec2.position + absoluteStreamPosition) - dataSpec2.absoluteStreamPosition, -1, dataSpec2.key, dataSpec2.flags | 2);
                try {
                    long resolvedLength = dataSource.open(dataSpec3);
                    if (counters.contentLength == -1 && resolvedLength != -1) {
                        counters.contentLength = dataSpec3.absoluteStreamPosition + resolvedLength;
                    }
                    totalRead = 0;
                    while (totalRead != length) {
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                        int read = dataSource.read(buffer, 0, length != -1 ? (int) Math.min((long) buffer.length, length - totalRead) : buffer.length);
                        if (read == -1) {
                            break loop0;
                        }
                        totalRead += (long) read;
                        counters.newlyCachedBytes += (long) read;
                    }
                    break loop0;
                } catch (PriorityTooLowException e) {
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (PriorityTooLowException e2) {
                dataSpec = dataSpec2;
            } catch (Throwable th3) {
                th = th3;
                dataSpec = dataSpec2;
            }
            Util.closeQuietly(dataSource);
            dataSpec2 = dataSpec;
        }
        Util.closeQuietly(dataSource);
        return totalRead;
        Util.closeQuietly(dataSource);
        throw th;
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
