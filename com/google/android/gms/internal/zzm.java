package com.google.android.gms.internal;

public class zzm<T> {
    public final T result;
    public final com.google.android.gms.internal.zzb.zza zzbf;
    public final zzr zzbg;
    public boolean zzbh;

    public interface zza {
        void zze(zzr com_google_android_gms_internal_zzr);
    }

    public interface zzb<T> {
        void zzb(T t);
    }

    private zzm(zzr com_google_android_gms_internal_zzr) {
        this.zzbh = false;
        this.result = null;
        this.zzbf = null;
        this.zzbg = com_google_android_gms_internal_zzr;
    }

    private zzm(T t, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        this.zzbh = false;
        this.result = t;
        this.zzbf = com_google_android_gms_internal_zzb_zza;
        this.zzbg = null;
    }

    public static <T> zzm<T> zza(T t, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        return new zzm(t, com_google_android_gms_internal_zzb_zza);
    }

    public static <T> zzm<T> zzd(zzr com_google_android_gms_internal_zzr) {
        return new zzm(com_google_android_gms_internal_zzr);
    }

    public boolean isSuccess() {
        return this.zzbg == null;
    }
}
