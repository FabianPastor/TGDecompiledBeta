package com.google.android.gms.internal;

final class zzcjo implements Runnable {
    private /* synthetic */ boolean zzecs;
    private /* synthetic */ zzcjn zzjhc;

    zzcjo(zzcjn com_google_android_gms_internal_zzcjn, boolean z) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzecs = z;
    }

    public final void run() {
        this.zzjhc.zzbp(this.zzecs);
    }
}
