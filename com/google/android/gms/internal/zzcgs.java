package com.google.android.gms.internal;

final class zzcgs implements Runnable {
    private /* synthetic */ zzceg zzbte;
    private /* synthetic */ zzcgp zzbtf;
    private /* synthetic */ zzcej zzbtg;

    zzcgs(zzcgp com_google_android_gms_internal_zzcgp, zzcej com_google_android_gms_internal_zzcej, zzceg com_google_android_gms_internal_zzceg) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbtg = com_google_android_gms_internal_zzcej;
        this.zzbte = com_google_android_gms_internal_zzceg;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzb(this.zzbtg, this.zzbte);
    }
}
