package com.google.android.gms.internal;

final class zzchl implements Runnable {
    private /* synthetic */ boolean val$enabled;
    private /* synthetic */ zzchk zzbtt;

    zzchl(zzchk com_google_android_gms_internal_zzchk, boolean z) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.val$enabled = z;
    }

    public final void run() {
        this.zzbtt.zzan(this.val$enabled);
    }
}
