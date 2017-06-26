package com.google.android.gms.internal;

final class zzcgu implements Runnable {
    private /* synthetic */ zzcgp zzbtf;
    private /* synthetic */ zzcej zzbtg;

    zzcgu(zzcgp com_google_android_gms_internal_zzcgp, zzcej com_google_android_gms_internal_zzcej) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbtg = com_google_android_gms_internal_zzcej;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzd(this.zzbtg);
    }
}
