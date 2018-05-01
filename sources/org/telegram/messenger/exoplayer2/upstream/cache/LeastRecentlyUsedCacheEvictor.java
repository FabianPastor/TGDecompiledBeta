package org.telegram.messenger.exoplayer2.upstream.cache;

import java.util.Comparator;
import java.util.TreeSet;

public final class LeastRecentlyUsedCacheEvictor implements Comparator<CacheSpan>, CacheEvictor {
    private long currentSize;
    private final TreeSet<CacheSpan> leastRecentlyUsed = new TreeSet(this);
    private final long maxBytes;

    public void onCacheInitialized() {
    }

    public LeastRecentlyUsedCacheEvictor(long j) {
        this.maxBytes = j;
    }

    public void onStartFile(Cache cache, String str, long j, long j2) {
        evictCache(cache, j2);
    }

    public void onSpanAdded(Cache cache, CacheSpan cacheSpan) {
        this.leastRecentlyUsed.add(cacheSpan);
        this.currentSize += cacheSpan.length;
        evictCache(cache, 0);
    }

    public void onSpanRemoved(Cache cache, CacheSpan cacheSpan) {
        this.leastRecentlyUsed.remove(cacheSpan);
        this.currentSize -= cacheSpan.length;
    }

    public void onSpanTouched(Cache cache, CacheSpan cacheSpan, CacheSpan cacheSpan2) {
        onSpanRemoved(cache, cacheSpan);
        onSpanAdded(cache, cacheSpan2);
    }

    public int compare(CacheSpan cacheSpan, CacheSpan cacheSpan2) {
        if (cacheSpan.lastAccessTimestamp - cacheSpan2.lastAccessTimestamp == 0) {
            return cacheSpan.compareTo(cacheSpan2);
        }
        return cacheSpan.lastAccessTimestamp < cacheSpan2.lastAccessTimestamp ? -1 : true;
    }

    private void evictCache(org.telegram.messenger.exoplayer2.upstream.cache.Cache r6, long r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r5 = this;
    L_0x0000:
        r0 = r5.currentSize;
        r2 = r0 + r7;
        r0 = r5.maxBytes;
        r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r4 <= 0) goto L_0x001e;
    L_0x000a:
        r0 = r5.leastRecentlyUsed;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x001e;
    L_0x0012:
        r0 = r5.leastRecentlyUsed;	 Catch:{ CacheException -> 0x0000 }
        r0 = r0.first();	 Catch:{ CacheException -> 0x0000 }
        r0 = (org.telegram.messenger.exoplayer2.upstream.cache.CacheSpan) r0;	 Catch:{ CacheException -> 0x0000 }
        r6.removeSpan(r0);	 Catch:{ CacheException -> 0x0000 }
        goto L_0x0000;
    L_0x001e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor.evictCache(org.telegram.messenger.exoplayer2.upstream.cache.Cache, long):void");
    }
}
