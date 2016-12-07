package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class CachedContent {
    private final TreeSet<SimpleCacheSpan> cachedSpans;
    public final int id;
    public final String key;
    private long length;

    public CachedContent(DataInputStream input) throws IOException {
        this(input.readInt(), input.readUTF(), input.readLong());
    }

    public CachedContent(int id, String key, long length) {
        this.id = id;
        this.key = key;
        this.length = length;
        this.cachedSpans = new TreeSet();
    }

    public void writeToStream(DataOutputStream output) throws IOException {
        output.writeInt(this.id);
        output.writeUTF(this.key);
        output.writeLong(this.length);
    }

    public long getLength() {
        return this.length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void addSpan(SimpleCacheSpan span) {
        this.cachedSpans.add(span);
    }

    public TreeSet<SimpleCacheSpan> getSpans() {
        return this.cachedSpans;
    }

    public SimpleCacheSpan getSpan(long position) {
        SimpleCacheSpan span = getSpanInternal(position);
        if (span.isCached) {
            return span;
        }
        SimpleCacheSpan ceilEntry = (SimpleCacheSpan) this.cachedSpans.ceiling(span);
        if (ceilEntry == null) {
            return SimpleCacheSpan.createOpenHole(this.key, position);
        }
        return SimpleCacheSpan.createClosedHole(this.key, position, ceilEntry.position - position);
    }

    public boolean isCached(long position, long length) {
        SimpleCacheSpan floorSpan = getSpanInternal(position);
        if (!floorSpan.isCached) {
            return false;
        }
        long queryEndPosition = position + length;
        long currentEndPosition = floorSpan.position + floorSpan.length;
        if (currentEndPosition >= queryEndPosition) {
            return true;
        }
        for (SimpleCacheSpan next : this.cachedSpans.tailSet(floorSpan, false)) {
            if (next.position > currentEndPosition) {
                return false;
            }
            currentEndPosition = Math.max(currentEndPosition, next.position + next.length);
            if (currentEndPosition >= queryEndPosition) {
                return true;
            }
        }
        return false;
    }

    public SimpleCacheSpan touch(SimpleCacheSpan cacheSpan) throws CacheException {
        Assertions.checkState(this.cachedSpans.remove(cacheSpan));
        SimpleCacheSpan newCacheSpan = cacheSpan.copyWithUpdatedLastAccessTime(this.id);
        if (cacheSpan.file.renameTo(newCacheSpan.file)) {
            this.cachedSpans.add(newCacheSpan);
            return newCacheSpan;
        }
        throw new CacheException("Renaming of " + cacheSpan.file + " to " + newCacheSpan.file + " failed.");
    }

    public boolean isEmpty() {
        return this.cachedSpans.isEmpty();
    }

    public boolean removeSpan(CacheSpan span) {
        if (!this.cachedSpans.remove(span)) {
            return false;
        }
        span.file.delete();
        return true;
    }

    public int headerHashCode() {
        return (((this.id * 31) + this.key.hashCode()) * 31) + ((int) (this.length ^ (this.length >>> 32)));
    }

    private SimpleCacheSpan getSpanInternal(long position) {
        SimpleCacheSpan lookupSpan = SimpleCacheSpan.createLookup(this.key, position);
        SimpleCacheSpan floorSpan = (SimpleCacheSpan) this.cachedSpans.floor(lookupSpan);
        return (floorSpan == null || floorSpan.position + floorSpan.length <= position) ? lookupSpan : floorSpan;
    }
}
