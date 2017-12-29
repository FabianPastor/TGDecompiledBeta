package com.google.android.gms.internal;

final class zzcgg implements Runnable {
    private /* synthetic */ long zziwu;
    private /* synthetic */ zzcgd zziwv;

    zzcgg(zzcgd com_google_android_gms_internal_zzcgd, long j) {
        this.zziwv = com_google_android_gms_internal_zzcgd;
        this.zziwu = j;
    }

    public final void run() {
        this.zziwv.zzak(this.zziwu);
    }
}
