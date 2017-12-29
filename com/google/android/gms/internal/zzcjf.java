package com.google.android.gms.internal;

final class zzcjf implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ zzcln zzjgt;

    zzcjf(zzcir com_google_android_gms_internal_zzcir, zzcln com_google_android_gms_internal_zzcln, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgt = com_google_android_gms_internal_zzcln;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final void run() {
        this.zzjgo.zziwf.zzbal();
        this.zzjgo.zziwf.zzc(this.zzjgt, this.zzjgn);
    }
}
