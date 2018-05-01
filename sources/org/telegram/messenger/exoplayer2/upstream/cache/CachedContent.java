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
    private boolean locked;

    public CachedContent(DataInputStream dataInputStream) throws IOException {
        this(dataInputStream.readInt(), dataInputStream.readUTF(), dataInputStream.readLong());
    }

    public CachedContent(int i, String str, long j) {
        this.id = i;
        this.key = str;
        this.length = j;
        this.cachedSpans = new TreeSet();
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.id);
        dataOutputStream.writeUTF(this.key);
        dataOutputStream.writeLong(this.length);
    }

    public long getLength() {
        return this.length;
    }

    public void setLength(long j) {
        this.length = j;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean z) {
        this.locked = z;
    }

    public void addSpan(SimpleCacheSpan simpleCacheSpan) {
        this.cachedSpans.add(simpleCacheSpan);
    }

    public TreeSet<SimpleCacheSpan> getSpans() {
        return this.cachedSpans;
    }

    public SimpleCacheSpan getSpan(long j) {
        SimpleCacheSpan createLookup = SimpleCacheSpan.createLookup(this.key, j);
        SimpleCacheSpan simpleCacheSpan = (SimpleCacheSpan) this.cachedSpans.floor(createLookup);
        if (simpleCacheSpan != null && simpleCacheSpan.position + simpleCacheSpan.length > j) {
            return simpleCacheSpan;
        }
        createLookup = (SimpleCacheSpan) this.cachedSpans.ceiling(createLookup);
        if (createLookup == null) {
            j = SimpleCacheSpan.createOpenHole(this.key, j);
        } else {
            j = SimpleCacheSpan.createClosedHole(this.key, j, createLookup.position - j);
        }
        return j;
    }

    public long getCachedBytesLength(long j, long j2) {
        long j3 = j2;
        SimpleCacheSpan span = getSpan(j);
        if (span.isHoleSpan()) {
            return -Math.min(span.isOpenEnded() ? Long.MAX_VALUE : span.length, j3);
        }
        long j4 = j + j3;
        long j5 = span.position + span.length;
        if (j5 < j4) {
            for (SimpleCacheSpan simpleCacheSpan : this.cachedSpans.tailSet(span, false)) {
                if (simpleCacheSpan.position > j5) {
                    break;
                }
                j5 = Math.max(j5, simpleCacheSpan.position + simpleCacheSpan.length);
                if (j5 >= j4) {
                    break;
                }
                CachedContent cachedContent = this;
            }
        }
        return Math.min(j5 - j, j3);
    }

    public SimpleCacheSpan touch(SimpleCacheSpan simpleCacheSpan) throws CacheException {
        Assertions.checkState(this.cachedSpans.remove(simpleCacheSpan));
        SimpleCacheSpan copyWithUpdatedLastAccessTime = simpleCacheSpan.copyWithUpdatedLastAccessTime(this.id);
        if (simpleCacheSpan.file.renameTo(copyWithUpdatedLastAccessTime.file)) {
            this.cachedSpans.add(copyWithUpdatedLastAccessTime);
            return copyWithUpdatedLastAccessTime;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Renaming of ");
        stringBuilder.append(simpleCacheSpan.file);
        stringBuilder.append(" to ");
        stringBuilder.append(copyWithUpdatedLastAccessTime.file);
        stringBuilder.append(" failed.");
        throw new CacheException(stringBuilder.toString());
    }

    public boolean isEmpty() {
        return this.cachedSpans.isEmpty();
    }

    public boolean removeSpan(CacheSpan cacheSpan) {
        if (!this.cachedSpans.remove(cacheSpan)) {
            return null;
        }
        cacheSpan.file.delete();
        return true;
    }

    public int headerHashCode() {
        return (31 * ((this.id * 31) + this.key.hashCode())) + ((int) (this.length ^ (this.length >>> 32)));
    }
}
