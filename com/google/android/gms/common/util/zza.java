package com.google.android.gms.common.util;

import android.support.v4.util.ArrayMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

public class zza<E> extends AbstractSet<E> {
    private final ArrayMap<E, E> Gp;

    public zza() {
        this.Gp = new ArrayMap();
    }

    public zza(int i) {
        this.Gp = new ArrayMap(i);
    }

    public zza(Collection<E> collection) {
        this(collection.size());
        addAll(collection);
    }

    public boolean add(E e) {
        if (this.Gp.containsKey(e)) {
            return false;
        }
        this.Gp.put(e, e);
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        return collection instanceof zza ? zza((zza) collection) : super.addAll(collection);
    }

    public void clear() {
        this.Gp.clear();
    }

    public boolean contains(Object obj) {
        return this.Gp.containsKey(obj);
    }

    public Iterator<E> iterator() {
        return this.Gp.keySet().iterator();
    }

    public boolean remove(Object obj) {
        if (!this.Gp.containsKey(obj)) {
            return false;
        }
        this.Gp.remove(obj);
        return true;
    }

    public int size() {
        return this.Gp.size();
    }

    public boolean zza(zza<? extends E> com_google_android_gms_common_util_zza__extends_E) {
        int size = size();
        this.Gp.putAll(com_google_android_gms_common_util_zza__extends_E.Gp);
        return size() > size;
    }
}
