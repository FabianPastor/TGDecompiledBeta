package com.google.android.gms.internal;

final class zzchr implements Runnable {
    private /* synthetic */ zzchl zzbtt;
    private /* synthetic */ long zzbtx;

    zzchr(zzchl com_google_android_gms_internal_zzchl, long j) {
        this.zzbtt = com_google_android_gms_internal_zzchl;
        this.zzbtx = j;
    }

    public final void run() {
        this.zzbtt.zzwG().zzbrx.set(this.zzbtx);
        this.zzbtt.zzwF().zzyC().zzj("Minimum session duration set", Long.valueOf(this.zzbtx));
    }
}
