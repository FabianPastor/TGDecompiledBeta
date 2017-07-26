package com.google.android.gms.internal;

final class zzche implements Runnable {
    private /* synthetic */ zzceh zzbte;
    private /* synthetic */ zzcgq zzbtf;
    private /* synthetic */ zzcji zzbtk;

    zzche(zzcgq com_google_android_gms_internal_zzcgq, zzcji com_google_android_gms_internal_zzcji, zzceh com_google_android_gms_internal_zzceh) {
        this.zzbtf = com_google_android_gms_internal_zzcgq;
        this.zzbtk = com_google_android_gms_internal_zzcji;
        this.zzbte = com_google_android_gms_internal_zzceh;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzb(this.zzbtk, this.zzbte);
    }
}
