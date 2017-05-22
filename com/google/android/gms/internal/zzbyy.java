package com.google.android.gms.internal;

final class zzbyy {
    static zzbyx zzcyc;
    static long zzcye;

    private zzbyy() {
    }

    static zzbyx zzafZ() {
        synchronized (zzbyy.class) {
            if (zzcyc != null) {
                zzbyx com_google_android_gms_internal_zzbyx = zzcyc;
                zzcyc = com_google_android_gms_internal_zzbyx.zzcyc;
                com_google_android_gms_internal_zzbyx.zzcyc = null;
                zzcye -= 8192;
                return com_google_android_gms_internal_zzbyx;
            }
            return new zzbyx();
        }
    }

    static void zzb(zzbyx com_google_android_gms_internal_zzbyx) {
        if (com_google_android_gms_internal_zzbyx.zzcyc != null || com_google_android_gms_internal_zzbyx.zzcyd != null) {
            throw new IllegalArgumentException();
        } else if (!com_google_android_gms_internal_zzbyx.zzcya) {
            synchronized (zzbyy.class) {
                if (zzcye + 8192 > 65536) {
                    return;
                }
                zzcye += 8192;
                com_google_android_gms_internal_zzbyx.zzcyc = zzcyc;
                com_google_android_gms_internal_zzbyx.limit = 0;
                com_google_android_gms_internal_zzbyx.pos = 0;
                zzcyc = com_google_android_gms_internal_zzbyx;
            }
        }
    }
}
