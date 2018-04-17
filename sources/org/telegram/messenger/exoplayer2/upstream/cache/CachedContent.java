package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class CachedContent {
    private final TreeSet<SimpleCacheSpan> cachedSpans;
    public final int id;
    public final String key;
    private long length;
    private boolean locked;

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

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void addSpan(SimpleCacheSpan span) {
        this.cachedSpans.add(span);
    }

    public TreeSet<SimpleCacheSpan> getSpans() {
        return this.cachedSpans;
    }

    public SimpleCacheSpan getSpan(long position) {
        SimpleCacheSpan lookupSpan = SimpleCacheSpan.createLookup(this.key, position);
        SimpleCacheSpan floorSpan = (SimpleCacheSpan) this.cachedSpans.floor(lookupSpan);
        if (floorSpan != null && floorSpan.position + floorSpan.length > position) {
            return floorSpan;
        }
        SimpleCacheSpan createOpenHole;
        SimpleCacheSpan ceilSpan = (SimpleCacheSpan) this.cachedSpans.ceiling(lookupSpan);
        if (ceilSpan == null) {
            createOpenHole = SimpleCacheSpan.createOpenHole(this.key, position);
        } else {
            createOpenHole = SimpleCacheSpan.createClosedHole(this.key, position, ceilSpan.position - position);
        }
        return createOpenHole;
    }

    public long getCachedBytesLength(long position, long length) {
        long j = length;
        SimpleCacheSpan span = getSpan(position);
        if (span.isHoleSpan()) {
            return -Math.min(span.isOpenEnded() ? Long.MAX_VALUE : span.length, j);
        }
        long queryEndPosition = position + j;
        long currentEndPosition = span.position + span.length;
        if (currentEndPosition < queryEndPosition) {
            Iterator it = this.cachedSpans.tailSet(span, false).iterator();
            while (it.hasNext()) {
                SimpleCacheSpan next = (SimpleCacheSpan) it.next();
                if (next.position > currentEndPosition) {
                    break;
                }
                Iterator it2 = it;
                currentEndPosition = Math.max(currentEndPosition, next.position + next.length);
                if (currentEndPosition >= queryEndPosition) {
                    break;
                }
                it = it2;
                CachedContent cachedContent = this;
            }
        }
        return Math.min(currentEndPosition - position, j);
    }

    public SimpleCacheSpan touch(SimpleCacheSpan cacheSpan) throws CacheException {
        Assertions.checkState(this.cachedSpans.remove(cacheSpan));
        SimpleCacheSpan newCacheSpan = cacheSpan.copyWithUpdatedLastAccessTime(this.id);
        if (cacheSpan.file.renameTo(newCacheSpan.file)) {
            this.cachedSpans.add(newCacheSpan);
            return newCacheSpan;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Renaming of ");
        stringBuilder.append(cacheSpan.file);
        stringBuilder.append(" to ");
        stringBuilder.append(newCacheSpan.file);
        stringBuilder.append(" failed.");
        throw new CacheException(stringBuilder.toString());
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
        return (31 * ((31 * this.id) + this.key.hashCode())) + ((int) (this.length ^ (this.length >>> 32)));
    }
}
