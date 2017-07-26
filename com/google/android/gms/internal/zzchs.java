package com.google.android.gms.internal;

final class zzchs implements Runnable {
    private /* synthetic */ zzchl zzbtt;
    private /* synthetic */ long zzbtx;

    zzchs(zzchl com_google_android_gms_internal_zzchl, long j) {
        this.zzbtt = com_google_android_gms_internal_zzchl;
        this.zzbtx = j;
    }

    public final void run() {
        this.zzbtt.zzwG().zzbry.set(this.zzbtx);
        this.zzbtt.zzwF().zzyC().zzj("Session timeout duration set", Long.valueOf(this.zzbtx));
    }
}
