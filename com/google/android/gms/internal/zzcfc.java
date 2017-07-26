package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;

public final class zzcfc<V> {
    private final String zzBN;
    private final V zzahV;
    private final zzbez<V> zzahW;

    private zzcfc(String str, zzbez<V> com_google_android_gms_internal_zzbez_V, V v) {
        zzbo.zzu(com_google_android_gms_internal_zzbez_V);
        this.zzahW = com_google_android_gms_internal_zzbez_V;
        this.zzahV = v;
        this.zzBN = str;
    }

    static zzcfc<Long> zzb(String str, long j, long j2) {
        return new zzcfc(str, zzbez.zza(str, Long.valueOf(j2)), Long.valueOf(j));
    }

    static zzcfc<Boolean> zzb(String str, boolean z, boolean z2) {
        return new zzcfc(str, zzbez.zzg(str, z2), Boolean.valueOf(z));
    }

    static zzcfc<String> zzj(String str, String str2, String str3) {
        return new zzcfc(str, zzbez.zzv(str, str3), str2);
    }

    static zzcfc<Integer> zzm(String str, int i, int i2) {
        return new zzcfc(str, zzbez.zza(str, Integer.valueOf(i2)), Integer.valueOf(i));
    }

    public final V get() {
        return this.zzahV;
    }

    public final V get(V v) {
        return v != null ? v : this.zzahV;
    }

    public final String getKey() {
        return this.zzBN;
    }
}
