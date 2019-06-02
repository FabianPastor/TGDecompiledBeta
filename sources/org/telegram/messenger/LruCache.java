package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LruCache<T> {
    private final LinkedHashMap<String, T> map;
    private final LinkedHashMap<String, ArrayList<String>> mapFilters;
    private int maxSize;
    private int size;

    /* Access modifiers changed, original: protected */
    public void entryRemoved(boolean z, String str, T t, T t2) {
    }

    /* Access modifiers changed, original: protected */
    public int sizeOf(String str, T t) {
        return 1;
    }

    public LruCache(int i) {
        if (i > 0) {
            this.maxSize = i;
            this.map = new LinkedHashMap(0, 0.75f, true);
            this.mapFilters = new LinkedHashMap();
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }

    public final T get(String str) {
        if (str != null) {
            synchronized (this) {
                Object obj = this.map.get(str);
                if (obj != null) {
                    return obj;
                }
                return null;
            }
        }
        throw new NullPointerException("key == null");
    }

    public ArrayList<String> getFilterKeys(String str) {
        ArrayList arrayList = (ArrayList) this.mapFilters.get(str);
        return arrayList != null ? new ArrayList(arrayList) : null;
    }

    public T put(String str, T t) {
        if (str == null || t == null) {
            throw new NullPointerException("key == null || value == null");
        }
        Object put;
        synchronized (this) {
            this.size += safeSizeOf(str, t);
            put = this.map.put(str, t);
            if (put != null) {
                this.size -= safeSizeOf(str, put);
            }
        }
        String[] split = str.split("@");
        if (split.length > 1) {
            ArrayList arrayList = (ArrayList) this.mapFilters.get(split[0]);
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
        synchronized (this) {
            Iterator it = this.map.entrySet().iterator();
            while (it.hasNext() && this.size > i) {
                if (this.map.isEmpty()) {
                    break;
                }
                Entry entry = (Entry) it.next();
                String str2 = (String) entry.getKey();
                if (str == null || !str.equals(str2)) {
                    Object value = entry.getValue();
                    this.size -= safeSizeOf(str2, value);
                    it.remove();
                    String[] split = str2.split("@");
                    if (split.length > 1) {
                        ArrayList arrayList = (ArrayList) this.mapFilters.get(split[0]);
                        if (arrayList != null) {
                            arrayList.remove(split[1]);
                            if (arrayList.isEmpty()) {
                                this.mapFilters.remove(split[0]);
                            }
                        }
                    }
                    entryRemoved(true, str2, value, null);
                }
            }
        }
    }

    public final T remove(String str) {
        if (str != null) {
            Object remove;
            synchronized (this) {
                remove = this.map.remove(str);
                if (remove != null) {
                    this.size -= safeSizeOf(str, remove);
                }
            }
            if (remove != null) {
                String[] split = str.split("@");
                if (split.length > 1) {
                    ArrayList arrayList = (ArrayList) this.mapFilters.get(split[0]);
                    if (arrayList != null) {
                        arrayList.remove(split[1]);
                        if (arrayList.isEmpty()) {
                            this.mapFilters.remove(split[0]);
                        }
                    }
                }
                entryRemoved(false, str, remove, null);
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Negative size: ");
        stringBuilder.append(str);
        stringBuilder.append("=");
        stringBuilder.append(t);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public final void evictAll() {
        trimToSize(-1, null);
    }

    public final synchronized int size() {
        return this.size;
    }

    public final synchronized int maxSize() {
        return this.maxSize;
    }
}
