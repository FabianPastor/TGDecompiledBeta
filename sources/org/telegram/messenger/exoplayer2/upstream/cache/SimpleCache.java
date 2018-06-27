package org.telegram.messenger.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.Listener;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SimpleCache implements Cache {
    private static final String TAG = "SimpleCache";
    private static boolean cacheFolderLockingDisabled;
    private static final HashSet<File> lockedCacheDirs = new HashSet();
    private final File cacheDir;
    private final CacheEvictor evictor;
    private final CachedContentIndex index;
    private final HashMap<String, ArrayList<Listener>> listeners;
    private boolean released;
    private long totalSpace;

    public static synchronized boolean isCacheFolderLocked(File cacheFolder) {
        boolean contains;
        synchronized (SimpleCache.class) {
            contains = lockedCacheDirs.contains(cacheFolder.getAbsoluteFile());
        }
        return contains;
    }

    @Deprecated
    public static synchronized void disableCacheFolderLocking() {
        synchronized (SimpleCache.class) {
            cacheFolderLockingDisabled = true;
            lockedCacheDirs.clear();
        }
    }

    public SimpleCache(File cacheDir, CacheEvictor evictor) {
        this(cacheDir, evictor, null, false);
    }

    public SimpleCache(File cacheDir, CacheEvictor evictor, byte[] secretKey) {
        this(cacheDir, evictor, secretKey, secretKey != null);
    }

    public SimpleCache(File cacheDir, CacheEvictor evictor, byte[] secretKey, boolean encrypt) {
        this(cacheDir, evictor, new CachedContentIndex(cacheDir, secretKey, encrypt));
    }

    SimpleCache(File cacheDir, CacheEvictor evictor, CachedContentIndex index) {
        if (lockFolder(cacheDir)) {
            this.cacheDir = cacheDir;
            this.evictor = evictor;
            this.index = index;
            this.listeners = new HashMap();
            final ConditionVariable conditionVariable = new ConditionVariable();
            new Thread("SimpleCache.initialize()") {
                public void run() {
                    synchronized (SimpleCache.this) {
                        conditionVariable.open();
                        SimpleCache.this.initialize();
                        SimpleCache.this.evictor.onCacheInitialized();
                    }
                }
            }.start();
            conditionVariable.block();
            return;
        }
        throw new IllegalStateException("Another SimpleCache instance uses the folder: " + cacheDir);
    }

    public synchronized void release() throws CacheException {
        if (!this.released) {
            this.listeners.clear();
            try {
                removeStaleSpansAndCachedContents();
            } finally {
                unlockFolder(this.cacheDir);
                this.released = true;
            }
        }
    }

    public synchronized NavigableSet<CacheSpan> addListener(String key, Listener listener) {
        Assertions.checkState(!this.released);
        ArrayList<Listener> listenersForKey = (ArrayList) this.listeners.get(key);
        if (listenersForKey == null) {
            listenersForKey = new ArrayList();
            this.listeners.put(key, listenersForKey);
        }
        listenersForKey.add(listener);
        return getCachedSpans(key);
    }

    public synchronized void removeListener(String key, Listener listener) {
        if (!this.released) {
            ArrayList<Listener> listenersForKey = (ArrayList) this.listeners.get(key);
            if (listenersForKey != null) {
                listenersForKey.remove(listener);
                if (listenersForKey.isEmpty()) {
                    this.listeners.remove(key);
                }
            }
        }
    }

    public synchronized NavigableSet<CacheSpan> getCachedSpans(String key) {
        NavigableSet<CacheSpan> treeSet;
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.index.get(key);
        if (cachedContent == null || cachedContent.isEmpty()) {
            treeSet = new TreeSet();
        } else {
            treeSet = new TreeSet(cachedContent.getSpans());
        }
        return treeSet;
    }

    public synchronized Set<String> getKeys() {
        Assertions.checkState(!this.released);
        return new HashSet(this.index.getKeys());
    }

    public synchronized long getCacheSpace() {
        Assertions.checkState(!this.released);
        return this.totalSpace;
    }

    public synchronized SimpleCacheSpan startReadWrite(String key, long position) throws InterruptedException, CacheException {
        SimpleCacheSpan span;
        while (true) {
            span = startReadWriteNonBlocking(key, position);
            if (span == null) {
                wait();
            }
        }
        return span;
    }

    public synchronized SimpleCacheSpan startReadWriteNonBlocking(String key, long position) throws CacheException {
        SimpleCacheSpan newCacheSpan;
        boolean z = true;
        synchronized (this) {
            if (this.released) {
                z = false;
            }
            Assertions.checkState(z);
            SimpleCacheSpan cacheSpan = getSpan(key, position);
            if (cacheSpan.isCached) {
                newCacheSpan = this.index.get(key).touch(cacheSpan);
                notifySpanTouched(cacheSpan, newCacheSpan);
            } else {
                CachedContent cachedContent = this.index.getOrAdd(key);
                if (cachedContent.isLocked()) {
                    newCacheSpan = null;
                } else {
                    cachedContent.setLocked(true);
                    newCacheSpan = cacheSpan;
                }
            }
        }
        return newCacheSpan;
    }

    public synchronized File startFile(String key, long position, long maxLength) throws CacheException {
        CachedContent cachedContent;
        Assertions.checkState(!this.released);
        cachedContent = this.index.get(key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
            removeStaleSpansAndCachedContents();
        }
        this.evictor.onStartFile(this, key, position, maxLength);
        return SimpleCacheSpan.getCacheFile(this.cacheDir, cachedContent.id, position, System.currentTimeMillis());
    }

    public synchronized void commitFile(File file) throws CacheException {
        boolean z = true;
        synchronized (this) {
            boolean z2;
            if (this.released) {
                z2 = false;
            } else {
                z2 = true;
            }
            Assertions.checkState(z2);
            SimpleCacheSpan span = SimpleCacheSpan.createCacheEntry(file, this.index);
            if (span != null) {
                z2 = true;
            } else {
                z2 = false;
            }
            Assertions.checkState(z2);
            CachedContent cachedContent = this.index.get(span.key);
            Assertions.checkNotNull(cachedContent);
            Assertions.checkState(cachedContent.isLocked());
            if (file.exists()) {
                if (file.length() == 0) {
                    file.delete();
                } else {
                    long length = ContentMetadataInternal.getContentLength(cachedContent.getMetadata());
                    if (length != -1) {
                        if (span.position + span.length > length) {
                            z = false;
                        }
                        Assertions.checkState(z);
                    }
                    addSpan(span);
                    this.index.store();
                    notifyAll();
                }
            }
        }
    }

    public synchronized void releaseHoleSpan(CacheSpan holeSpan) {
        boolean z = false;
        synchronized (this) {
            if (!this.released) {
                z = true;
            }
            Assertions.checkState(z);
            CachedContent cachedContent = this.index.get(holeSpan.key);
            Assertions.checkNotNull(cachedContent);
            Assertions.checkState(cachedContent.isLocked());
            cachedContent.setLocked(false);
            this.index.maybeRemove(cachedContent.key);
            notifyAll();
        }
    }

    public synchronized void removeSpan(CacheSpan span) throws CacheException {
        boolean z = true;
        synchronized (this) {
            if (this.released) {
                z = false;
            }
            Assertions.checkState(z);
            removeSpan(span, true);
        }
    }

    public synchronized boolean isCached(String key, long position, long length) {
        boolean z = true;
        synchronized (this) {
            boolean z2;
            if (this.released) {
                z2 = false;
            } else {
                z2 = true;
            }
            Assertions.checkState(z2);
            CachedContent cachedContent = this.index.get(key);
            if (cachedContent == null || cachedContent.getCachedBytesLength(position, length) < length) {
                z = false;
            }
        }
        return z;
    }

    public synchronized long getCachedLength(String key, long position, long length) {
        CachedContent cachedContent;
        Assertions.checkState(!this.released);
        cachedContent = this.index.get(key);
        return cachedContent != null ? cachedContent.getCachedBytesLength(position, length) : -length;
    }

    public synchronized void setContentLength(String key, long length) throws CacheException {
        ContentMetadataMutations mutations = new ContentMetadataMutations();
        ContentMetadataInternal.setContentLength(mutations, length);
        applyContentMetadataMutations(key, mutations);
    }

    public synchronized long getContentLength(String key) {
        return ContentMetadataInternal.getContentLength(getContentMetadata(key));
    }

    public synchronized void applyContentMetadataMutations(String key, ContentMetadataMutations mutations) throws CacheException {
        Assertions.checkState(!this.released);
        this.index.applyContentMetadataMutations(key, mutations);
        this.index.store();
    }

    public synchronized ContentMetadata getContentMetadata(String key) {
        Assertions.checkState(!this.released);
        return this.index.getContentMetadata(key);
    }

    private SimpleCacheSpan getSpan(String key, long position) throws CacheException {
        CachedContent cachedContent = this.index.get(key);
        if (cachedContent == null) {
            return SimpleCacheSpan.createOpenHole(key, position);
        }
        while (true) {
            SimpleCacheSpan span = cachedContent.getSpan(position);
            if (!span.isCached || span.file.exists()) {
                return span;
            }
            removeStaleSpansAndCachedContents();
        }
    }

    private void initialize() {
        if (this.cacheDir.exists()) {
            this.index.load();
            File[] files = this.cacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.getName().equals(CachedContentIndex.FILE_NAME)) {
                        SimpleCacheSpan span = file.length() > 0 ? SimpleCacheSpan.createCacheEntry(file, this.index) : null;
                        if (span != null) {
                            addSpan(span);
                        } else {
                            file.delete();
                        }
                    }
                }
                this.index.removeEmpty();
                try {
                    this.index.store();
                    return;
                } catch (CacheException e) {
                    Log.e(TAG, "Storing index file failed", e);
                    return;
                }
            }
            return;
        }
        this.cacheDir.mkdirs();
    }

    private void addSpan(SimpleCacheSpan span) {
        this.index.getOrAdd(span.key).addSpan(span);
        this.totalSpace += span.length;
        notifySpanAdded(span);
    }

    private void removeSpan(CacheSpan span, boolean removeEmptyCachedContent) throws CacheException {
        CachedContent cachedContent = this.index.get(span.key);
        if (cachedContent != null && cachedContent.removeSpan(span)) {
            this.totalSpace -= span.length;
            if (removeEmptyCachedContent) {
                try {
                    this.index.maybeRemove(cachedContent.key);
                    this.index.store();
                } catch (Throwable th) {
                    notifySpanRemoved(span);
                }
            }
            notifySpanRemoved(span);
        }
    }

    private void removeStaleSpansAndCachedContents() throws CacheException {
        ArrayList<CacheSpan> spansToBeRemoved = new ArrayList();
        for (CachedContent cachedContent : this.index.getAll()) {
            Iterator it = cachedContent.getSpans().iterator();
            while (it.hasNext()) {
                CacheSpan span = (CacheSpan) it.next();
                if (!span.file.exists()) {
                    spansToBeRemoved.add(span);
                }
            }
        }
        for (int i = 0; i < spansToBeRemoved.size(); i++) {
            removeSpan((CacheSpan) spansToBeRemoved.get(i), false);
        }
        this.index.removeEmpty();
        this.index.store();
    }

    private void notifySpanRemoved(CacheSpan span) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanRemoved(this, span);
            }
        }
        this.evictor.onSpanRemoved(this, span);
    }

    private void notifySpanAdded(SimpleCacheSpan span) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanAdded(this, span);
            }
        }
        this.evictor.onSpanAdded(this, span);
    }

    private void notifySpanTouched(SimpleCacheSpan oldSpan, CacheSpan newSpan) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(oldSpan.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanTouched(this, oldSpan, newSpan);
            }
        }
        this.evictor.onSpanTouched(this, oldSpan, newSpan);
    }

    private static synchronized boolean lockFolder(File cacheDir) {
        boolean z;
        synchronized (SimpleCache.class) {
            if (cacheFolderLockingDisabled) {
                z = true;
            } else {
                z = lockedCacheDirs.add(cacheDir.getAbsoluteFile());
            }
        }
        return z;
    }

    private static synchronized void unlockFolder(File cacheDir) {
        synchronized (SimpleCache.class) {
            if (!cacheFolderLockingDisabled) {
                lockedCacheDirs.remove(cacheDir.getAbsoluteFile());
            }
        }
    }
}
