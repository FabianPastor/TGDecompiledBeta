package com.google.android.gms.internal;

public final class zzapr {
    private static zzapr zzaWJ;
    private final zzapo zzaWK = new zzapo();
    private final zzapp zzaWL = new zzapp();

    static {
        zza(new zzapr());
    }

    private zzapr() {
    }

    private static zzapr zzCP() {
        zzapr com_google_android_gms_internal_zzapr;
        synchronized (zzapr.class) {
            com_google_android_gms_internal_zzapr = zzaWJ;
        }
        return com_google_android_gms_internal_zzapr;
    }

    public static zzapo zzCQ() {
        return zzCP().zzaWK;
    }

    public static zzapp zzCR() {
        return zzCP().zzaWL;
    }

    protected static void zza(zzapr com_google_android_gms_internal_zzapr) {
        synchronized (zzapr.class) {
            zzaWJ = com_google_android_gms_internal_zzapr;
        }
    }
}
