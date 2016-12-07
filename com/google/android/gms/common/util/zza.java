package com.google.android.gms.common.util;

import android.support.v4.util.ArrayMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

public class zza<E> extends AbstractSet<E> {
    private final ArrayMap<E, E> zzaGJ;

    public zza() {
        this.zzaGJ = new ArrayMap();
    }

    public zza(int i) {
        this.zzaGJ = new ArrayMap(i);
    }

    public zza(Collection<E> collection) {
        this(collection.size());
        addAll(collection);
    }

    public boolean add(E e) {
        if (this.zzaGJ.containsKey(e)) {
            return false;
        }
        this.zzaGJ.put(e, e);
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        return collection instanceof zza ? zza((zza) collection) : super.addAll(collection);
    }

    public void clear() {
        this.zzaGJ.clear();
    }

    public boolean contains(Object obj) {
        return this.zzaGJ.containsKey(obj);
    }

    public Iterator<E> iterator() {
        return this.zzaGJ.keySet().iterator();
    }

    public boolean remove(Object obj) {
        if (!this.zzaGJ.containsKey(obj)) {
            return false;
        }
        this.zzaGJ.remove(obj);
        return true;
    }

    public int size() {
        return this.zzaGJ.size();
    }

    public boolean zza(zza<? extends E> com_google_android_gms_common_util_zza__extends_E) {
        int size = size();
        this.zzaGJ.putAll(com_google_android_gms_common_util_zza__extends_E.zzaGJ);
        return size() > size;
    }
}
