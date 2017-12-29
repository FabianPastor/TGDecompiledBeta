package com.google.android.gms.internal;

final class zzcld implements Runnable {
    private /* synthetic */ zzcim zzjdt;
    private /* synthetic */ Runnable zzjjb;

    zzcld(zzcla com_google_android_gms_internal_zzcla, zzcim com_google_android_gms_internal_zzcim, Runnable runnable) {
        this.zzjdt = com_google_android_gms_internal_zzcim;
        this.zzjjb = runnable;
    }

    public final void run() {
        this.zzjdt.zzbal();
        this.zzjdt.zzi(this.zzjjb);
        this.zzjdt.zzbah();
    }
}
