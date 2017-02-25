package com.google.android.gms.internal;

public class zzn<T> {
    public final T result;
    public final com.google.android.gms.internal.zzb.zza zzaf;
    public final zzs zzag;
    public boolean zzah;

    public interface zza {
        void zze(zzs com_google_android_gms_internal_zzs);
    }

    public interface zzb<T> {
        void zzb(T t);
    }

    private zzn(zzs com_google_android_gms_internal_zzs) {
        this.zzah = false;
        this.result = null;
        this.zzaf = null;
        this.zzag = com_google_android_gms_internal_zzs;
    }

    private zzn(T t, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        this.zzah = false;
        this.result = t;
        this.zzaf = com_google_android_gms_internal_zzb_zza;
        this.zzag = null;
    }

    public static <T> zzn<T> zza(T t, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        return new zzn(t, com_google_android_gms_internal_zzb_zza);
    }

    public static <T> zzn<T> zzd(zzs com_google_android_gms_internal_zzs) {
        return new zzn(com_google_android_gms_internal_zzs);
    }

    public boolean isSuccess() {
        return this.zzag == null;
    }
}
