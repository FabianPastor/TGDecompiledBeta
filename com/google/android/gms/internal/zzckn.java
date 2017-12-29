package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzckn implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzckg zzjij;

    zzckn(zzckg com_google_android_gms_internal_zzckg, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjij = com_google_android_gms_internal_zzckg;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final void run() {
        zzche zzd = this.zzjij.zzjid;
        if (zzd == null) {
            this.zzjij.zzawy().zzazd().log("Failed to send measurementEnabled to service");
            return;
        }
        try {
            zzd.zzb(this.zzjgn);
            this.zzjij.zzxr();
        } catch (RemoteException e) {
            this.zzjij.zzawy().zzazd().zzj("Failed to send measurementEnabled to the service", e);
        }
    }
}
