package com.google.android.gms.internal;

final class zzchq implements Runnable {
    private /* synthetic */ zzchk zzbtt;
    private /* synthetic */ long zzbtx;

    zzchq(zzchk com_google_android_gms_internal_zzchk, long j) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.zzbtx = j;
    }

    public final void run() {
        this.zzbtt.zzwG().zzbrx.set(this.zzbtx);
        this.zzbtt.zzwF().zzyC().zzj("Minimum session duration set", Long.valueOf(this.zzbtx));
    }
}
