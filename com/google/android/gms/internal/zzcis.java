package com.google.android.gms.internal;

final class zzcis implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzcir zzjgo;

    zzcis(zzcir com_google_android_gms_internal_zzcir, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final void run() {
        this.zzjgo.zziwf.zzbal();
        this.zzjgo.zziwf.zze(this.zzjgn);
    }
}
