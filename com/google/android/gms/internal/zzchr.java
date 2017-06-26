package com.google.android.gms.internal;

final class zzchr implements Runnable {
    private /* synthetic */ zzchk zzbtt;
    private /* synthetic */ long zzbtx;

    zzchr(zzchk com_google_android_gms_internal_zzchk, long j) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.zzbtx = j;
    }

    public final void run() {
        this.zzbtt.zzwG().zzbry.set(this.zzbtx);
        this.zzbtt.zzwF().zzyC().zzj("Session timeout duration set", Long.valueOf(this.zzbtx));
    }
}
