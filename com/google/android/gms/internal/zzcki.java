package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzcki implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzckg zzjij;

    zzcki(zzckg com_google_android_gms_internal_zzckg, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjij = com_google_android_gms_internal_zzckg;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final void run() {
        zzche zzd = this.zzjij.zzjid;
        if (zzd == null) {
            this.zzjij.zzawy().zzazd().log("Failed to reset data on the service; null service");
            return;
        }
        try {
            zzd.zzd(this.zzjgn);
        } catch (RemoteException e) {
            this.zzjij.zzawy().zzazd().zzj("Failed to reset data on the service", e);
        }
        this.zzjij.zzxr();
    }
}
