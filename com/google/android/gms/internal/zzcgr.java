package com.google.android.gms.internal;

final class zzcgr implements Runnable {
    private /* synthetic */ zzceh zzbte;
    private /* synthetic */ zzcgq zzbtf;

    zzcgr(zzcgq com_google_android_gms_internal_zzcgq, zzceh com_google_android_gms_internal_zzceh) {
        this.zzbtf = com_google_android_gms_internal_zzcgq;
        this.zzbte = com_google_android_gms_internal_zzceh;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzd(this.zzbte);
    }
}
