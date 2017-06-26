package com.google.android.gms.internal;

final class zzchd implements Runnable {
    private /* synthetic */ zzceg zzbte;
    private /* synthetic */ zzcgp zzbtf;
    private /* synthetic */ zzcjh zzbtk;

    zzchd(zzcgp com_google_android_gms_internal_zzcgp, zzcjh com_google_android_gms_internal_zzcjh, zzceg com_google_android_gms_internal_zzceg) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbtk = com_google_android_gms_internal_zzcjh;
        this.zzbte = com_google_android_gms_internal_zzceg;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzb(this.zzbtk, this.zzbte);
    }
}
