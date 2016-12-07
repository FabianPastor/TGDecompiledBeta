package com.google.android.gms.internal;

import com.google.android.gms.internal.zzaps.zza;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class zzapv<T> extends zzaot<T> {
    private final zzaot<T> bkU;
    private final zzaob bmQ;
    private final Type bmR;

    zzapv(zzaob com_google_android_gms_internal_zzaob, zzaot<T> com_google_android_gms_internal_zzaot_T, Type type) {
        this.bmQ = com_google_android_gms_internal_zzaob;
        this.bkU = com_google_android_gms_internal_zzaot_T;
        this.bmR = type;
    }

    private Type zzb(Type type, Object obj) {
        return obj != null ? (type == Object.class || (type instanceof TypeVariable) || (type instanceof Class)) ? obj.getClass() : type : type;
    }

    public void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) throws IOException {
        zzaot com_google_android_gms_internal_zzaot = this.bkU;
        Type zzb = zzb(this.bmR, t);
        if (zzb != this.bmR) {
            com_google_android_gms_internal_zzaot = this.bmQ.zza(zzapx.zzl(zzb));
            if ((com_google_android_gms_internal_zzaot instanceof zza) && !(this.bkU instanceof zza)) {
                com_google_android_gms_internal_zzaot = this.bkU;
            }
        }
        com_google_android_gms_internal_zzaot.zza(com_google_android_gms_internal_zzaqa, t);
    }

    public T zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        return this.bkU.zzb(com_google_android_gms_internal_zzapy);
    }
}
