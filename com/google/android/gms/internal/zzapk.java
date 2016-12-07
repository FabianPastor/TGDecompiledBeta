package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class zzapk<E> extends zzaot<Object> {
    public static final zzaou bmp = new zzaou() {
        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            Type bz = com_google_android_gms_internal_zzapx_T.bz();
            if (!(bz instanceof GenericArrayType) && (!(bz instanceof Class) || !((Class) bz).isArray())) {
                return null;
            }
            bz = zzapa.zzh(bz);
            return new zzapk(com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzaob.zza(zzapx.zzl(bz)), zzapa.zzf(bz));
        }
    };
    private final Class<E> bmq;
    private final zzaot<E> bmr;

    public zzapk(zzaob com_google_android_gms_internal_zzaob, zzaot<E> com_google_android_gms_internal_zzaot_E, Class<E> cls) {
        this.bmr = new zzapv(com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzaot_E, cls);
        this.bmq = cls;
    }

    public void zza(zzaqa com_google_android_gms_internal_zzaqa, Object obj) throws IOException {
        if (obj == null) {
            com_google_android_gms_internal_zzaqa.bx();
            return;
        }
        com_google_android_gms_internal_zzaqa.bt();
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            this.bmr.zza(com_google_android_gms_internal_zzaqa, Array.get(obj, i));
        }
        com_google_android_gms_internal_zzaqa.bu();
    }

    public Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
        List arrayList = new ArrayList();
        com_google_android_gms_internal_zzapy.beginArray();
        while (com_google_android_gms_internal_zzapy.hasNext()) {
            arrayList.add(this.bmr.zzb(com_google_android_gms_internal_zzapy));
        }
        com_google_android_gms_internal_zzapy.endArray();
        Object newInstance = Array.newInstance(this.bmq, arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            Array.set(newInstance, i, arrayList.get(i));
        }
        return newInstance;
    }
}
