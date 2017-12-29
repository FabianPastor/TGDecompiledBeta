package com.google.android.gms.internal;

final class zzcjd implements Runnable {
    private /* synthetic */ String zzimf;
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ zzcha zzjgs;

    zzcjd(zzcir com_google_android_gms_internal_zzcir, zzcha com_google_android_gms_internal_zzcha, String str) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgs = com_google_android_gms_internal_zzcha;
        this.zzimf = str;
    }

    public final void run() {
        this.zzjgo.zziwf.zzbal();
        this.zzjgo.zziwf.zzb(this.zzjgs, this.zzimf);
    }
}
