package com.google.android.gms.internal;

final class zzcit implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ zzcgl zzjgp;

    zzcit(zzcir com_google_android_gms_internal_zzcir, zzcgl com_google_android_gms_internal_zzcgl, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgp = com_google_android_gms_internal_zzcgl;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final void run() {
        this.zzjgo.zziwf.zzbal();
        this.zzjgo.zziwf.zzc(this.zzjgp, this.zzjgn);
    }
}
