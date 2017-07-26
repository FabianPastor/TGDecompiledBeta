package com.google.android.gms.internal;

public final class zzcaf {
    private static zzcaf zzaXH;
    private final zzcaa zzaXI = new zzcaa();
    private final zzcab zzaXJ = new zzcab();

    static {
        zzcaf com_google_android_gms_internal_zzcaf = new zzcaf();
        synchronized (zzcaf.class) {
            zzaXH = com_google_android_gms_internal_zzcaf;
        }
    }

    private zzcaf() {
    }

    private static zzcaf zzua() {
        zzcaf com_google_android_gms_internal_zzcaf;
        synchronized (zzcaf.class) {
            com_google_android_gms_internal_zzcaf = zzaXH;
        }
        return com_google_android_gms_internal_zzcaf;
    }

    public static zzcaa zzub() {
        return zzua().zzaXI;
    }

    public static zzcab zzuc() {
        return zzua().zzaXJ;
    }
}
