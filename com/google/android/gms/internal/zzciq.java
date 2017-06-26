package com.google.android.gms.internal;

final class zzciq implements Runnable {
    private /* synthetic */ zzcfc zzbuj;
    private /* synthetic */ zzcip zzbuk;

    zzciq(zzcip com_google_android_gms_internal_zzcip, zzcfc com_google_android_gms_internal_zzcfc) {
        this.zzbuk = com_google_android_gms_internal_zzcip;
        this.zzbuj = com_google_android_gms_internal_zzcfc;
    }

    public final void run() {
        synchronized (this.zzbuk) {
            this.zzbuk.zzbuh = false;
            if (!this.zzbuk.zzbua.isConnected()) {
                this.zzbuk.zzbua.zzwF().zzyD().log("Connected to service");
                this.zzbuk.zzbua.zza(this.zzbuj);
            }
        }
    }
}
