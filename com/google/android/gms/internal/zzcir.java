package com.google.android.gms.internal;

final class zzcir implements Runnable {
    private /* synthetic */ zzcfd zzbuj;
    private /* synthetic */ zzciq zzbuk;

    zzcir(zzciq com_google_android_gms_internal_zzciq, zzcfd com_google_android_gms_internal_zzcfd) {
        this.zzbuk = com_google_android_gms_internal_zzciq;
        this.zzbuj = com_google_android_gms_internal_zzcfd;
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
