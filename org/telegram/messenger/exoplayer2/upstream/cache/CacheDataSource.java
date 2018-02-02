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

    private void closeCurrentSource() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1444)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1466)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:80)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r4 = this;
        r1 = 0;
        r3 = 0;
        r0 = r4.currentDataSource;
        if (r0 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r0 = r4.currentDataSource;	 Catch:{ all -> 0x001e }
        r0.close();	 Catch:{ all -> 0x001e }
        r4.currentDataSource = r3;
        r4.currentDataSpecLengthUnset = r1;
        r0 = r4.currentHoleSpan;
        if (r0 == 0) goto L_0x0006;
    L_0x0014:
        r0 = r4.cache;
        r1 = r4.currentHoleSpan;
        r0.releaseHoleSpan(r1);
        r4.currentHoleSpan = r3;
        goto L_0x0006;
    L_0x001e:
        r0 = move-exception;
        r4.currentDataSource = r3;
        r4.currentDataSpecLengthUnset = r1;
        r1 = r4.currentHoleSpan;
        if (r1 == 0) goto L_0x0030;
    L_0x0027:
        r1 = r4.cache;
        r2 = r4.currentHoleSpan;
        r1.releaseHoleSpan(r2);
        r4.currentHoleSpan = r3;
    L_0x0030:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource.closeCurrentSource():void");
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
        boolean z;
        boolean z2 = true;
        this.cache = cache;
        this.cacheReadDataSource = cacheReadDataSource;
        this.blockOnCache = (flags & 1) != 0;
        if ((flags & 2) != 0) {
            z = true;
        } else {
            z = false;
        }
        this.ignoreCacheOnError = z;
        if ((flags & 4) == 0) {
            z2 = false;
        }
        this.ignoreCacheForUnsetLengthRequests = z2;
        this.upstreamDataSource = upstream;
        if (cacheWriteDataSink != null) {
            this.cacheWriteDataSource = new TeeDataSource(upstream, cacheWriteDataSink);
        } else {
            this.cacheWriteDataSource = null;
        }
        this.eventListener = eventListener;
    }

    public long open(DataSpec dataSpec) throws IOException {
        boolean z = false;
        try {
            this.uri = dataSpec.uri;
            this.flags = dataSpec.flags;
            this.key = CacheUtil.getKey(dataSpec);
            this.readPosition = dataSpec.position;
            if ((this.ignoreCacheOnError && this.seenCacheError) || (dataSpec.length == -1 && this.ignoreCacheForUnsetLengthRequests)) {
                z = true;
            }
            this.currentRequestIgnoresCache = z;
            if (dataSpec.length != -1 || this.currentRequestIgnoresCache) {
                this.bytesRemaining = dataSpec.length;
            } else {
                this.bytesRemaining = this.cache.getContentLength(this.key);
                if (this.bytesRemaining != -1) {
                    this.bytesRemaining -= dataSpec.position;
                    if (this.bytesRemaining <= 0) {
                        throw new DataSourceException(0);
                    }
                }
            }
            openNextSource(false);
            return this.bytesRemaining;
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (this.readPosition >= this.checkCachePosition) {
                openNextSource(true);
            }
            int bytesRead = this.currentDataSource.read(buffer, offset, readLength);
            if (bytesRead != -1) {
                if (this.currentDataSource == this.cacheReadDataSource) {
                    this.totalCachedBytesRead += (long) bytesRead;
                }
                this.readPosition += (long) bytesRead;
                if (this.bytesRemaining == -1) {
                    return bytesRead;
                }
                this.bytesRemaining -= (long) bytesRead;
                return bytesRead;
            } else if (this.currentDataSpecLengthUnset) {
                setBytesRemaining(0);
                return bytesRead;
            } else if (this.bytesRemaining <= 0 && this.bytesRemaining != -1) {
                return bytesRead;
            } else {
                closeCurrentSource();
                openNextSource(false);
                return read(buffer, offset, readLength);
            }
        } catch (IOException e) {
            if (this.currentDataSpecLengthUnset && isCausedByPositionOutOfRange(e)) {
                setBytesRemaining(0);
                return -1;
            }
            handleBeforeThrow(e);
            throw e;
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
        } else if (this.blockOnCache) {
            try {
                nextSpan = this.cache.startReadWrite(this.key, this.readPosition);
            } catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
        } else {
            nextSpan = this.cache.startReadWriteNonBlocking(this.key, this.readPosition);
        }
        if (nextSpan == null) {
            nextDataSource = this.upstreamDataSource;
            nextDataSpec = new DataSpec(this.uri, this.readPosition, this.bytesRemaining, this.key, this.flags);
        } else if (nextSpan.isCached) {
            Uri fileUri = Uri.fromFile(nextSpan.file);
            long filePosition = this.readPosition - nextSpan.position;
            length = nextSpan.length - filePosition;
            if (this.bytesRemaining != -1) {
                length = Math.min(length, this.bytesRemaining);
            }
            nextDataSpec = new DataSpec(fileUri, this.readPosition, filePosition, length, this.key, this.flags);
            nextDataSource = this.cacheReadDataSource;
        } else {
            if (nextSpan.isOpenEnded()) {
                length = this.bytesRemaining;
            } else {
                length = nextSpan.length;
                if (this.bytesRemaining != -1) {
                    length = Math.min(length, this.bytesRemaining);
                }
            }
            DataSpec dataSpec = new DataSpec(this.uri, this.readPosition, length, this.key, this.flags);
            if (this.cacheWriteDataSource != null) {
                nextDataSource = this.cacheWriteDataSource;
            } else {
                nextDataSource = this.upstreamDataSource;
                this.cache.releaseHoleSpan(nextSpan);
                nextSpan = null;
            }
        }
        long j = (this.currentRequestIgnoresCache || nextDataSource != this.upstreamDataSource) ? Long.MAX_VALUE : this.readPosition + MIN_READ_BEFORE_CHECKING_CACHE;
        this.checkCachePosition = j;
        if (checkCache) {
            Assertions.checkState(this.currentDataSource == this.upstreamDataSource);
            if (nextDataSource != this.upstreamDataSource) {
                try {
                    closeCurrentSource();
                } catch (Throwable th) {
                    if (nextSpan.isHoleSpan()) {
                        this.cache.releaseHoleSpan(nextSpan);
                    }
                }
            } else {
                return;
            }
        }
        if (nextSpan != null && nextSpan.isHoleSpan()) {
            this.currentHoleSpan = nextSpan;
        }
        this.currentDataSource = nextDataSource;
        this.currentDataSpecLengthUnset = nextDataSpec.length == -1;
        long resolvedLength = nextDataSource.open(nextDataSpec);
        if (this.currentDataSpecLengthUnset && resolvedLength != -1) {
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
