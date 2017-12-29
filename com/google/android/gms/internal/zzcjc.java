package com.google.android.gms.internal;

final class zzcjc implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ zzcha zzjgs;

    zzcjc(zzcir com_google_android_gms_internal_zzcir, zzcha com_google_android_gms_internal_zzcha, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgs = com_google_android_gms_internal_zzcha;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final void run() {
        this.zzjgo.zziwf.zzbal();
        this.zzjgo.zziwf.zzb(this.zzjgs, this.zzjgn);
    }
}
