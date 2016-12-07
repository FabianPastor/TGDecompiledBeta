package org.telegram.messenger.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import android.util.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.Listener;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SimpleCache implements Cache {
    private final File cacheDir;
    private final HashMap<String, Pair<Long, TreeSet<CacheSpan>>> cachedSpans;
    private final CacheEvictor evictor;
    private final HashMap<String, ArrayList<Listener>> listeners;
    private final HashMap<String, CacheSpan> lockedSpans;
    private long totalSpace = 0;

    public SimpleCache(File cacheDir, CacheEvictor evictor) {
        this.cacheDir = cacheDir;
        this.evictor = evictor;
        this.lockedSpans = new HashMap();
        this.cachedSpans = new HashMap();
        this.listeners = new HashMap();
        final ConditionVariable conditionVariable = new ConditionVariable();
        new Thread("SimpleCache.initialize()") {
            public void run() {
                synchronized (SimpleCache.this) {
                    conditionVariable.open();
                    SimpleCache.this.initialize();
                }
            }
        }.start();
        conditionVariable.block();
    }

    public synchronized NavigableSet<CacheSpan> addListener(String key, Listener listener) {
        ArrayList<Listener> listenersForKey = (ArrayList) this.listeners.get(key);
        if (listenersForKey == null) {
            listenersForKey = new ArrayList();
            this.listeners.put(key, listenersForKey);
        }
        listenersForKey.add(listener);
        return getCachedSpans(key);
    }

    public synchronized void removeListener(String key, Listener listener) {
        ArrayList<Listener> listenersForKey = (ArrayList) this.listeners.get(key);
        if (listenersForKey != null) {
            listenersForKey.remove(listener);
            if (listenersForKey.isEmpty()) {
                this.listeners.remove(key);
            }
        }
    }

    public synchronized NavigableSet<CacheSpan> getCachedSpans(String key) {
        TreeSet<CacheSpan> spansForKey;
        spansForKey = getSpansForKey(key);
        return spansForKey == null ? null : new TreeSet(spansForKey);
    }

    public synchronized Set<String> getKeys() {
        return new HashSet(this.cachedSpans.keySet());
    }

    public synchronized long getCacheSpace() {
        return this.totalSpace;
    }

    public synchronized CacheSpan startReadWrite(String key, long position) throws InterruptedException {
        CacheSpan span;
        CacheSpan lookupSpan = CacheSpan.createLookup(key, position);
        while (true) {
            span = startReadWriteNonBlocking(lookupSpan);
            if (span == null) {
                wait();
            }
        }
        return span;
    }

    public synchronized CacheSpan startReadWriteNonBlocking(String key, long position) {
        return startReadWriteNonBlocking(CacheSpan.createLookup(key, position));
    }

    private synchronized CacheSpan startReadWriteNonBlocking(CacheSpan lookupSpan) {
        CacheSpan newCacheSpan;
        CacheSpan cacheSpan = getSpan(lookupSpan);
        if (cacheSpan.isCached) {
            newCacheSpan = cacheSpan.touch();
            replaceSpan(cacheSpan, newCacheSpan);
            notifySpanTouched(cacheSpan, newCacheSpan);
        } else if (this.lockedSpans.containsKey(lookupSpan.key)) {
            newCacheSpan = null;
        } else {
            this.lockedSpans.put(lookupSpan.key, cacheSpan);
            newCacheSpan = cacheSpan;
        }
        return newCacheSpan;
    }

    public synchronized File startFile(String key, long position, long maxLength) {
        Assertions.checkState(this.lockedSpans.containsKey(key));
        if (!this.cacheDir.exists()) {
            removeStaleSpans();
            this.cacheDir.mkdirs();
        }
        this.evictor.onStartFile(this, key, position, maxLength);
        return CacheSpan.getCacheFileName(this.cacheDir, key, position, System.currentTimeMillis());
    }

    public synchronized void commitFile(File file) {
        boolean z = true;
        synchronized (this) {
            CacheSpan span = CacheSpan.createCacheEntry(file);
            Assertions.checkState(span != null);
            Assertions.checkState(this.lockedSpans.containsKey(span.key));
            if (file.exists()) {
                if (file.length() == 0) {
                    file.delete();
                } else {
                    Long length = Long.valueOf(getContentLength(span.key));
                    if (length.longValue() != -1) {
                        if (span.position + span.length > length.longValue()) {
                            z = false;
                        }
                        Assertions.checkState(z);
                    }
                    addSpan(span);
                    notifyAll();
                }
            }
        }
    }

    public synchronized void releaseHoleSpan(CacheSpan holeSpan) {
        Assertions.checkState(holeSpan == this.lockedSpans.remove(holeSpan.key));
        notifyAll();
    }

    private CacheSpan getSpan(CacheSpan lookupSpan) {
        String key = lookupSpan.key;
        long offset = lookupSpan.position;
        TreeSet<CacheSpan> entries = getSpansForKey(key);
        if (entries == null) {
            return CacheSpan.createOpenHole(key, lookupSpan.position);
        }
        CacheSpan floorSpan = (CacheSpan) entries.floor(lookupSpan);
        if (floorSpan == null || floorSpan.position > offset || offset >= floorSpan.position + floorSpan.length) {
            CacheSpan createOpenHole;
            CacheSpan ceilEntry = (CacheSpan) entries.ceiling(lookupSpan);
            if (ceilEntry == null) {
                createOpenHole = CacheSpan.createOpenHole(key, lookupSpan.position);
            } else {
                createOpenHole = CacheSpan.createClosedHole(key, lookupSpan.position, ceilEntry.position - lookupSpan.position);
            }
            return createOpenHole;
        } else if (floorSpan.file.exists()) {
            return floorSpan;
        } else {
            removeStaleSpans();
            return getSpan(lookupSpan);
        }
    }

    private void initialize() {
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
        }
        File[] files = this.cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File file2;
                if (file2.length() == 0) {
                    file2.delete();
                } else {
                    file2 = CacheSpan.upgradeIfNeeded(file2);
                    CacheSpan span = CacheSpan.createCacheEntry(file2);
                    if (span == null) {
                        file2.delete();
                    } else {
                        addSpan(span);
                    }
                }
            }
            this.evictor.onCacheInitialized();
        }
    }

    private void addSpan(CacheSpan span) {
        TreeSet<CacheSpan> spansForKey;
        Pair<Long, TreeSet<CacheSpan>> entryForKey = (Pair) this.cachedSpans.get(span.key);
        if (entryForKey == null) {
            spansForKey = new TreeSet();
            setKeyValue(span.key, -1, spansForKey);
        } else {
            spansForKey = (TreeSet) entryForKey.second;
        }
        spansForKey.add(span);
        this.totalSpace += span.length;
        notifySpanAdded(span);
    }

    public synchronized void removeSpan(CacheSpan span) {
        TreeSet<CacheSpan> spansForKey = getSpansForKey(span.key);
        this.totalSpace -= span.length;
        Assertions.checkState(spansForKey.remove(span));
        span.file.delete();
        if (spansForKey.isEmpty()) {
            this.cachedSpans.remove(span.key);
        }
        notifySpanRemoved(span);
    }

    private void removeStaleSpans() {
        Iterator<Entry<String, Pair<Long, TreeSet<CacheSpan>>>> iterator = this.cachedSpans.entrySet().iterator();
        while (iterator.hasNext()) {
            Iterator<CacheSpan> spanIterator = ((TreeSet) ((Pair) ((Entry) iterator.next()).getValue()).second).iterator();
            boolean isEmpty = true;
            while (spanIterator.hasNext()) {
                CacheSpan span = (CacheSpan) spanIterator.next();
                if (span.file.exists()) {
                    isEmpty = false;
                } else {
                    spanIterator.remove();
                    if (span.isCached) {
                        this.totalSpace -= span.length;
                    }
                    notifySpanRemoved(span);
                }
            }
            if (isEmpty) {
                iterator.remove();
            }
        }
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

    private void notifySpanAdded(CacheSpan span) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanAdded(this, span);
            }
        }
        this.evictor.onSpanAdded(this, span);
    }

    private void notifySpanTouched(CacheSpan oldSpan, CacheSpan newSpan) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(oldSpan.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanTouched(this, oldSpan, newSpan);
            }
        }
        this.evictor.onSpanTouched(this, oldSpan, newSpan);
    }

    public synchronized boolean isCached(String key, long position, long length) {
        boolean z;
        TreeSet<CacheSpan> entries = getSpansForKey(key);
        if (entries == null) {
            z = false;
        } else {
            CacheSpan floorSpan = (CacheSpan) entries.floor(CacheSpan.createLookup(key, position));
            if (floorSpan == null || floorSpan.position + floorSpan.length <= position) {
                z = false;
            } else {
                long queryEndPosition = position + length;
                long currentEndPosition = floorSpan.position + floorSpan.length;
                if (currentEndPosition >= queryEndPosition) {
                    z = true;
                } else {
                    for (CacheSpan next : entries.tailSet(floorSpan, false)) {
                        if (next.position <= currentEndPosition) {
                            currentEndPosition = Math.max(currentEndPosition, next.position + next.length);
                            if (currentEndPosition >= queryEndPosition) {
                                z = true;
                                break;
                            }
                        }
                        z = false;
                        break;
                    }
                    z = false;
                }
            }
        }
        return z;
    }

    public synchronized boolean setContentLength(String key, long length) {
        boolean z;
        Pair<Long, TreeSet<CacheSpan>> entryForKey = (Pair) this.cachedSpans.get(key);
        TreeSet<CacheSpan> entries;
        if (entryForKey != null) {
            entries = entryForKey.second;
            if (!(entries == null || entries.isEmpty())) {
                CacheSpan last = (CacheSpan) entries.last();
                if (last.position + last.length > length) {
                    z = false;
                }
            }
            setKeyValue(key, length, entries);
            z = true;
        } else {
            entries = new TreeSet();
            setKeyValue(key, length, entries);
            z = true;
        }
        return z;
    }

    public synchronized long getContentLength(String key) {
        Pair<Long, TreeSet<CacheSpan>> entryForKey;
        entryForKey = (Pair) this.cachedSpans.get(key);
        return entryForKey == null ? -1 : ((Long) entryForKey.first).longValue();
    }

    private TreeSet<CacheSpan> getSpansForKey(String key) {
        Pair<Long, TreeSet<CacheSpan>> entryForKey = (Pair) this.cachedSpans.get(key);
        return entryForKey != null ? (TreeSet) entryForKey.second : null;
    }

    private void setKeyValue(String key, long length, TreeSet<CacheSpan> entries) {
        this.cachedSpans.put(key, Pair.create(Long.valueOf(length), entries));
    }

    private void replaceSpan(CacheSpan oldSpan, CacheSpan newSpan) {
        TreeSet<CacheSpan> spansForKey = getSpansForKey(oldSpan.key);
        Assertions.checkState(spansForKey.remove(oldSpan));
        spansForKey.add(newSpan);
    }
}
