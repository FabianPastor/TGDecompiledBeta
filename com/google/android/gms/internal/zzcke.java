package com.google.android.gms.internal;

final class zzcke implements Runnable {
    private /* synthetic */ zzckc zzjhz;
    private /* synthetic */ zzckf zzjia;

    zzcke(zzckc com_google_android_gms_internal_zzckc, zzckf com_google_android_gms_internal_zzckf) {
        this.zzjhz = com_google_android_gms_internal_zzckc;
        this.zzjia = com_google_android_gms_internal_zzckf;
    }

    public final void run() {
        this.zzjhz.zza(this.zzjia);
        this.zzjhz.zzjhn = null;
        this.zzjhz.zzawp().zza(null);
    }
}
