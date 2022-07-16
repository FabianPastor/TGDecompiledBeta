package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LruCache<T> {
    private final LinkedHashMap<String, T> map;
    private final LinkedHashMap<String, ArrayList<String>> mapFilters;
    private int maxSize;
    private int size;

    /* access modifiers changed from: protected */
    public void entryRemoved(boolean z, String str, T t, T t2) {
    }

    /* access modifiers changed from: protected */
    public int sizeOf(String str, T t) {
        return 1;
    }

    public LruCache(int i) {
        if (i > 0) {
            this.maxSize = i;
            this.map = new LinkedHashMap<>(0, 0.75f, true);
            this.mapFilters = new LinkedHashMap<>();
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }

    public final T get(String str) {
        if (str != null) {
            synchronized (this) {
                T t = this.map.get(str);
                if (t != null) {
                    return t;
                }
                return null;
            }
        }
        throw new NullPointerException("key == null");
    }

    public ArrayList<String> getFilterKeys(String str) {
        ArrayList arrayList = this.mapFilters.get(str);
        if (arrayList != null) {
            return new ArrayList<>(arrayList);
        }
        return null;
    }

    public void moveToFront(String str) {
        Object remove = this.map.remove(str);
        if (remove != null) {
            this.map.put(str, remove);
        }
    }

    public T put(String str, T t) {
        T put;
        if (str == null || t == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            this.size += safeSizeOf(str, t);
            put = this.map.put(str, t);
            if (put != null) {
                this.size -= safeSizeOf(str, put);
            }
        }
        String[] split = str.split("@");
        if (split.length > 1) {
            ArrayList arrayList = this.mapFilters.get(split[0]);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.mapFilters.put(split[0], arrayList);
            }
            if (!arrayList.contains(split[1])) {
                arrayList.add(split[1]);
            }
        }
        if (put != null) {
            entryRemoved(false, str, put, t);
        }
        trimToSize(this.maxSize, str);
        return put;
    }

    private void trimToSize(int i, String str) {
        ArrayList arrayList;
        synchronized (this) {
            Iterator<Map.Entry<String, T>> it = this.map.entrySet().iterator();
            while (true) {
                if (!it.hasNext() || this.size <= i) {
                    break;
                } else if (this.map.isEmpty()) {
                    break;
                } else {
                    Map.Entry next = it.next();
                    String str2 = (String) next.getKey();
                    if (str == null || !str.equals(str2)) {
                        Object value = next.getValue();
                        this.size -= safeSizeOf(str2, value);
                        it.remove();
                        String[] split = str2.split("@");
                        if (split.length > 1 && (arrayList = this.mapFilters.get(split[0])) != null) {
                            arrayList.remove(split[1]);
                            if (arrayList.isEmpty()) {
                                this.mapFilters.remove(split[0]);
                            }
                        }
                        entryRemoved(true, str2, value, (Object) null);
                    }
                }
            }
        }
    }

    public final T remove(String str) {
        T remove;
        ArrayList arrayList;
        if (str != null) {
            synchronized (this) {
                remove = this.map.remove(str);
                if (remove != null) {
                    this.size -= safeSizeOf(str, remove);
                }
            }
            if (remove != null) {
                String[] split = str.split("@");
                if (split.length > 1 && (arrayList = this.mapFilters.get(split[0])) != null) {
                    arrayList.remove(split[1]);
                    if (arrayList.isEmpty()) {
                        this.mapFilters.remove(split[0]);
                    }
                }
                entryRemoved(false, str, remove, (T) null);
            }
            return remove;
        }
        throw new NullPointerException("key == null");
    }

    public boolean contains(String str) {
        return this.map.containsKey(str);
    }

    private int safeSizeOf(String str, T t) {
        int sizeOf = sizeOf(str, t);
        if (sizeOf >= 0) {
            return sizeOf;
        }
        throw new IllegalStateException("Negative size: " + str + "=" + t);
    }

    public final void evictAll() {
        trimToSize(-1, (String) null);
    }

    public final synchronized int size() {
        return this.size;
    }

    public final synchronized int maxSize() {
        return this.maxSize;
    }

    public final synchronized Set<Map.Entry<String, T>> entrySet() {
        return this.map.entrySet();
    }
}
