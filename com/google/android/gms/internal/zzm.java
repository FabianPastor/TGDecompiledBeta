package com.google.android.gms.internal;

public class zzm<T> {
    public final T result;
    public final com.google.android.gms.internal.zzb.zza zzae;
    public final zzr zzaf;
    public boolean zzag;

    public interface zza {
        void zze(zzr com_google_android_gms_internal_zzr);
    }

    public interface zzb<T> {
        void zzb(T t);
    }

    private zzm(zzr com_google_android_gms_internal_zzr) {
        this.zzag = false;
        this.result = null;
        this.zzae = null;
        this.zzaf = com_google_android_gms_internal_zzr;
    }

    private zzm(T t, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        this.zzag = false;
        this.result = t;
        this.zzae = com_google_android_gms_internal_zzb_zza;
        this.zzaf = null;
    }

    public static <T> zzm<T> zza(T t, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        return new zzm(t, com_google_android_gms_internal_zzb_zza);
    }

    public static <T> zzm<T> zzd(zzr com_google_android_gms_internal_zzr) {
        return new zzm(com_google_android_gms_internal_zzr);
    }

    public boolean isSuccess() {
        return this.zzaf == null;
    }
}
