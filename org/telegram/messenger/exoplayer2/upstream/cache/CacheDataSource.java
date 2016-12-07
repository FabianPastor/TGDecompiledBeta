package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import android.util.Log;
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
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink.CacheDataSinkException;

public final class CacheDataSource implements DataSource {
    public static final long DEFAULT_MAX_CACHE_FILE_SIZE = 2097152;
    public static final int FLAG_BLOCK_ON_CACHE = 1;
    public static final int FLAG_CACHE_UNBOUNDED_REQUESTS = 4;
    public static final int FLAG_IGNORE_CACHE_ON_ERROR = 2;
    private static final String TAG = "CacheDataSource";
    private final boolean blockOnCache;
    private final boolean bypassUnboundedRequests;
    private long bytesRemaining;
    private final Cache cache;
    private final DataSource cacheReadDataSource;
    private final DataSource cacheWriteDataSource;
    private DataSource currentDataSource;
    private boolean currentRequestIgnoresCache;
    private boolean currentRequestUnbounded;
    private final EventListener eventListener;
    private int flags;
    private final boolean ignoreCacheOnError;
    private String key;
    private CacheSpan lockedSpan;
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
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1439)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1461)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:80)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r4 = this;
        r3 = 0;
        r0 = r4.currentDataSource;
        if (r0 != 0) goto L_0x0006;
    L_0x0005:
        return;
    L_0x0006:
        r0 = r4.currentDataSource;	 Catch:{ all -> 0x001f }
        r0.close();	 Catch:{ all -> 0x001f }
        r0 = 0;	 Catch:{ all -> 0x001f }
        r4.currentDataSource = r0;	 Catch:{ all -> 0x001f }
        r0 = 0;	 Catch:{ all -> 0x001f }
        r4.currentRequestUnbounded = r0;	 Catch:{ all -> 0x001f }
        r0 = r4.lockedSpan;
        if (r0 == 0) goto L_0x0005;
    L_0x0015:
        r0 = r4.cache;
        r1 = r4.lockedSpan;
        r0.releaseHoleSpan(r1);
        r4.lockedSpan = r3;
        goto L_0x0005;
    L_0x001f:
        r0 = move-exception;
        r1 = r4.lockedSpan;
        if (r1 == 0) goto L_0x002d;
    L_0x0024:
        r1 = r4.cache;
        r2 = r4.lockedSpan;
        r1.releaseHoleSpan(r2);
        r4.lockedSpan = r3;
    L_0x002d:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource.closeCurrentSource():void");
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
        if ((flags & 4) != 0) {
            z2 = false;
        }
        this.bypassUnboundedRequests = z2;
        this.upstreamDataSource = upstream;
        if (cacheWriteDataSink != null) {
            this.cacheWriteDataSource = new TeeDataSource(upstream, cacheWriteDataSink);
        } else {
            this.cacheWriteDataSource = null;
        }
        this.eventListener = eventListener;
    }

    public long open(DataSpec dataSpec) throws IOException {
        boolean z = true;
        try {
            this.uri = dataSpec.uri;
            this.flags = dataSpec.flags;
            this.key = dataSpec.key;
            this.readPosition = dataSpec.position;
            if (!((this.ignoreCacheOnError && this.seenCacheError) || (this.bypassUnboundedRequests && dataSpec.length == -1))) {
                z = false;
            }
            this.currentRequestIgnoresCache = z;
            if (dataSpec.length != -1 || this.currentRequestIgnoresCache) {
                this.bytesRemaining = dataSpec.length;
            } else {
                this.bytesRemaining = this.cache.getContentLength(this.key);
                if (this.bytesRemaining != -1) {
                    this.bytesRemaining -= dataSpec.position;
                }
            }
            openNextSource(true);
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
            int bytesRead = this.currentDataSource.read(buffer, offset, readLength);
            if (bytesRead >= 0) {
                if (this.currentDataSource == this.cacheReadDataSource) {
                    this.totalCachedBytesRead += (long) bytesRead;
                }
                this.readPosition += (long) bytesRead;
                if (this.bytesRemaining == -1) {
                    return bytesRead;
                }
                this.bytesRemaining -= (long) bytesRead;
                return bytesRead;
            }
            if (this.currentRequestUnbounded) {
                setContentLength(this.readPosition);
                this.bytesRemaining = 0;
            }
            closeCurrentSource();
            if ((this.bytesRemaining > 0 || this.bytesRemaining == -1) && openNextSource(false)) {
                return read(buffer, offset, readLength);
            }
            return bytesRead;
        } catch (IOException e) {
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

    private boolean openNextSource(boolean initial) throws IOException {
        CacheSpan span;
        DataSpec dataSpec;
        if (this.currentRequestIgnoresCache) {
            span = null;
        } else if (this.blockOnCache) {
            try {
                span = this.cache.startReadWrite(this.key, this.readPosition);
            } catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
        } else {
            span = this.cache.startReadWriteNonBlocking(this.key, this.readPosition);
        }
        if (span == null) {
            this.currentDataSource = this.upstreamDataSource;
            dataSpec = new DataSpec(this.uri, this.readPosition, this.bytesRemaining, this.key, this.flags);
        } else if (span.isCached) {
            Uri fileUri = Uri.fromFile(span.file);
            long filePosition = this.readPosition - span.position;
            length = span.length - filePosition;
            if (this.bytesRemaining != -1) {
                length = Math.min(length, this.bytesRemaining);
            }
            dataSpec = new DataSpec(fileUri, this.readPosition, filePosition, length, this.key, this.flags);
            this.currentDataSource = this.cacheReadDataSource;
        } else {
            DataSource dataSource;
            this.lockedSpan = span;
            if (span.isOpenEnded()) {
                length = this.bytesRemaining;
            } else {
                length = span.length;
                if (this.bytesRemaining != -1) {
                    length = Math.min(length, this.bytesRemaining);
                }
            }
            DataSpec dataSpec2 = new DataSpec(this.uri, this.readPosition, length, this.key, this.flags);
            if (this.cacheWriteDataSource != null) {
                dataSource = this.cacheWriteDataSource;
            } else {
                dataSource = this.upstreamDataSource;
            }
            this.currentDataSource = dataSource;
        }
        this.currentRequestUnbounded = dataSpec.length == -1;
        boolean successful = false;
        long currentBytesRemaining = 0;
        try {
            currentBytesRemaining = this.currentDataSource.open(dataSpec);
            successful = true;
        } catch (IOException e2) {
            IOException e3 = e2;
            if (!initial && this.currentRequestUnbounded) {
                Throwable cause = e3;
                while (cause != null) {
                    if ((cause instanceof DataSourceException) && ((DataSourceException) cause).reason == 0) {
                        e3 = null;
                        break;
                    }
                    cause = cause.getCause();
                }
            }
            if (e3 != null) {
                throw e3;
            }
        }
        if (this.currentRequestUnbounded && currentBytesRemaining != -1) {
            this.bytesRemaining = currentBytesRemaining;
            if (this.lockedSpan != null) {
                setContentLength(dataSpec.position + this.bytesRemaining);
            }
        }
        return successful;
    }

    private void setContentLength(long length) {
        if (!this.cache.setContentLength(this.key, length)) {
            Log.e(TAG, "cache.setContentLength(" + length + ") failed. cache.getContentLength() = " + this.cache.getContentLength(this.key));
        }
    }

    private void handleBeforeThrow(IOException exception) {
        if (this.currentDataSource == this.cacheReadDataSource || (exception instanceof CacheDataSinkException)) {
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
