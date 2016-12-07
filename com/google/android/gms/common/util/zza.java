package com.google.android.gms.common.util;

import android.support.v4.util.ArrayMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

public class zza<E> extends AbstractSet<E> {
    private final ArrayMap<E, E> EJ;

    public zza() {
        this.EJ = new ArrayMap();
    }

    public zza(int i) {
        this.EJ = new ArrayMap(i);
    }

    public zza(Collection<E> collection) {
        this(collection.size());
        addAll(collection);
    }

    public boolean add(E e) {
        if (this.EJ.containsKey(e)) {
            return false;
        }
        this.EJ.put(e, e);
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        return collection instanceof zza ? zza((zza) collection) : super.addAll(collection);
    }

    public void clear() {
        this.EJ.clear();
    }

    public boolean contains(Object obj) {
        return this.EJ.containsKey(obj);
    }

    public Iterator<E> iterator() {
        return this.EJ.keySet().iterator();
    }

    public boolean remove(Object obj) {
        if (!this.EJ.containsKey(obj)) {
            return false;
        }
        this.EJ.remove(obj);
        return true;
    }

    public int size() {
        return this.EJ.size();
    }

    public boolean zza(zza<? extends E> com_google_android_gms_common_util_zza__extends_E) {
        int size = size();
        this.EJ.putAll(com_google_android_gms_common_util_zza__extends_E.EJ);
        return size() > size;
    }
}
