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

    public CacheDataSource(Cache cache, DataSource dataSource) {
        this(cache, dataSource, 0, DEFAULT_MAX_CACHE_FILE_SIZE);
    }

    public CacheDataSource(Cache cache, DataSource dataSource, int i) {
        this(cache, dataSource, i, DEFAULT_MAX_CACHE_FILE_SIZE);
    }

    public CacheDataSource(Cache cache, DataSource dataSource, int i, long j) {
        this(cache, dataSource, new FileDataSource(), new CacheDataSink(cache, j), i, null);
    }

    public CacheDataSource(Cache cache, DataSource dataSource, DataSource dataSource2, DataSink dataSink, int i, EventListener eventListener) {
        this.cache = cache;
        this.cacheReadDataSource = dataSource2;
        dataSource2 = null;
        this.blockOnCache = (i & 1) != null ? 1 : null;
        this.ignoreCacheOnError = (i & 2) != null ? 1 : null;
        if ((i & 4) != null) {
            dataSource2 = 1;
        }
        this.ignoreCacheForUnsetLengthRequests = dataSource2;
        this.upstreamDataSource = dataSource;
        if (dataSink != null) {
            this.cacheWriteDataSource = new TeeDataSource(dataSource, dataSink);
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
        } catch (DataSpec dataSpec2) {
            handleBeforeThrow(dataSpec2);
            throw dataSpec2;
        }
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (i2 == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (this.readPosition >= this.checkCachePosition) {
                openNextSource(true);
            }
            int read = this.currentDataSource.read(bArr, i, i2);
            if (read != -1) {
                if (this.currentDataSource == this.cacheReadDataSource) {
                    this.totalCachedBytesRead += (long) read;
                }
                long j = (long) read;
                this.readPosition += j;
                if (this.bytesRemaining != -1) {
                    this.bytesRemaining -= j;
                }
            } else if (this.currentDataSpecLengthUnset) {
                setBytesRemaining(0);
            } else {
                if (this.bytesRemaining <= 0) {
                    if (this.bytesRemaining == -1) {
                    }
                }
                closeCurrentSource();
                openNextSource(false);
                return read(bArr, i, i2);
            }
            return read;
        } catch (byte[] bArr2) {
            if (this.currentDataSpecLengthUnset == 0 || isCausedByPositionOutOfRange(bArr2) == 0) {
                handleBeforeThrow(bArr2);
                throw bArr2;
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

    private void openNextSource(boolean r20) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r6_8 org.telegram.messenger.exoplayer2.upstream.DataSpec) in PHI: PHI: (r6_11 org.telegram.messenger.exoplayer2.upstream.DataSpec) = (r6_0 org.telegram.messenger.exoplayer2.upstream.DataSpec), (r6_4 org.telegram.messenger.exoplayer2.upstream.DataSpec), (r6_8 org.telegram.messenger.exoplayer2.upstream.DataSpec), (r6_8 org.telegram.messenger.exoplayer2.upstream.DataSpec) binds: {(r6_0 org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:13:0x002c, (r6_4 org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:20:0x0063, (r6_8 org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:30:0x00ac, (r6_8 org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:31:0x00af}
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
        r19 = this;
        r1 = r19;
        r2 = r1.currentRequestIgnoresCache;
        r3 = 0;
        if (r2 == 0) goto L_0x0009;
    L_0x0007:
        r2 = r3;
        goto L_0x0028;
    L_0x0009:
        r2 = r1.blockOnCache;
        if (r2 == 0) goto L_0x001e;
    L_0x000d:
        r2 = r1.cache;	 Catch:{ InterruptedException -> 0x0018 }
        r4 = r1.key;	 Catch:{ InterruptedException -> 0x0018 }
        r5 = r1.readPosition;	 Catch:{ InterruptedException -> 0x0018 }
        r2 = r2.startReadWrite(r4, r5);	 Catch:{ InterruptedException -> 0x0018 }
        goto L_0x0028;
    L_0x0018:
        r2 = new java.io.InterruptedIOException;
        r2.<init>();
        throw r2;
    L_0x001e:
        r2 = r1.cache;
        r4 = r1.key;
        r5 = r1.readPosition;
        r2 = r2.startReadWriteNonBlocking(r4, r5);
    L_0x0028:
        r4 = -1;
        if (r2 != 0) goto L_0x0040;
    L_0x002c:
        r3 = r1.upstreamDataSource;
        r14 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;
        r7 = r1.uri;
        r8 = r1.readPosition;
        r10 = r1.bytesRemaining;
        r12 = r1.key;
        r13 = r1.flags;
        r6 = r14;
        r6.<init>(r7, r8, r10, r12, r13);
        goto L_0x00b8;
    L_0x0040:
        r6 = r2.isCached;
        if (r6 == 0) goto L_0x0081;
    L_0x0044:
        r3 = r2.file;
        r7 = android.net.Uri.fromFile(r3);
        r8 = r1.readPosition;
        r10 = r2.position;
        r12 = r8 - r10;
        r8 = r2.length;
        r10 = r8 - r12;
        r8 = r1.bytesRemaining;
        r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r3 == 0) goto L_0x0062;
    L_0x005a:
        r8 = r1.bytesRemaining;
        r8 = java.lang.Math.min(r10, r8);
        r14 = r8;
        goto L_0x0063;
    L_0x0062:
        r14 = r10;
    L_0x0063:
        r3 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;
        r8 = r1.readPosition;
        r10 = r1.key;
        r11 = r1.flags;
        r6 = r3;
        r16 = r10;
        r17 = r11;
        r10 = r12;
        r12 = r14;
        r14 = r16;
        r15 = r17;
        r6.<init>(r7, r8, r10, r12, r14, r15);
        r6 = r1.cacheReadDataSource;
        r18 = r6;
        r6 = r3;
        r3 = r18;
        goto L_0x00b8;
    L_0x0081:
        r6 = r2.isOpenEnded();
        if (r6 == 0) goto L_0x008b;
    L_0x0087:
        r6 = r1.bytesRemaining;
    L_0x0089:
        r12 = r6;
        goto L_0x009a;
    L_0x008b:
        r6 = r2.length;
        r8 = r1.bytesRemaining;
        r10 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r10 == 0) goto L_0x0089;
    L_0x0093:
        r8 = r1.bytesRemaining;
        r6 = java.lang.Math.min(r6, r8);
        goto L_0x0089;
    L_0x009a:
        r6 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;
        r9 = r1.uri;
        r10 = r1.readPosition;
        r14 = r1.key;
        r15 = r1.flags;
        r8 = r6;
        r8.<init>(r9, r10, r12, r14, r15);
        r7 = r1.cacheWriteDataSource;
        if (r7 == 0) goto L_0x00af;
    L_0x00ac:
        r3 = r1.cacheWriteDataSource;
        goto L_0x00b8;
    L_0x00af:
        r7 = r1.upstreamDataSource;
        r8 = r1.cache;
        r8.releaseHoleSpan(r2);
        r2 = r3;
        r3 = r7;
    L_0x00b8:
        r7 = r1.currentRequestIgnoresCache;
        if (r7 != 0) goto L_0x00c8;
    L_0x00bc:
        r7 = r1.upstreamDataSource;
        if (r3 != r7) goto L_0x00c8;
    L_0x00c0:
        r7 = r1.readPosition;
        r9 = 102400; // 0x19000 float:1.43493E-40 double:5.05923E-319;
        r11 = r7 + r9;
        goto L_0x00cd;
    L_0x00c8:
        r11 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
    L_0x00cd:
        r1.checkCachePosition = r11;
        r7 = 0;
        r8 = 1;
        if (r20 == 0) goto L_0x00f5;
    L_0x00d3:
        r9 = r1.currentDataSource;
        r10 = r1.upstreamDataSource;
        if (r9 != r10) goto L_0x00db;
    L_0x00d9:
        r9 = r8;
        goto L_0x00dc;
    L_0x00db:
        r9 = r7;
    L_0x00dc:
        org.telegram.messenger.exoplayer2.util.Assertions.checkState(r9);
        r9 = r1.upstreamDataSource;
        if (r3 != r9) goto L_0x00e4;
    L_0x00e3:
        return;
    L_0x00e4:
        r19.closeCurrentSource();	 Catch:{ Throwable -> 0x00e8 }
        goto L_0x00f5;
    L_0x00e8:
        r0 = move-exception;
        r3 = r2.isHoleSpan();
        if (r3 == 0) goto L_0x00f4;
    L_0x00ef:
        r3 = r1.cache;
        r3.releaseHoleSpan(r2);
    L_0x00f4:
        throw r0;
    L_0x00f5:
        if (r2 == 0) goto L_0x00ff;
    L_0x00f7:
        r9 = r2.isHoleSpan();
        if (r9 == 0) goto L_0x00ff;
    L_0x00fd:
        r1.currentHoleSpan = r2;
    L_0x00ff:
        r1.currentDataSource = r3;
        r9 = r6.length;
        r2 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x0108;
    L_0x0107:
        r7 = r8;
    L_0x0108:
        r1.currentDataSpecLengthUnset = r7;
        r2 = r3.open(r6);
        r6 = r1.currentDataSpecLengthUnset;
        if (r6 == 0) goto L_0x0119;
    L_0x0112:
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 == 0) goto L_0x0119;
    L_0x0116:
        r1.setBytesRemaining(r2);
    L_0x0119:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSource.openNextSource(boolean):void");
    }

    private static boolean isCausedByPositionOutOfRange(IOException iOException) {
        while (iOException != null) {
            if ((iOException instanceof DataSourceException) && ((DataSourceException) iOException).reason == 0) {
                return true;
            }
            iOException = iOException.getCause();
        }
        return null;
    }

    private void setBytesRemaining(long j) throws IOException {
        this.bytesRemaining = j;
        if (isWritingToCache()) {
            this.cache.setContentLength(this.key, this.readPosition + j);
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

    private void handleBeforeThrow(IOException iOException) {
        if (this.currentDataSource == this.cacheReadDataSource || (iOException instanceof CacheException) != null) {
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
