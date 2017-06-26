package com.google.android.gms.common.util;

import android.support.v4.util.ArrayMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

@Deprecated
public final class zza<E> extends AbstractSet<E> {
    private final ArrayMap<E, E> zzaJC;

    public zza() {
        this.zzaJC = new ArrayMap();
    }

    public zza(int i) {
        this.zzaJC = new ArrayMap(i);
    }

    public final boolean add(E e) {
        if (this.zzaJC.containsKey(e)) {
            return false;
        }
        this.zzaJC.put(e, e);
        return true;
    }

    public final boolean addAll(Collection<? extends E> collection) {
        if (!(collection instanceof zza)) {
            return super.addAll(collection);
        }
        zza com_google_android_gms_common_util_zza = (zza) collection;
        int size = size();
        this.zzaJC.putAll(com_google_android_gms_common_util_zza.zzaJC);
        return size() > size;
    }

    public final void clear() {
        this.zzaJC.clear();
    }

    public final boolean contains(Object obj) {
        return this.zzaJC.containsKey(obj);
    }

    public final Iterator<E> iterator() {
        return this.zzaJC.keySet().iterator();
    }

    public final boolean remove(Object obj) {
        if (!this.zzaJC.containsKey(obj)) {
            return false;
        }
        this.zzaJC.remove(obj);
        return true;
    }

    public final int size() {
        return this.zzaJC.size();
    }
}
