package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSourceException;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.FileDataSource;
import org.telegram.messenger.exoplayer2.upstream.TeeDataSource;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;

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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void closeCurrentSource() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0006 in list [B:6:0x0014]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:43)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
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

    private void openNextSource(boolean r27) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r4_3 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec) in PHI: PHI: (r4_1 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec) = (r4_0 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec), (r4_2 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec), (r4_3 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec), (r4_3 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec) binds: {(r4_0 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:4:0x000a, (r4_2 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:31:0x00bc, (r4_3 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:37:0x0101, (r4_3 'nextDataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:41:0x0120}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r26 = this;
        r0 = r26;
        r6 = r0.currentRequestIgnoresCache;
        if (r6 == 0) goto L_0x005c;
    L_0x0006:
        r22 = 0;
    L_0x0008:
        if (r22 != 0) goto L_0x008c;
    L_0x000a:
        r0 = r26;
        r0 = r0.upstreamDataSource;
        r21 = r0;
        r4 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;
        r0 = r26;
        r5 = r0.uri;
        r0 = r26;
        r6 = r0.readPosition;
        r0 = r26;
        r8 = r0.bytesRemaining;
        r0 = r26;
        r10 = r0.key;
        r0 = r26;
        r11 = r0.flags;
        r4.<init>(r5, r6, r8, r10, r11);
    L_0x0029:
        r0 = r26;
        r6 = r0.currentRequestIgnoresCache;
        if (r6 != 0) goto L_0x0133;
    L_0x002f:
        r0 = r26;
        r6 = r0.upstreamDataSource;
        r0 = r21;
        if (r0 != r6) goto L_0x0133;
    L_0x0037:
        r0 = r26;
        r6 = r0.readPosition;
        r12 = 102400; // 0x19000 float:1.43493E-40 double:5.05923E-319;
        r6 = r6 + r12;
    L_0x003f:
        r0 = r26;
        r0.checkCachePosition = r6;
        if (r27 == 0) goto L_0x0140;
    L_0x0045:
        r0 = r26;
        r6 = r0.currentDataSource;
        r0 = r26;
        r7 = r0.upstreamDataSource;
        if (r6 != r7) goto L_0x013a;
    L_0x004f:
        r6 = 1;
    L_0x0050:
        org.telegram.messenger.exoplayer2.util.Assertions.checkState(r6);
        r0 = r26;
        r6 = r0.upstreamDataSource;
        r0 = r21;
        if (r0 != r6) goto L_0x013d;
    L_0x005b:
        return;
    L_0x005c:
        r0 = r26;
        r6 = r0.blockOnCache;
        if (r6 == 0) goto L_0x007a;
    L_0x0062:
        r0 = r26;	 Catch:{ InterruptedException -> 0x0073 }
        r6 = r0.cache;	 Catch:{ InterruptedException -> 0x0073 }
        r0 = r26;	 Catch:{ InterruptedException -> 0x0073 }
        r7 = r0.key;	 Catch:{ InterruptedException -> 0x0073 }
        r0 = r26;	 Catch:{ InterruptedException -> 0x0073 }
        r12 = r0.readPosition;	 Catch:{ InterruptedException -> 0x0073 }
        r22 = r6.startReadWrite(r7, r12);	 Catch:{ InterruptedException -> 0x0073 }
        goto L_0x0008;
    L_0x0073:
        r20 = move-exception;
        r6 = new java.io.InterruptedIOException;
        r6.<init>();
        throw r6;
    L_0x007a:
        r0 = r26;
        r6 = r0.cache;
        r0 = r26;
        r7 = r0.key;
        r0 = r26;
        r12 = r0.readPosition;
        r22 = r6.startReadWriteNonBlocking(r7, r12);
        goto L_0x0008;
    L_0x008c:
        r0 = r22;
        r6 = r0.isCached;
        if (r6 == 0) goto L_0x00d5;
    L_0x0092:
        r0 = r22;
        r6 = r0.file;
        r5 = android.net.Uri.fromFile(r6);
        r0 = r26;
        r6 = r0.readPosition;
        r0 = r22;
        r12 = r0.position;
        r8 = r6 - r12;
        r0 = r22;
        r6 = r0.length;
        r10 = r6 - r8;
        r0 = r26;
        r6 = r0.bytesRemaining;
        r12 = -1;
        r6 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1));
        if (r6 == 0) goto L_0x00bc;
    L_0x00b4:
        r0 = r26;
        r6 = r0.bytesRemaining;
        r10 = java.lang.Math.min(r10, r6);
    L_0x00bc:
        r4 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;
        r0 = r26;
        r6 = r0.readPosition;
        r0 = r26;
        r12 = r0.key;
        r0 = r26;
        r13 = r0.flags;
        r4.<init>(r5, r6, r8, r10, r12, r13);
        r0 = r26;
        r0 = r0.cacheReadDataSource;
        r21 = r0;
        goto L_0x0029;
    L_0x00d5:
        r6 = r22.isOpenEnded();
        if (r6 == 0) goto L_0x0109;
    L_0x00db:
        r0 = r26;
        r10 = r0.bytesRemaining;
    L_0x00df:
        r4 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;
        r0 = r26;
        r13 = r0.uri;
        r0 = r26;
        r14 = r0.readPosition;
        r0 = r26;
        r0 = r0.key;
        r18 = r0;
        r0 = r26;
        r0 = r0.flags;
        r19 = r0;
        r12 = r4;
        r16 = r10;
        r12.<init>(r13, r14, r16, r18, r19);
        r0 = r26;
        r6 = r0.cacheWriteDataSource;
        if (r6 == 0) goto L_0x0120;
    L_0x0101:
        r0 = r26;
        r0 = r0.cacheWriteDataSource;
        r21 = r0;
        goto L_0x0029;
    L_0x0109:
        r0 = r22;
        r10 = r0.length;
        r0 = r26;
        r6 = r0.bytesRemaining;
        r12 = -1;
        r6 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1));
        if (r6 == 0) goto L_0x00df;
    L_0x0117:
        r0 = r26;
        r6 = r0.bytesRemaining;
        r10 = java.lang.Math.min(r10, r6);
        goto L_0x00df;
    L_0x0120:
        r0 = r26;
        r0 = r0.upstreamDataSource;
        r21 = r0;
        r0 = r26;
        r6 = r0.cache;
        r0 = r22;
        r6.releaseHoleSpan(r0);
        r22 = 0;
        goto L_0x0029;
    L_0x0133:
        r6 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        goto L_0x003f;
    L_0x013a:
        r6 = 0;
        goto L_0x0050;
    L_0x013d:
        r26.closeCurrentSource();	 Catch:{ Throwable -> 0x017c }
    L_0x0140:
        if (r22 == 0) goto L_0x014e;
    L_0x0142:
        r6 = r22.isHoleSpan();
        if (r6 == 0) goto L_0x014e;
    L_0x0148:
        r0 = r22;
        r1 = r26;
        r1.currentHoleSpan = r0;
    L_0x014e:
        r0 = r21;
        r1 = r26;
        r1.currentDataSource = r0;
        r6 = r4.length;
        r12 = -1;
        r6 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1));
        if (r6 != 0) goto L_0x018d;
    L_0x015c:
        r6 = 1;
    L_0x015d:
        r0 = r26;
        r0.currentDataSpecLengthUnset = r6;
        r0 = r21;
        r24 = r0.open(r4);
        r0 = r26;
        r6 = r0.currentDataSpecLengthUnset;
        if (r6 == 0) goto L_0x005b;
    L_0x016d:
        r6 = -1;
        r6 = (r24 > r6 ? 1 : (r24 == r6 ? 0 : -1));
        if (r6 == 0) goto L_0x005b;
    L_0x0173:
        r0 = r26;
        r1 = r24;
        r0.setBytesRemaining(r1);
        goto L_0x005b;
    L_0x017c:
        r20 = move-exception;
        r6 = r22.isHoleSpan();
        if (r6 == 0) goto L_0x018c;
    L_0x0183:
        r0 = r26;
        r6 = r0.cache;
        r0 = r22;
        r6.releaseHoleSpan(r0);
    L_0x018c:
        throw r20;
    L_0x018d:
        r6 = 0;
        goto L_0x015d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource.openNextSource(boolean):void");
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
