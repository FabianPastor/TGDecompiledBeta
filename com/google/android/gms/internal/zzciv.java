package com.google.android.gms.internal;

final class zzciv implements Runnable {
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ zzcgl zzjgp;

    zzciv(zzcir com_google_android_gms_internal_zzcir, zzcgl com_google_android_gms_internal_zzcgl) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgp = com_google_android_gms_internal_zzcgl;
    }

    public final void run() {
        this.zzjgo.zziwf.zzbal();
        this.zzjgo.zziwf.zze(this.zzjgp);
    }
}
