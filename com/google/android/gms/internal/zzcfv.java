package com.google.android.gms.internal;

final class zzcfv implements Runnable {
    private /* synthetic */ boolean zzbrg;
    private /* synthetic */ zzcfu zzbrh;

    zzcfv(zzcfu com_google_android_gms_internal_zzcfu, boolean z) {
        this.zzbrh = com_google_android_gms_internal_zzcfu;
        this.zzbrg = z;
    }

    public final void run() {
        this.zzbrh.zzboe.zzam(this.zzbrg);
    }
}
