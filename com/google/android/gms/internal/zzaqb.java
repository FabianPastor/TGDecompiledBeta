package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class zzaqb<E> extends zzapk<Object> {
    public static final zzapl bpG = new zzapl() {
        public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
            Type bC = com_google_android_gms_internal_zzaqo_T.bC();
            if (!(bC instanceof GenericArrayType) && (!(bC instanceof Class) || !((Class) bC).isArray())) {
                return null;
            }
            bC = zzapr.zzh(bC);
            return new zzaqb(com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzaos.zza(zzaqo.zzl(bC)), zzapr.zzf(bC));
        }
    };
    private final Class<E> bpH;
    private final zzapk<E> bpI;

    public zzaqb(zzaos com_google_android_gms_internal_zzaos, zzapk<E> com_google_android_gms_internal_zzapk_E, Class<E> cls) {
        this.bpI = new zzaqm(com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzapk_E, cls);
        this.bpH = cls;
    }

    public void zza(zzaqr com_google_android_gms_internal_zzaqr, Object obj) throws IOException {
        if (obj == null) {
            com_google_android_gms_internal_zzaqr.bA();
            return;
        }
        com_google_android_gms_internal_zzaqr.bw();
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            this.bpI.zza(com_google_android_gms_internal_zzaqr, Array.get(obj, i));
        }
        com_google_android_gms_internal_zzaqr.bx();
    }

    public Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }
        List arrayList = new ArrayList();
        com_google_android_gms_internal_zzaqp.beginArray();
        while (com_google_android_gms_internal_zzaqp.hasNext()) {
            arrayList.add(this.bpI.zzb(com_google_android_gms_internal_zzaqp));
        }
        com_google_android_gms_internal_zzaqp.endArray();
        Object newInstance = Array.newInstance(this.bpH, arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            Array.set(newInstance, i, arrayList.get(i));
        }
        return newInstance;
    }
}
