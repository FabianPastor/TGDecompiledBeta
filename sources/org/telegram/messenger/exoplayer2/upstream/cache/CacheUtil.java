package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;

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
            long blockLength = cache.getCachedLength(key, start, left != -1 ? left : Long.MAX_VALUE);
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
            long blockLength = cache.getCachedLength(key, start, left != -1 ? left : Long.MAX_VALUE);
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

    private static long readAndDiscard(org.telegram.messenger.exoplayer2.upstream.DataSpec r21, long r22, long r24, org.telegram.messenger.exoplayer2.upstream.DataSource r26, byte[] r27, org.telegram.messenger.exoplayer2.util.PriorityTaskManager r28, int r29, org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.CachingCounters r30) throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r21_1 'dataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec) in PHI: PHI: (r21_3 'dataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec) = (r21_1 'dataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec), (r21_2 'dataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec) binds: {(r21_1 'dataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:57:0x0016, (r21_2 'dataSpec' org.telegram.messenger.exoplayer2.upstream.DataSpec)=B:56:0x0016}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = r21;
    L_0x0002:
        if (r28 == 0) goto L_0x0007;
    L_0x0004:
        r28.proceed(r29);
    L_0x0007:
        r3 = java.lang.Thread.interrupted();	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        if (r3 == 0) goto L_0x001c;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
    L_0x000d:
        r3 = new java.lang.InterruptedException;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r3.<init>();	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        throw r3;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
    L_0x0013:
        r3 = move-exception;
        r21 = r2;
    L_0x0016:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r26);
        r2 = r21;
        goto L_0x0002;
    L_0x001c:
        r21 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r4 = r2.uri;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r5 = r2.postBody;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r6 = r2.position;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r6 = r6 + r22;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r8 = r2.absoluteStreamPosition;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r8 = r6 - r8;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r10 = -1;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r12 = r2.key;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r3 = r2.flags;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r13 = r3 | 2;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r3 = r21;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r6 = r22;	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r3.<init>(r4, r5, r6, r8, r10, r12, r13);	 Catch:{ PriorityTooLowException -> 0x0013, all -> 0x00bb }
        r0 = r26;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r1 = r21;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r16 = r0.open(r1);	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r0 = r30;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r4 = r0.contentLength;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r6 = -1;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        if (r3 != 0) goto L_0x005b;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x004b:
        r4 = -1;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3 = (r16 > r4 ? 1 : (r16 == r4 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        if (r3 == 0) goto L_0x005b;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x0051:
        r0 = r21;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r4 = r0.absoluteStreamPosition;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r4 = r4 + r16;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r0 = r30;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r0.contentLength = r4;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x005b:
        r18 = 0;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x005d:
        r3 = (r18 > r24 ? 1 : (r18 == r24 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        if (r3 == 0) goto L_0x00a0;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x0061:
        r3 = java.lang.Thread.interrupted();	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        if (r3 == 0) goto L_0x006f;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x0067:
        r3 = new java.lang.InterruptedException;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3.<init>();	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        throw r3;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x006d:
        r3 = move-exception;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        goto L_0x0016;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x006f:
        r4 = 0;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r6 = -1;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3 = (r24 > r6 ? 1 : (r24 == r6 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        if (r3 == 0) goto L_0x00a4;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x0076:
        r0 = r27;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3 = r0.length;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r6 = (long) r3;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r8 = r24 - r18;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r6 = java.lang.Math.min(r6, r8);	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3 = (int) r6;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x0081:
        r0 = r26;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r1 = r27;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r14 = r0.read(r1, r4, r3);	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3 = -1;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        if (r14 != r3) goto L_0x00a8;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x008c:
        r0 = r30;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r4 = r0.contentLength;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r6 = -1;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        if (r3 != 0) goto L_0x00a0;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x0096:
        r0 = r21;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r4 = r0.absoluteStreamPosition;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r4 = r4 + r18;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r0 = r30;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r0.contentLength = r4;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x00a0:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r26);
        return r18;
    L_0x00a4:
        r0 = r27;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r3 = r0.length;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        goto L_0x0081;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
    L_0x00a8:
        r4 = (long) r14;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r18 = r18 + r4;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r0 = r30;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r4 = r0.newlyCachedBytes;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r6 = (long) r14;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r4 = r4 + r6;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r0 = r30;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        r0.newlyCachedBytes = r4;	 Catch:{ PriorityTooLowException -> 0x006d, all -> 0x00b6 }
        goto L_0x005d;
    L_0x00b6:
        r3 = move-exception;
    L_0x00b7:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r26);
        throw r3;
    L_0x00bb:
        r3 = move-exception;
        r21 = r2;
        goto L_0x00b7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.readAndDiscard(org.telegram.messenger.exoplayer2.upstream.DataSpec, long, long, org.telegram.messenger.exoplayer2.upstream.DataSource, byte[], org.telegram.messenger.exoplayer2.util.PriorityTaskManager, int, org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil$CachingCounters):long");
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
