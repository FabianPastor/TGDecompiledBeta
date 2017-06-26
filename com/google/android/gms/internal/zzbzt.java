package com.google.android.gms.internal;

public abstract class zzbzt<T> {
    private final int zzBO;
    private final String zzBP;
    private final T zzBQ;

    private zzbzt(int i, String str, T t) {
        this.zzBO = i;
        this.zzBP = str;
        this.zzBQ = t;
        zzcae.zzub().zza(this);
    }

    public static zzbzv zzb(int i, String str, Boolean bool) {
        return new zzbzv(0, str, bool);
    }

    public static zzbzw zzb(int i, String str, int i2) {
        return new zzbzw(0, str, Integer.valueOf(i2));
    }

    public static zzbzx zzb(int i, String str, long j) {
        return new zzbzx(0, str, Long.valueOf(j));
    }

    public final String getKey() {
        return this.zzBP;
    }

    public final int getSource() {
        return this.zzBO;
    }

    protected abstract T zza(zzcab com_google_android_gms_internal_zzcab);

    public final T zzdI() {
        return this.zzBQ;
    }
}
