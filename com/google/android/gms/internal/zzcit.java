package com.google.android.gms.internal;

final class zzcit implements Runnable {
    private /* synthetic */ zzciq zzbuk;
    private /* synthetic */ zzcfd zzbul;

    zzcit(zzciq com_google_android_gms_internal_zzciq, zzcfd com_google_android_gms_internal_zzcfd) {
        this.zzbuk = com_google_android_gms_internal_zzciq;
        this.zzbul = com_google_android_gms_internal_zzcfd;
    }

    public final void run() {
        synchronized (this.zzbuk) {
            this.zzbuk.zzbuh = false;
            if (!this.zzbuk.zzbua.isConnected()) {
                this.zzbuk.zzbua.zzwF().zzyC().log("Connected to remote service");
                this.zzbuk.zzbua.zza(this.zzbul);
            }
        }
    }
}
