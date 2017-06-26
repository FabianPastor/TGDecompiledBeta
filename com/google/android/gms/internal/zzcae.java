package com.google.android.gms.internal;

public final class zzcae {
    private static zzcae zzaXH;
    private final zzbzz zzaXI = new zzbzz();
    private final zzcaa zzaXJ = new zzcaa();

    static {
        zzcae com_google_android_gms_internal_zzcae = new zzcae();
        synchronized (zzcae.class) {
            zzaXH = com_google_android_gms_internal_zzcae;
        }
    }

    private zzcae() {
    }

    private static zzcae zzua() {
        zzcae com_google_android_gms_internal_zzcae;
        synchronized (zzcae.class) {
            com_google_android_gms_internal_zzcae = zzaXH;
        }
        return com_google_android_gms_internal_zzcae;
    }

    public static zzbzz zzub() {
        return zzua().zzaXI;
    }

    public static zzcaa zzuc() {
        return zzua().zzaXJ;
    }
}
