package com.google.android.gms.internal;

final class zzckv implements Runnable {
    private /* synthetic */ zzche zzjis;
    private /* synthetic */ zzcku zzjit;

    zzckv(zzcku com_google_android_gms_internal_zzcku, zzche com_google_android_gms_internal_zzche) {
        this.zzjit = com_google_android_gms_internal_zzcku;
        this.zzjis = com_google_android_gms_internal_zzche;
    }

    public final void run() {
        synchronized (this.zzjit) {
            this.zzjit.zzjiq = false;
            if (!this.zzjit.zzjij.isConnected()) {
                this.zzjit.zzjij.zzawy().zzazj().log("Connected to service");
                this.zzjit.zzjij.zza(this.zzjis);
            }
        }
    }
}
