package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
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

    public static void getCached(DataSpec dataSpec, Cache cache, CachingCounters cachingCounters) {
        long j;
        Cache cache2;
        DataSpec dataSpec2 = dataSpec;
        CachingCounters cachingCounters2 = cachingCounters;
        String key = getKey(dataSpec);
        long j2 = dataSpec2.absoluteStreamPosition;
        if (dataSpec2.length != -1) {
            j = dataSpec2.length;
            cache2 = cache;
        } else {
            cache2 = cache;
            j = cache2.getContentLength(key);
        }
        cachingCounters2.contentLength = j;
        cachingCounters2.alreadyCachedBytes = 0;
        cachingCounters2.newlyCachedBytes = 0;
        long j3 = j2;
        long j4 = j;
        while (j4 != 0) {
            j2 = cache2.getCachedLength(key, j3, j4 != -1 ? j4 : Long.MAX_VALUE);
            if (j2 > 0) {
                cachingCounters2.alreadyCachedBytes += j2;
            } else {
                j2 = -j2;
                if (j2 == Long.MAX_VALUE) {
                    return;
                }
            }
            j = j3 + j2;
            if (j4 == -1) {
                j2 = 0;
            }
            j3 = j;
            j4 -= j2;
        }
    }

    public static void cache(DataSpec dataSpec, Cache cache, DataSource dataSource, CachingCounters cachingCounters) throws IOException, InterruptedException {
        cache(dataSpec, cache, new CacheDataSource(cache, dataSource), new byte[131072], null, 0, cachingCounters, false);
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheDataSource cacheDataSource, byte[] bArr, PriorityTaskManager priorityTaskManager, int i, CachingCounters cachingCounters, boolean z) throws IOException, InterruptedException {
        DataSpec dataSpec2 = dataSpec;
        Cache cache2 = cache;
        CachingCounters cachingCounters2 = cachingCounters;
        Assertions.checkNotNull(cacheDataSource);
        Assertions.checkNotNull(bArr);
        if (cachingCounters2 != null) {
            getCached(dataSpec2, cache2, cachingCounters2);
        } else {
            cachingCounters2 = new CachingCounters();
        }
        CachingCounters cachingCounters3 = cachingCounters2;
        String key = getKey(dataSpec);
        long j = dataSpec2.absoluteStreamPosition;
        long contentLength = dataSpec2.length != -1 ? dataSpec2.length : cache2.getContentLength(key);
        while (contentLength != 0) {
            long j2;
            long cachedLength = cache2.getCachedLength(key, j, contentLength != -1 ? contentLength : Long.MAX_VALUE);
            if (cachedLength > 0) {
                j2 = cachedLength;
            } else {
                long j3 = -cachedLength;
                j2 = j3;
                if (readAndDiscard(dataSpec2, j, j3, cacheDataSource, bArr, priorityTaskManager, i, cachingCounters3) < j2) {
                    if (z && contentLength != -1) {
                        throw new EOFException();
                    }
                    return;
                }
            }
            long j4 = j + j2;
            if (contentLength == -1) {
                j2 = 0;
            }
            j = j4;
            contentLength -= j2;
        }
    }

    private static long readAndDiscard(org.telegram.messenger.exoplayer2.upstream.DataSpec r21, long r22, long r24, org.telegram.messenger.exoplayer2.upstream.DataSource r26, byte[] r27, org.telegram.messenger.exoplayer2.util.PriorityTaskManager r28, int r29, org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.CachingCounters r30) throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = r26;
        r3 = r27;
        r4 = r30;
        r5 = r21;
    L_0x0008:
        if (r28 == 0) goto L_0x000d;
    L_0x000a:
        r28.proceed(r29);
    L_0x000d:
        r6 = java.lang.Thread.interrupted();	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        if (r6 == 0) goto L_0x0019;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
    L_0x0013:
        r6 = new java.lang.InterruptedException;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r6.<init>();	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        throw r6;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
    L_0x0019:
        r6 = new org.telegram.messenger.exoplayer2.upstream.DataSpec;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r8 = r5.uri;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r9 = r5.postBody;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r10 = r5.position;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r12 = r10 + r22;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r10 = r5.absoluteStreamPosition;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r14 = r12 - r10;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r16 = -1;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r12 = r5.key;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r7 = r5.flags;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r18 = r7 | 2;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r7 = r6;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r10 = r22;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r19 = r12;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r12 = r14;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r14 = r16;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r16 = r19;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r17 = r18;	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r7.<init>(r8, r9, r10, r12, r14, r16, r17);	 Catch:{ PriorityTooLowException -> 0x00a7, all -> 0x00a1 }
        r7 = r2.open(r6);	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r9 = r4.contentLength;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r11 = -1;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r5 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        if (r5 != 0) goto L_0x0054;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x004a:
        r5 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        if (r5 == 0) goto L_0x0054;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x004e:
        r9 = r6.absoluteStreamPosition;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r11 = r9 + r7;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r4.contentLength = r11;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x0054:
        r7 = 0;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x0056:
        r5 = (r7 > r24 ? 1 : (r7 == r24 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        if (r5 == 0) goto L_0x009b;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x005a:
        r5 = java.lang.Thread.interrupted();	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        if (r5 == 0) goto L_0x0066;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x0060:
        r5 = new java.lang.InterruptedException;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r5.<init>();	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        throw r5;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x0066:
        r5 = 0;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r9 = -1;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r11 = (r24 > r9 ? 1 : (r24 == r9 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        if (r11 == 0) goto L_0x0077;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x006d:
        r9 = r3.length;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r9 = (long) r9;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r11 = r24 - r7;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r9 = java.lang.Math.min(r9, r11);	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r9 = (int) r9;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        goto L_0x0078;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x0077:
        r9 = r3.length;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x0078:
        r5 = r2.read(r3, r5, r9);	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r9 = -1;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        if (r5 != r9) goto L_0x008e;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x007f:
        r9 = r4.contentLength;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r11 = -1;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r5 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        if (r5 != 0) goto L_0x009b;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x0087:
        r9 = r6.absoluteStreamPosition;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r11 = r9 + r7;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r4.contentLength = r11;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        goto L_0x009b;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
    L_0x008e:
        r11 = -1;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r9 = (long) r5;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r13 = r7 + r9;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r7 = r4.newlyCachedBytes;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r11 = r7 + r9;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r4.newlyCachedBytes = r11;	 Catch:{ PriorityTooLowException -> 0x009f, all -> 0x00a1 }
        r7 = r13;
        goto L_0x0056;
    L_0x009b:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r26);
        return r7;
    L_0x009f:
        r5 = r6;
        goto L_0x00a7;
    L_0x00a1:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r26);
        throw r1;
    L_0x00a7:
        org.telegram.messenger.exoplayer2.util.Util.closeQuietly(r26);
        goto L_0x0008;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.readAndDiscard(org.telegram.messenger.exoplayer2.upstream.DataSpec, long, long, org.telegram.messenger.exoplayer2.upstream.DataSource, byte[], org.telegram.messenger.exoplayer2.util.PriorityTaskManager, int, org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil$CachingCounters):long");
    }

    public static void remove(org.telegram.messenger.exoplayer2.upstream.cache.Cache r1, java.lang.String r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = r1.getCachedSpans(r2);
        r2 = r2.iterator();
    L_0x0008:
        r0 = r2.hasNext();
        if (r0 == 0) goto L_0x0018;
    L_0x000e:
        r0 = r2.next();
        r0 = (org.telegram.messenger.exoplayer2.upstream.cache.CacheSpan) r0;
        r1.removeSpan(r0);	 Catch:{ CacheException -> 0x0008 }
        goto L_0x0008;
    L_0x0018:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.CacheUtil.remove(org.telegram.messenger.exoplayer2.upstream.cache.Cache, java.lang.String):void");
    }

    private CacheUtil() {
    }
}
