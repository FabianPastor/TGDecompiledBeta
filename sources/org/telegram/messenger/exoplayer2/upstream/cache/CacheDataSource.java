package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSourceException;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.FileDataSource;
import org.telegram.messenger.exoplayer2.upstream.TeeDataSource;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class CacheDataSource implements DataSource {
    public static final long DEFAULT_MAX_CACHE_FILE_SIZE = 2097152;
    public static final int FLAG_BLOCK_ON_CACHE = 1;
    public static final int FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS = 4;
    public static final int FLAG_IGNORE_CACHE_ON_ERROR = 2;
    private static final long MIN_READ_BEFORE_CHECKING_CACHE = 102400;
    private final boolean blockOnCache;
    private long bytesRemaining;
    private final Cache cache;
    private final DataSource cacheReadDataSource;
    private final DataSource cacheWriteDataSource;
    private long checkCachePosition;
    private DataSource currentDataSource;
    private boolean currentDataSpecLengthUnset;
    private CacheSpan currentHoleSpan;
    private boolean currentRequestIgnoresCache;
    private final EventListener eventListener;
    private int flags;
    private final boolean ignoreCacheForUnsetLengthRequests;
    private final boolean ignoreCacheOnError;
    private String key;
    private long readPosition;
    private boolean seenCacheError;
    private long totalCachedBytesRead;
    private final DataSource upstreamDataSource;
    private Uri uri;

    public interface EventListener {
        void onCachedBytesRead(long j, long j2);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    public CacheDataSource(Cache cache, DataSource upstream) {
        this(cache, upstream, 0, DEFAULT_MAX_CACHE_FILE_SIZE);
    }

    public CacheDataSource(Cache cache, DataSource upstream, int flags) {
        this(cache, upstream, flags, DEFAULT_MAX_CACHE_FILE_SIZE);
    }

    public CacheDataSource(Cache cache, DataSource upstream, int flags, long maxCacheFileSize) {
        this(cache, upstream, new FileDataSource(), new CacheDataSink(cache, maxCacheFileSize), flags, null);
    }

    public CacheDataSource(Cache cache, DataSource upstream, DataSource cacheReadDataSource, DataSink cacheWriteDataSink, int flags, EventListener eventListener) {
        this.cache = cache;
        this.cacheReadDataSource = cacheReadDataSource;
        boolean z = false;
        this.blockOnCache = (flags & 1) != 0;
        this.ignoreCacheOnError = (flags & 2) != 0;
        if ((flags & 4) != 0) {
            z = true;
        }
        this.ignoreCacheForUnsetLengthRequests = z;
        this.upstreamDataSource = upstream;
        if (cacheWriteDataSink != null) {
            this.cacheWriteDataSource = new TeeDataSource(upstream, cacheWriteDataSink);
        } else {
            this.cacheWriteDataSource = null;
        }
        this.eventListener = eventListener;
    }

    public long open(DataSpec dataSpec) throws IOException {
        try {
            this.uri = dataSpec.uri;
            this.flags = dataSpec.flags;
            this.key = CacheUtil.getKey(dataSpec);
            this.readPosition = dataSpec.position;
            boolean z = (this.ignoreCacheOnError && this.seenCacheError) || (dataSpec.length == -1 && this.ignoreCacheForUnsetLengthRequests);
            this.currentRequestIgnoresCache = z;
            if (dataSpec.length == -1) {
                if (!this.currentRequestIgnoresCache) {
                    this.bytesRemaining = this.cache.getContentLength(this.key);
                    if (this.bytesRemaining != -1) {
                        this.bytesRemaining -= dataSpec.position;
                        if (this.bytesRemaining <= 0) {
                            throw new DataSourceException(0);
                        }
                    }
                    openNextSource(false);
                    return this.bytesRemaining;
                }
            }
            this.bytesRemaining = dataSpec.length;
            openNextSource(false);
            return this.bytesRemaining;
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        IOException e;
        IOException e2;
        CacheDataSource cacheDataSource = this;
        int i = readLength;
        if (i == 0) {
            return 0;
        }
        if (cacheDataSource.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (cacheDataSource.readPosition >= cacheDataSource.checkCachePosition) {
                openNextSource(true);
            }
            try {
                int bytesRead = cacheDataSource.currentDataSource.read(buffer, offset, i);
                if (bytesRead != -1) {
                    if (cacheDataSource.currentDataSource == cacheDataSource.cacheReadDataSource) {
                        cacheDataSource.totalCachedBytesRead += (long) bytesRead;
                    }
                    cacheDataSource.readPosition += (long) bytesRead;
                    if (cacheDataSource.bytesRemaining != -1) {
                        cacheDataSource.bytesRemaining -= (long) bytesRead;
                    }
                } else if (cacheDataSource.currentDataSpecLengthUnset) {
                    setBytesRemaining(0);
                } else {
                    if (cacheDataSource.bytesRemaining <= 0) {
                        if (cacheDataSource.bytesRemaining == -1) {
                        }
                    }
                    closeCurrentSource();
                    openNextSource(false);
                    return read(buffer, offset, readLength);
                }
                return bytesRead;
            } catch (IOException e3) {
                e = e3;
                e2 = e;
                if (cacheDataSource.currentDataSpecLengthUnset) {
                }
                handleBeforeThrow(e2);
                throw e2;
            }
        } catch (IOException e4) {
            e = e4;
            byte[] bArr = buffer;
            int i2 = offset;
            e2 = e;
            if (cacheDataSource.currentDataSpecLengthUnset || !isCausedByPositionOutOfRange(e2)) {
                handleBeforeThrow(e2);
                throw e2;
            }
            setBytesRemaining(0);
            return -1;
        }
    }

    public Uri getUri() {
        return this.currentDataSource == this.upstreamDataSource ? this.currentDataSource.getUri() : this.uri;
    }

    public void close() throws IOException {
        this.uri = null;
        notifyBytesRead();
        try {
            closeCurrentSource();
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    private void openNextSource(boolean checkCache) throws IOException {
        CacheSpan nextSpan;
        DataSource nextDataSource;
        DataSpec nextDataSpec;
        if (this.currentRequestIgnoresCache) {
            nextSpan = null;
        } else if (r1.blockOnCache) {
            try {
                nextSpan = r1.cache.startReadWrite(r1.key, r1.readPosition);
            } catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
        } else {
            nextSpan = r1.cache.startReadWriteNonBlocking(r1.key, r1.readPosition);
        }
        if (nextSpan == null) {
            nextDataSource = r1.upstreamDataSource;
            nextDataSpec = new DataSpec(r1.uri, r1.readPosition, r1.bytesRemaining, r1.key, r1.flags);
        } else if (nextSpan.isCached) {
            long length;
            Uri fileUri = Uri.fromFile(nextSpan.file);
            long filePosition = r1.readPosition - nextSpan.position;
            long length2 = nextSpan.length - filePosition;
            if (r1.bytesRemaining != -1) {
                length = Math.min(length2, r1.bytesRemaining);
            } else {
                length = length2;
            }
            nextDataSpec = new DataSpec(fileUri, r1.readPosition, filePosition, length, r1.key, r1.flags);
            nextDataSource = r1.cacheReadDataSource;
        } else {
            DataSource dataSource;
            if (nextSpan.isOpenEnded()) {
                nextDataSource = r1.bytesRemaining;
            } else {
                nextDataSource = nextSpan.length;
                if (r1.bytesRemaining != -1) {
                    nextDataSource = Math.min(nextDataSource, r1.bytesRemaining);
                }
            }
            DataSpec nextDataSpec2 = new DataSpec(r1.uri, r1.readPosition, nextDataSource, r1.key, r1.flags);
            if (r1.cacheWriteDataSource != null) {
                dataSource = r1.cacheWriteDataSource;
            } else {
                dataSource = r1.upstreamDataSource;
                r1.cache.releaseHoleSpan(nextSpan);
                nextSpan = null;
            }
            nextDataSpec = nextDataSpec2;
            nextDataSource = dataSource;
        }
        long j = (r1.currentRequestIgnoresCache || nextDataSource != r1.upstreamDataSource) ? Long.MAX_VALUE : r1.readPosition + MIN_READ_BEFORE_CHECKING_CACHE;
        r1.checkCachePosition = j;
        boolean z = false;
        if (checkCache) {
            Assertions.checkState(r1.currentDataSource == r1.upstreamDataSource);
            if (nextDataSource != r1.upstreamDataSource) {
                try {
                    closeCurrentSource();
                } catch (Throwable th) {
                    Throwable e2 = th;
                    if (nextSpan.isHoleSpan()) {
                        r1.cache.releaseHoleSpan(nextSpan);
                    }
                }
            } else {
                return;
            }
        }
        if (nextSpan != null && nextSpan.isHoleSpan()) {
            r1.currentHoleSpan = nextSpan;
        }
        r1.currentDataSource = nextDataSource;
        if (nextDataSpec.length == -1) {
            z = true;
        }
        r1.currentDataSpecLengthUnset = z;
        long resolvedLength = nextDataSource.open(nextDataSpec);
        if (r1.currentDataSpecLengthUnset && resolvedLength != -1) {
            setBytesRemaining(resolvedLength);
        }
    }

    private static boolean isCausedByPositionOutOfRange(IOException e) {
        Throwable cause = e;
        while (cause != null) {
            if ((cause instanceof DataSourceException) && ((DataSourceException) cause).reason == 0) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private void setBytesRemaining(long bytesRemaining) throws IOException {
        this.bytesRemaining = bytesRemaining;
        if (isWritingToCache()) {
            this.cache.setContentLength(this.key, this.readPosition + bytesRemaining);
        }
    }

    private boolean isWritingToCache() {
        return this.currentDataSource == this.cacheWriteDataSource;
    }

    private void closeCurrentSource() throws IOException {
        if (this.currentDataSource != null) {
            try {
                this.currentDataSource.close();
            } finally {
                this.currentDataSource = null;
                this.currentDataSpecLengthUnset = false;
                if (this.currentHoleSpan != null) {
                    this.cache.releaseHoleSpan(this.currentHoleSpan);
                    this.currentHoleSpan = null;
                }
            }
        }
    }

    private void handleBeforeThrow(IOException exception) {
        if (this.currentDataSource == this.cacheReadDataSource || (exception instanceof CacheException)) {
            this.seenCacheError = true;
        }
    }

    private void notifyBytesRead() {
        if (this.eventListener != null && this.totalCachedBytesRead > 0) {
            this.eventListener.onCachedBytesRead(this.cache.getCacheSpace(), this.totalCachedBytesRead);
            this.totalCachedBytesRead = 0;
        }
    }
}
