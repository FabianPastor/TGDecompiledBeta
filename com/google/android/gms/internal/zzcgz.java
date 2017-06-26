package com.google.android.gms.internal;

final class zzcgz implements Runnable {
    private /* synthetic */ zzceg zzbte;
    private /* synthetic */ zzcgp zzbtf;
    private /* synthetic */ zzcey zzbtj;

    zzcgz(zzcgp com_google_android_gms_internal_zzcgp, zzcey com_google_android_gms_internal_zzcey, zzceg com_google_android_gms_internal_zzceg) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbtj = com_google_android_gms_internal_zzcey;
        this.zzbte = com_google_android_gms_internal_zzceg;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzb(this.zzbtj, this.zzbte);
    }
}
