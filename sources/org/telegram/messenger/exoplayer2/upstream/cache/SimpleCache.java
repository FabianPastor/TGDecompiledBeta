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
    private final File cacheDir;
    private final CacheEvictor evictor;
    private final CachedContentIndex index;
    private final HashMap<String, ArrayList<Listener>> listeners;
    private long totalSpace;

    public SimpleCache(File file, CacheEvictor cacheEvictor) {
        this(file, cacheEvictor, null, false);
    }

    public SimpleCache(File file, CacheEvictor cacheEvictor, byte[] bArr) {
        this(file, cacheEvictor, bArr, bArr != null);
    }

    public SimpleCache(File file, CacheEvictor cacheEvictor, byte[] bArr, boolean z) {
        this(file, cacheEvictor, new CachedContentIndex(file, bArr, z));
    }

    SimpleCache(File file, CacheEvictor cacheEvictor, CachedContentIndex cachedContentIndex) {
        this.totalSpace = 0;
        this.cacheDir = file;
        this.evictor = cacheEvictor;
        this.index = cachedContentIndex;
        this.listeners = new HashMap();
        file = new ConditionVariable();
        new Thread("SimpleCache.initialize()") {
            public void run() {
                synchronized (SimpleCache.this) {
                    file.open();
                    SimpleCache.this.initialize();
                    SimpleCache.this.evictor.onCacheInitialized();
                }
            }
        }.start();
        file.block();
    }

    public synchronized NavigableSet<CacheSpan> addListener(String str, Listener listener) {
        ArrayList arrayList = (ArrayList) this.listeners.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.listeners.put(str, arrayList);
        }
        arrayList.add(listener);
        return getCachedSpans(str);
    }

    public synchronized void removeListener(String str, Listener listener) {
        ArrayList arrayList = (ArrayList) this.listeners.get(str);
        if (arrayList != null) {
            arrayList.remove(listener);
            if (arrayList.isEmpty() != null) {
                this.listeners.remove(str);
            }
        }
    }

    public synchronized NavigableSet<CacheSpan> getCachedSpans(String str) {
        NavigableSet<CacheSpan> treeSet;
        str = this.index.get(str);
        if (str != null) {
            if (!str.isEmpty()) {
                treeSet = new TreeSet(str.getSpans());
            }
        }
        treeSet = new TreeSet();
        return treeSet;
    }

    public synchronized Set<String> getKeys() {
        return new HashSet(this.index.getKeys());
    }

    public synchronized long getCacheSpace() {
        return this.totalSpace;
    }

    public synchronized SimpleCacheSpan startReadWrite(String str, long j) throws InterruptedException, CacheException {
        SimpleCacheSpan startReadWriteNonBlocking;
        while (true) {
            startReadWriteNonBlocking = startReadWriteNonBlocking(str, j);
            if (startReadWriteNonBlocking == null) {
                wait();
            }
        }
        return startReadWriteNonBlocking;
    }

    public synchronized SimpleCacheSpan startReadWriteNonBlocking(String str, long j) throws CacheException {
        j = getSpan(str, j);
        if (j.isCached) {
            str = this.index.get(str).touch(j);
            notifySpanTouched(j, str);
            return str;
        }
        str = this.index.getOrAdd(str);
        if (str.isLocked()) {
            return null;
        }
        str.setLocked(true);
        return j;
    }

    public synchronized File startFile(String str, long j, long j2) throws CacheException {
        CachedContent cachedContent;
        cachedContent = this.index.get(str);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        if (!this.cacheDir.exists()) {
            removeStaleSpansAndCachedContents();
            this.cacheDir.mkdirs();
        }
        this.evictor.onStartFile(this, str, j, j2);
        return SimpleCacheSpan.getCacheFile(this.cacheDir, cachedContent.id, j, System.currentTimeMillis());
    }

    public synchronized void commitFile(File file) throws CacheException {
        SimpleCacheSpan createCacheEntry = SimpleCacheSpan.createCacheEntry(file, this.index);
        boolean z = false;
        Assertions.checkState(createCacheEntry != null);
        CachedContent cachedContent = this.index.get(createCacheEntry.key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        if (!file.exists()) {
            return;
        }
        if (file.length() == 0) {
            file.delete();
            return;
        }
        file = Long.valueOf(cachedContent.getLength());
        if (file.longValue() != -1) {
            if (createCacheEntry.position + createCacheEntry.length <= file.longValue()) {
                z = true;
            }
            Assertions.checkState(z);
        }
        addSpan(createCacheEntry);
        this.index.store();
        notifyAll();
    }

    public synchronized void releaseHoleSpan(CacheSpan cacheSpan) {
        cacheSpan = this.index.get(cacheSpan.key);
        Assertions.checkNotNull(cacheSpan);
        Assertions.checkState(cacheSpan.isLocked());
        cacheSpan.setLocked(false);
        notifyAll();
    }

    private SimpleCacheSpan getSpan(String str, long j) throws CacheException {
        CachedContent cachedContent = this.index.get(str);
        if (cachedContent == null) {
            return SimpleCacheSpan.createOpenHole(str, j);
        }
        while (true) {
            str = cachedContent.getSpan(j);
            if (!str.isCached || str.file.exists()) {
                return str;
            }
            removeStaleSpansAndCachedContents();
        }
        return str;
    }

    private void initialize() {
        if (this.cacheDir.exists()) {
            this.index.load();
            File[] listFiles = this.cacheDir.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (!file.getName().equals(CachedContentIndex.FILE_NAME)) {
                        SimpleCacheSpan createCacheEntry = file.length() > 0 ? SimpleCacheSpan.createCacheEntry(file, this.index) : null;
                        if (createCacheEntry != null) {
                            addSpan(createCacheEntry);
                        } else {
                            file.delete();
                        }
                    }
                }
                this.index.removeEmpty();
                try {
                    this.index.store();
                } catch (Throwable e) {
                    Log.e(TAG, "Storing index file failed", e);
                }
                return;
            }
            return;
        }
        this.cacheDir.mkdirs();
    }

    private void addSpan(SimpleCacheSpan simpleCacheSpan) {
        this.index.getOrAdd(simpleCacheSpan.key).addSpan(simpleCacheSpan);
        this.totalSpace += simpleCacheSpan.length;
        notifySpanAdded(simpleCacheSpan);
    }

    private void removeSpan(CacheSpan cacheSpan, boolean z) throws CacheException {
        CachedContent cachedContent = this.index.get(cacheSpan.key);
        if (cachedContent != null) {
            if (cachedContent.removeSpan(cacheSpan)) {
                this.totalSpace -= cacheSpan.length;
                if (z) {
                    try {
                        this.index.maybeRemove(cachedContent.key);
                        this.index.store();
                    } catch (Throwable th) {
                        notifySpanRemoved(cacheSpan);
                    }
                }
                notifySpanRemoved(cacheSpan);
            }
        }
    }

    public synchronized void removeSpan(CacheSpan cacheSpan) throws CacheException {
        removeSpan(cacheSpan, true);
    }

    private void removeStaleSpansAndCachedContents() throws CacheException {
        ArrayList arrayList = new ArrayList();
        for (CachedContent spans : this.index.getAll()) {
            Iterator it = spans.getSpans().iterator();
            while (it.hasNext()) {
                CacheSpan cacheSpan = (CacheSpan) it.next();
                if (!cacheSpan.file.exists()) {
                    arrayList.add(cacheSpan);
                }
            }
        }
        for (int i = 0; i < arrayList.size(); i++) {
            removeSpan((CacheSpan) arrayList.get(i), false);
        }
        this.index.removeEmpty();
        this.index.store();
    }

    private void notifySpanRemoved(CacheSpan cacheSpan) {
        ArrayList arrayList = (ArrayList) this.listeners.get(cacheSpan.key);
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((Listener) arrayList.get(size)).onSpanRemoved(this, cacheSpan);
            }
        }
        this.evictor.onSpanRemoved(this, cacheSpan);
    }

    private void notifySpanAdded(SimpleCacheSpan simpleCacheSpan) {
        ArrayList arrayList = (ArrayList) this.listeners.get(simpleCacheSpan.key);
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((Listener) arrayList.get(size)).onSpanAdded(this, simpleCacheSpan);
            }
        }
        this.evictor.onSpanAdded(this, simpleCacheSpan);
    }

    private void notifySpanTouched(SimpleCacheSpan simpleCacheSpan, CacheSpan cacheSpan) {
        ArrayList arrayList = (ArrayList) this.listeners.get(simpleCacheSpan.key);
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((Listener) arrayList.get(size)).onSpanTouched(this, simpleCacheSpan, cacheSpan);
            }
        }
        this.evictor.onSpanTouched(this, simpleCacheSpan, cacheSpan);
    }

    public synchronized boolean isCached(String str, long j, long j2) {
        str = this.index.get(str);
        str = (str == null || str.getCachedBytesLength(j, j2) < j2) ? null : true;
        return str;
    }

    public synchronized long getCachedLength(String str, long j, long j2) {
        str = this.index.get(str);
        return str != null ? str.getCachedBytesLength(j, j2) : -j2;
    }

    public synchronized void setContentLength(String str, long j) throws CacheException {
        this.index.setContentLength(str, j);
        this.index.store();
    }

    public synchronized long getContentLength(String str) {
        return this.index.getContentLength(str);
    }
}
