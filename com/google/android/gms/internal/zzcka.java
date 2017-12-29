package com.google.android.gms.internal;

final class zzcka implements Runnable {
    private /* synthetic */ zzcjn zzjhc;

    zzcka(zzcjn com_google_android_gms_internal_zzcjn) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
    }

    public final void run() {
        zzcjk com_google_android_gms_internal_zzcjk = this.zzjhc;
        com_google_android_gms_internal_zzcjk.zzve();
        com_google_android_gms_internal_zzcjk.zzxf();
        com_google_android_gms_internal_zzcjk.zzawy().zzazi().log("Resetting analytics data (FE)");
        com_google_android_gms_internal_zzcjk.zzawp().resetAnalyticsData();
    }
}
