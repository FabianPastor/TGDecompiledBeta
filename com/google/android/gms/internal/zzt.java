package com.google.android.gms.internal;

public final class zzt<T> {
    public final T result;
    public final zzc zzae;
    public final zzaa zzaf;
    public boolean zzag;

    private zzt(zzaa com_google_android_gms_internal_zzaa) {
        this.zzag = false;
        this.result = null;
        this.zzae = null;
        this.zzaf = com_google_android_gms_internal_zzaa;
    }

    private zzt(T t, zzc com_google_android_gms_internal_zzc) {
        this.zzag = false;
        this.result = t;
        this.zzae = com_google_android_gms_internal_zzc;
        this.zzaf = null;
    }

    public static <T> zzt<T> zza(T t, zzc com_google_android_gms_internal_zzc) {
        return new zzt(t, com_google_android_gms_internal_zzc);
    }

    public static <T> zzt<T> zzc(zzaa com_google_android_gms_internal_zzaa) {
        return new zzt(com_google_android_gms_internal_zzaa);
    }
}
