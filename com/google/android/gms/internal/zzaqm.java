package com.google.android.gms.internal;

import com.google.android.gms.internal.zzaqj.zza;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class zzaqm<T> extends zzapk<T> {
    private final zzapk<T> bol;
    private final zzaos bqh;
    private final Type bqi;

    zzaqm(zzaos com_google_android_gms_internal_zzaos, zzapk<T> com_google_android_gms_internal_zzapk_T, Type type) {
        this.bqh = com_google_android_gms_internal_zzaos;
        this.bol = com_google_android_gms_internal_zzapk_T;
        this.bqi = type;
    }

    private Type zzb(Type type, Object obj) {
        return obj != null ? (type == Object.class || (type instanceof TypeVariable) || (type instanceof Class)) ? obj.getClass() : type : type;
    }

    public void zza(zzaqr com_google_android_gms_internal_zzaqr, T t) throws IOException {
        zzapk com_google_android_gms_internal_zzapk = this.bol;
        Type zzb = zzb(this.bqi, t);
        if (zzb != this.bqi) {
            com_google_android_gms_internal_zzapk = this.bqh.zza(zzaqo.zzl(zzb));
            if ((com_google_android_gms_internal_zzapk instanceof zza) && !(this.bol instanceof zza)) {
                com_google_android_gms_internal_zzapk = this.bol;
            }
        }
        com_google_android_gms_internal_zzapk.zza(com_google_android_gms_internal_zzaqr, t);
    }

    public T zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        return this.bol.zzb(com_google_android_gms_internal_zzaqp);
    }
}
