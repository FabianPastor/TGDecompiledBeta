package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;

public final class zzcfb<V> {
    private final String zzBP;
    private final V zzahV;
    private final zzbey<V> zzahW;

    private zzcfb(String str, zzbey<V> com_google_android_gms_internal_zzbey_V, V v) {
        zzbo.zzu(com_google_android_gms_internal_zzbey_V);
        this.zzahW = com_google_android_gms_internal_zzbey_V;
        this.zzahV = v;
        this.zzBP = str;
    }

    static zzcfb<Long> zzb(String str, long j, long j2) {
        return new zzcfb(str, zzbey.zza(str, Long.valueOf(j2)), Long.valueOf(j));
    }

    static zzcfb<Boolean> zzb(String str, boolean z, boolean z2) {
        return new zzcfb(str, zzbey.zzg(str, z2), Boolean.valueOf(z));
    }

    static zzcfb<String> zzj(String str, String str2, String str3) {
        return new zzcfb(str, zzbey.zzv(str, str3), str2);
    }

    static zzcfb<Integer> zzm(String str, int i, int i2) {
        return new zzcfb(str, zzbey.zza(str, Integer.valueOf(i2)), Integer.valueOf(i));
    }

    public final V get() {
        return this.zzahV;
    }

    public final V get(V v) {
        return v != null ? v : this.zzahV;
    }

    public final String getKey() {
        return this.zzBP;
    }
}
