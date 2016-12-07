package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzapk<T> {
    public abstract void zza(zzaqr com_google_android_gms_internal_zzaqr, T t) throws IOException;

    public abstract T zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException;

    public final zzaoy zzcn(T t) {
        try {
            zzaqr com_google_android_gms_internal_zzaqg = new zzaqg();
            zza(com_google_android_gms_internal_zzaqg, t);
            return com_google_android_gms_internal_zzaqg.bu();
        } catch (Throwable e) {
            throw new zzaoz(e);
        }
    }
}
