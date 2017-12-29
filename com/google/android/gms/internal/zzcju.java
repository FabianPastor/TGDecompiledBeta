package com.google.android.gms.internal;

final class zzcju implements Runnable {
    private /* synthetic */ zzcjn zzjhc;
    private /* synthetic */ long zzjhg;

    zzcju(zzcjn com_google_android_gms_internal_zzcjn, long j) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzjhg = j;
    }

    public final void run() {
        this.zzjhc.zzawz().zzjdf.set(this.zzjhg);
        this.zzjhc.zzawy().zzazi().zzj("Session timeout duration set", Long.valueOf(this.zzjhg));
    }
}
