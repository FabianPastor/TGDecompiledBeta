package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;

public final class zzchd<V> {
    private final String zzbhb;
    private final V zzdxn;
    private final zzbey<V> zzdxo;

    private zzchd(String str, zzbey<V> com_google_android_gms_internal_zzbey_V, V v) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzbey_V);
        this.zzdxo = com_google_android_gms_internal_zzbey_V;
        this.zzdxn = v;
        this.zzbhb = str;
    }

    static zzchd<Long> zzb(String str, long j, long j2) {
        return new zzchd(str, zzbey.zza(str, Long.valueOf(j2)), Long.valueOf(j));
    }

    static zzchd<Boolean> zzb(String str, boolean z, boolean z2) {
        return new zzchd(str, zzbey.zze(str, z2), Boolean.valueOf(z));
    }

    static zzchd<String> zzi(String str, String str2, String str3) {
        return new zzchd(str, zzbey.zzs(str, str3), str2);
    }

    static zzchd<Integer> zzm(String str, int i, int i2) {
        return new zzchd(str, zzbey.zza(str, Integer.valueOf(i2)), Integer.valueOf(i));
    }

    public final V get() {
        return this.zzdxn;
    }

    public final V get(V v) {
        return v != null ? v : this.zzdxn;
    }

    public final String getKey() {
        return this.zzbhb;
    }
}
