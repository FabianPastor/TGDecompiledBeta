package com.google.android.gms.internal;

final class zzchd implements Runnable {
    private /* synthetic */ zzceh zzbte;
    private /* synthetic */ zzcgq zzbtf;
    private /* synthetic */ zzcji zzbtk;

    zzchd(zzcgq com_google_android_gms_internal_zzcgq, zzcji com_google_android_gms_internal_zzcji, zzceh com_google_android_gms_internal_zzceh) {
        this.zzbtf = com_google_android_gms_internal_zzcgq;
        this.zzbtk = com_google_android_gms_internal_zzcji;
        this.zzbte = com_google_android_gms_internal_zzceh;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzc(this.zzbtk, this.zzbte);
    }
}
