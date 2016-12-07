package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzaot<T> {
    public abstract void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) throws IOException;

    public abstract T zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException;

    public final zzaoh zzco(T t) {
        try {
            zzaqa com_google_android_gms_internal_zzapp = new zzapp();
            zza(com_google_android_gms_internal_zzapp, t);
            return com_google_android_gms_internal_zzapp.br();
        } catch (Throwable e) {
            throw new zzaoi(e);
        }
    }
}
