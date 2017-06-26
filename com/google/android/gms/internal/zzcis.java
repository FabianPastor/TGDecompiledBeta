package com.google.android.gms.internal;

final class zzcis implements Runnable {
    private /* synthetic */ zzcip zzbuk;
    private /* synthetic */ zzcfc zzbul;

    zzcis(zzcip com_google_android_gms_internal_zzcip, zzcfc com_google_android_gms_internal_zzcfc) {
        this.zzbuk = com_google_android_gms_internal_zzcip;
        this.zzbul = com_google_android_gms_internal_zzcfc;
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
