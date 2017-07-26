package com.google.android.gms.internal;

final class zzcgs implements Runnable {
    private /* synthetic */ zzceh zzbte;
    private /* synthetic */ zzcgq zzbtf;
    private /* synthetic */ zzcek zzbtg;

    zzcgs(zzcgq com_google_android_gms_internal_zzcgq, zzcek com_google_android_gms_internal_zzcek, zzceh com_google_android_gms_internal_zzceh) {
        this.zzbtf = com_google_android_gms_internal_zzcgq;
        this.zzbtg = com_google_android_gms_internal_zzcek;
        this.zzbte = com_google_android_gms_internal_zzceh;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzc(this.zzbtg, this.zzbte);
    }
}
