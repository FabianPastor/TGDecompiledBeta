package org.telegram.messenger;

import android.graphics.drawable.BitmapDrawable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LruCache {
    private final LinkedHashMap<String, BitmapDrawable> map;
    private final LinkedHashMap<String, ArrayList<String>> mapFilters;
    private int maxSize;
    private int size;

    protected void entryRemoved(boolean z, String str, BitmapDrawable bitmapDrawable, BitmapDrawable bitmapDrawable2) {
    }

    protected int sizeOf(String str, BitmapDrawable bitmapDrawable) {
        return 1;
    }

    public LruCache(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = i;
        this.map = new LinkedHashMap(0, 0.75f, true);
        this.mapFilters = new LinkedHashMap();
    }

    public final BitmapDrawable get(String str) {
        if (str == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) this.map.get(str);
            if (bitmapDrawable != null) {
                return bitmapDrawable;
            }
            return null;
        }
    }

    public ArrayList<String> getFilterKeys(String str) {
        ArrayList arrayList = (ArrayList) this.mapFilters.get(str);
        return arrayList != null ? new ArrayList(arrayList) : null;
    }

    public BitmapDrawable put(String str, BitmapDrawable bitmapDrawable) {
        if (str != null) {
            if (bitmapDrawable != null) {
                BitmapDrawable bitmapDrawable2;
                synchronized (this) {
                    this.size += safeSizeOf(str, bitmapDrawable);
                    bitmapDrawable2 = (BitmapDrawable) this.map.put(str, bitmapDrawable);
                    if (bitmapDrawable2 != null) {
                        this.size -= safeSizeOf(str, bitmapDrawable2);
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
                if (bitmapDrawable2 != null) {
                    entryRemoved(false, str, bitmapDrawable2, bitmapDrawable);
                }
                trimToSize(this.maxSize, str);
                return bitmapDrawable2;
            }
        }
        throw new NullPointerException("key == null || value == null");
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
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) entry.getValue();
                    this.size -= safeSizeOf(str2, bitmapDrawable);
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
                    entryRemoved(true, str2, bitmapDrawable, null);
                }
            }
        }
    }

    public final BitmapDrawable remove(String str) {
        if (str == null) {
            throw new NullPointerException("key == null");
        }
        BitmapDrawable bitmapDrawable;
        synchronized (this) {
            bitmapDrawable = (BitmapDrawable) this.map.remove(str);
            if (bitmapDrawable != null) {
                this.size -= safeSizeOf(str, bitmapDrawable);
            }
        }
        if (bitmapDrawable != null) {
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
            entryRemoved(false, str, bitmapDrawable, null);
        }
        return bitmapDrawable;
    }

    public boolean contains(String str) {
        return this.map.containsKey(str);
    }

    private int safeSizeOf(String str, BitmapDrawable bitmapDrawable) {
        int sizeOf = sizeOf(str, bitmapDrawable);
        if (sizeOf >= 0) {
            return sizeOf;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Negative size: ");
        stringBuilder.append(str);
        stringBuilder.append("=");
        stringBuilder.append(bitmapDrawable);
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
