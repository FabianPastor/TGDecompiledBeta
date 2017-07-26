package com.google.android.gms.internal;

final class zzchb implements Runnable {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ zzcgq zzbtf;
    private /* synthetic */ zzcez zzbtj;

    zzchb(zzcgq com_google_android_gms_internal_zzcgq, zzcez com_google_android_gms_internal_zzcez, String str) {
        this.zzbtf = com_google_android_gms_internal_zzcgq;
        this.zzbtj = com_google_android_gms_internal_zzcez;
        this.zzbjh = str;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zzb(this.zzbtj, this.zzbjh);
    }
}
