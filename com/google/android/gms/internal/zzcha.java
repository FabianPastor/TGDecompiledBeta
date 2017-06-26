package com.google.android.gms.internal;

final class zzcha implements Runnable {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ zzcgp zzbtf;
    private /* synthetic */ zzcey zzbtj;

    zzcha(zzcgp com_google_android_gms_internal_zzcgp, zzcey com_google_android_gms_internal_zzcey, String str) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbtj = com_google_android_gms_internal_zzcey;
        this.zzbjh = str;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzb(this.zzbtj, this.zzbjh);
    }
}
