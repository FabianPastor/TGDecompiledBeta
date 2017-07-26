package com.google.android.gms.internal;

final class zzcgv implements Runnable {
    private /* synthetic */ zzcgq zzbtf;
    private /* synthetic */ zzcek zzbtg;

    zzcgv(zzcgq com_google_android_gms_internal_zzcgq, zzcek com_google_android_gms_internal_zzcek) {
        this.zzbtf = com_google_android_gms_internal_zzcgq;
        this.zzbtg = com_google_android_gms_internal_zzcek;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzd(this.zzbtg);
    }
}
