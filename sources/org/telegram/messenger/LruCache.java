package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<T> {
    private final LinkedHashMap<String, T> map;
    private final LinkedHashMap<String, ArrayList<String>> mapFilters;
    private int maxSize;
    private int size;

    public LruCache(int maxSize2) {
        if (maxSize2 > 0) {
            this.maxSize = maxSize2;
            this.map = new LinkedHashMap<>(0, 0.75f, true);
            this.mapFilters = new LinkedHashMap<>();
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }

    public final T get(String key) {
        if (key != null) {
            synchronized (this) {
                T mapValue = this.map.get(key);
                if (mapValue != null) {
                    return mapValue;
                }
                return null;
            }
        }
        throw new NullPointerException("key == null");
    }

    public ArrayList<String> getFilterKeys(String key) {
        ArrayList<String> arr = this.mapFilters.get(key);
        if (arr != null) {
            return new ArrayList<>(arr);
        }
        return null;
    }

    public void moveToFront(String key) {
        T value = this.map.remove(key);
        if (value != null) {
            this.map.put(key, value);
        }
    }

    public T put(String key, T value) {
        T previous;
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            this.size += safeSizeOf(key, value);
            previous = this.map.put(key, value);
            if (previous != null) {
                this.size -= safeSizeOf(key, previous);
            }
        }
        String[] args = key.split("@");
        if (args.length > 1) {
            ArrayList<String> arr = this.mapFilters.get(args[0]);
            if (arr == null) {
                arr = new ArrayList<>();
                this.mapFilters.put(args[0], arr);
            }
            if (!arr.contains(args[1])) {
                arr.add(args[1]);
            }
        }
        if (previous != null) {
            entryRemoved(false, key, previous, value);
        }
        trimToSize(this.maxSize, key);
        return previous;
    }

    private void trimToSize(int maxSize2, String justAdded) {
        ArrayList<String> arr;
        synchronized (this) {
            Iterator<Map.Entry<String, T>> iterator = this.map.entrySet().iterator();
            while (true) {
                if (!iterator.hasNext() || this.size <= maxSize2) {
                    break;
                } else if (this.map.isEmpty()) {
                    break;
                } else {
                    Map.Entry<String, T> entry = iterator.next();
                    String key = entry.getKey();
                    if (justAdded == null || !justAdded.equals(key)) {
                        T value = entry.getValue();
                        this.size -= safeSizeOf(key, value);
                        iterator.remove();
                        String[] args = key.split("@");
                        if (args.length > 1 && (arr = this.mapFilters.get(args[0])) != null) {
                            arr.remove(args[1]);
                            if (arr.isEmpty()) {
                                this.mapFilters.remove(args[0]);
                            }
                        }
                        entryRemoved(true, key, value, (T) null);
                    }
                }
            }
        }
    }

    public final T remove(String key) {
        T previous;
        ArrayList<String> arr;
        if (key != null) {
            synchronized (this) {
                previous = this.map.remove(key);
                if (previous != null) {
                    this.size -= safeSizeOf(key, previous);
                }
            }
            if (previous != null) {
                String[] args = key.split("@");
                if (args.length > 1 && (arr = this.mapFilters.get(args[0])) != null) {
                    arr.remove(args[1]);
                    if (arr.isEmpty()) {
                        this.mapFilters.remove(args[0]);
                    }
                }
                entryRemoved(false, key, previous, (T) null);
            }
            return previous;
        }
        throw new NullPointerException("key == null");
    }

    public boolean contains(String key) {
        return this.map.containsKey(key);
    }

    /* access modifiers changed from: protected */
    public void entryRemoved(boolean evicted, String key, T t, T t2) {
    }

    private int safeSizeOf(String key, T value) {
        int result = sizeOf(key, value);
        if (result >= 0) {
            return result;
        }
        throw new IllegalStateException("Negative size: " + key + "=" + value);
    }

    /* access modifiers changed from: protected */
    public int sizeOf(String key, T t) {
        return 1;
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
}
