package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class CachedContent {
    private static final int VERSION_MAX = Integer.MAX_VALUE;
    private static final int VERSION_METADATA_INTRODUCED = 2;
    private final TreeSet<SimpleCacheSpan> cachedSpans = new TreeSet();
    public final int id;
    public final String key;
    private boolean locked;
    private DefaultContentMetadata metadata = DefaultContentMetadata.EMPTY;

    public static CachedContent readFromStream(int version, DataInputStream input) throws IOException {
        CachedContent cachedContent = new CachedContent(input.readInt(), input.readUTF());
        if (version < 2) {
            long length = input.readLong();
            ContentMetadataMutations mutations = new ContentMetadataMutations();
            ContentMetadataInternal.setContentLength(mutations, length);
            cachedContent.applyMetadataMutations(mutations);
        } else {
            cachedContent.metadata = DefaultContentMetadata.readFromStream(input);
        }
        return cachedContent;
    }

    public CachedContent(int id, String key) {
        this.id = id;
        this.key = key;
    }

    public void writeToStream(DataOutputStream output) throws IOException {
        output.writeInt(this.id);
        output.writeUTF(this.key);
        this.metadata.writeToStream(output);
    }

    public ContentMetadata getMetadata() {
        return this.metadata;
    }

    public boolean applyMetadataMutations(ContentMetadataMutations mutations) {
        DefaultContentMetadata oldMetadata = this.metadata;
        this.metadata = this.metadata.copyWithMutationsApplied(mutations);
        return !this.metadata.equals(oldMetadata);
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
        SimpleCacheSpan span = getSpan(position);
        if (span.isHoleSpan()) {
            long j;
            if (span.isOpenEnded()) {
                j = Long.MAX_VALUE;
            } else {
                j = span.length;
            }
            return -Math.min(j, length);
        }
        long queryEndPosition = position + length;
        long currentEndPosition = span.position + span.length;
        if (currentEndPosition < queryEndPosition) {
            for (SimpleCacheSpan next : this.cachedSpans.tailSet(span, false)) {
                if (next.position <= currentEndPosition) {
                    currentEndPosition = Math.max(currentEndPosition, next.position + next.length);
                    if (currentEndPosition >= queryEndPosition) {
                        break;
                    }
                }
                break;
            }
        }
        return Math.min(currentEndPosition - position, length);
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

    public int headerHashCode(int version) {
        int result = (this.id * 31) + this.key.hashCode();
        if (version >= 2) {
            return (result * 31) + this.metadata.hashCode();
        }
        long length = ContentMetadataInternal.getContentLength(this.metadata);
        return (result * 31) + ((int) ((length >>> 32) ^ length));
    }

    public int hashCode() {
        return (headerHashCode(Integer.MAX_VALUE) * 31) + this.cachedSpans.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CachedContent that = (CachedContent) o;
        if (this.id == that.id && this.key.equals(that.key) && this.cachedSpans.equals(that.cachedSpans) && this.metadata.equals(that.metadata)) {
            return true;
        }
        return false;
    }
}
