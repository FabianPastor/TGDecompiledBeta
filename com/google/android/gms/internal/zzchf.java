package com.google.android.gms.internal;

final class zzchf implements Runnable {
    private /* synthetic */ zzceg zzbte;
    private /* synthetic */ zzcgp zzbtf;

    zzchf(zzcgp com_google_android_gms_internal_zzcgp, zzceg com_google_android_gms_internal_zzceg) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbte = com_google_android_gms_internal_zzceg;
    }

    public final void run() {
        this.zzbtf.zzboe.zzze();
        this.zzbtf.zzboe.zze(this.zzbte);
    }
}
