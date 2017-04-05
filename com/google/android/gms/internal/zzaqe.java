package com.google.android.gms.internal;

public final class zzaqe {
    private static zzaqe zzaXl;
    private final zzaqb zzaXm = new zzaqb();
    private final zzaqc zzaXn = new zzaqc();

    static {
        zza(new zzaqe());
    }

    private zzaqe() {
    }

    private static zzaqe zzDD() {
        zzaqe com_google_android_gms_internal_zzaqe;
        synchronized (zzaqe.class) {
            com_google_android_gms_internal_zzaqe = zzaXl;
        }
        return com_google_android_gms_internal_zzaqe;
    }

    public static zzaqb zzDE() {
        return zzDD().zzaXm;
    }

    public static zzaqc zzDF() {
        return zzDD().zzaXn;
    }

    protected static void zza(zzaqe com_google_android_gms_internal_zzaqe) {
        synchronized (zzaqe.class) {
            zzaXl = com_google_android_gms_internal_zzaqe;
        }
    }
}
