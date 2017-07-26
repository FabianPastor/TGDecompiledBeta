package com.google.android.gms.internal;

final class zzchm implements Runnable {
    private /* synthetic */ boolean val$enabled;
    private /* synthetic */ zzchl zzbtt;

    zzchm(zzchl com_google_android_gms_internal_zzchl, boolean z) {
        this.zzbtt = com_google_android_gms_internal_zzchl;
        this.val$enabled = z;
    }

    public final void run() {
        this.zzbtt.zzan(this.val$enabled);
    }
}
