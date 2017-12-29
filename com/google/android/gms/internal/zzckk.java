package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzckk implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzckg zzjij;

    zzckk(zzckg com_google_android_gms_internal_zzckg, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjij = com_google_android_gms_internal_zzckg;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final void run() {
        zzche zzd = this.zzjij.zzjid;
        if (zzd == null) {
            this.zzjij.zzawy().zzazd().log("Discarding data. Failed to send app launch");
            return;
        }
        try {
            zzd.zza(this.zzjgn);
            this.zzjij.zza(zzd, null, this.zzjgn);
            this.zzjij.zzxr();
        } catch (RemoteException e) {
            this.zzjij.zzawy().zzazd().zzj("Failed to send app launch to the service", e);
        }
    }
}
