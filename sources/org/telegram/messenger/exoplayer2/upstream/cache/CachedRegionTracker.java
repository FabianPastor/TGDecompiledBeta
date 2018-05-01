package org.telegram.messenger.exoplayer2.upstream.cache;

import android.util.Log;
import java.util.Arrays;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.Listener;

public final class CachedRegionTracker implements Listener {
    public static final int CACHED_TO_END = -2;
    public static final int NOT_CACHED = -1;
    private static final String TAG = "CachedRegionTracker";
    private final Cache cache;
    private final String cacheKey;
    private final ChunkIndex chunkIndex;
    private final Region lookupRegion = new Region(0, 0);
    private final TreeSet<Region> regions = new TreeSet();

    private static class Region implements Comparable<Region> {
        public long endOffset;
        public int endOffsetIndex;
        public long startOffset;

        public Region(long j, long j2) {
            this.startOffset = j;
            this.endOffset = j2;
        }

        public int compareTo(Region region) {
            if (this.startOffset < region.startOffset) {
                return -1;
            }
            return this.startOffset == region.startOffset ? null : 1;
        }
    }

    public void onSpanTouched(Cache cache, CacheSpan cacheSpan, CacheSpan cacheSpan2) {
    }

    public CachedRegionTracker(Cache cache, String str, ChunkIndex chunkIndex) {
        this.cache = cache;
        this.cacheKey = str;
        this.chunkIndex = chunkIndex;
        synchronized (this) {
            cache = cache.addListener(str, this).descendingIterator();
            while (cache.hasNext() != null) {
                mergeSpan((CacheSpan) cache.next());
            }
        }
    }

    public void release() {
        this.cache.removeListener(this.cacheKey, this);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int getRegionEndTimeMs(long j) {
        this.lookupRegion.startOffset = j;
        Region region = (Region) this.regions.floor(this.lookupRegion);
        if (region != null && j <= region.endOffset) {
            if (region.endOffsetIndex != -1) {
                j = region.endOffsetIndex;
                if (j == this.chunkIndex.length - 1 && region.endOffset == this.chunkIndex.offsets[j] + ((long) this.chunkIndex.sizes[j])) {
                    return -2;
                }
                return (int) ((this.chunkIndex.timesUs[j] + ((this.chunkIndex.durationsUs[j] * (region.endOffset - this.chunkIndex.offsets[j])) / ((long) this.chunkIndex.sizes[j]))) / 1000);
            }
        }
    }

    public synchronized void onSpanAdded(Cache cache, CacheSpan cacheSpan) {
        mergeSpan(cacheSpan);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onSpanRemoved(Cache cache, CacheSpan cacheSpan) {
        cache = new Region(cacheSpan.position, cacheSpan.position + cacheSpan.length);
        Region region = (Region) this.regions.floor(cache);
        if (region == null) {
            Log.e(TAG, "Removed a span we were not aware of");
            return;
        }
        this.regions.remove(region);
        if (region.startOffset < cache.startOffset) {
            Region region2 = new Region(region.startOffset, cache.startOffset);
            int binarySearch = Arrays.binarySearch(this.chunkIndex.offsets, region2.endOffset);
            if (binarySearch < 0) {
                binarySearch = (-binarySearch) - 2;
            }
            region2.endOffsetIndex = binarySearch;
            this.regions.add(region2);
        }
        if (region.endOffset > cache.endOffset) {
            region2 = new Region(cache.endOffset + 1, region.endOffset);
            region2.endOffsetIndex = region.endOffsetIndex;
            this.regions.add(region2);
        }
    }

    private void mergeSpan(CacheSpan cacheSpan) {
        Region region = new Region(cacheSpan.position, cacheSpan.position + cacheSpan.length);
        Region region2 = (Region) this.regions.floor(region);
        Region region3 = (Region) this.regions.ceiling(region);
        boolean regionsConnect = regionsConnect(region2, region);
        if (regionsConnect(region, region3)) {
            if (regionsConnect) {
                region2.endOffset = region3.endOffset;
                region2.endOffsetIndex = region3.endOffsetIndex;
            } else {
                region.endOffset = region3.endOffset;
                region.endOffsetIndex = region3.endOffsetIndex;
                this.regions.add(region);
            }
            this.regions.remove(region3);
        } else if (regionsConnect) {
            region2.endOffset = region.endOffset;
            int i = region2.endOffsetIndex;
            while (i < this.chunkIndex.length - 1) {
                int i2 = i + 1;
                if (this.chunkIndex.offsets[i2] > region2.endOffset) {
                    break;
                }
                i = i2;
            }
            region2.endOffsetIndex = i;
        } else {
            cacheSpan = Arrays.binarySearch(this.chunkIndex.offsets, region.endOffset);
            if (cacheSpan < null) {
                cacheSpan = (-cacheSpan) - 2;
            }
            region.endOffsetIndex = cacheSpan;
            this.regions.add(region);
        }
    }

    private boolean regionsConnect(Region region, Region region2) {
        return (region == null || region2 == null || region.endOffset != region2.startOffset) ? null : true;
    }
}
