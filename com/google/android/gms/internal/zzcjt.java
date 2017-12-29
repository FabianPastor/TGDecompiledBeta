package com.google.android.gms.internal;

final class zzcjt implements Runnable {
    private /* synthetic */ zzcjn zzjhc;
    private /* synthetic */ long zzjhg;

    zzcjt(zzcjn com_google_android_gms_internal_zzcjn, long j) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzjhg = j;
    }

    public final void run() {
        this.zzjhc.zzawz().zzjde.set(this.zzjhg);
        this.zzjhc.zzawy().zzazi().zzj("Minimum session duration set", Long.valueOf(this.zzjhg));
    }
}
