package com.google.android.gms.common.util;

import android.support.v4.util.ArrayMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

public class zza<E> extends AbstractSet<E> {
    private final ArrayMap<E, E> zzaHS;

    public zza() {
        this.zzaHS = new ArrayMap();
    }

    public zza(int i) {
        this.zzaHS = new ArrayMap(i);
    }

    public zza(Collection<E> collection) {
        this(collection.size());
        addAll(collection);
    }

    public boolean add(E e) {
        if (this.zzaHS.containsKey(e)) {
            return false;
        }
        this.zzaHS.put(e, e);
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        return collection instanceof zza ? zza((zza) collection) : super.addAll(collection);
    }

    public void clear() {
        this.zzaHS.clear();
    }

    public boolean contains(Object obj) {
        return this.zzaHS.containsKey(obj);
    }

    public Iterator<E> iterator() {
        return this.zzaHS.keySet().iterator();
    }

    public boolean remove(Object obj) {
        if (!this.zzaHS.containsKey(obj)) {
            return false;
        }
        this.zzaHS.remove(obj);
        return true;
    }

    public int size() {
        return this.zzaHS.size();
    }

    public boolean zza(zza<? extends E> com_google_android_gms_common_util_zza__extends_E) {
        int size = size();
        this.zzaHS.putAll(com_google_android_gms_common_util_zza__extends_E.zzaHS);
        return size() > size;
    }
}
