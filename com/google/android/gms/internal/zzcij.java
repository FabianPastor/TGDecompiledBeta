package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzcij implements Runnable {
    private /* synthetic */ zzcid zzbua;

    zzcij(zzcid com_google_android_gms_internal_zzcid) {
        this.zzbua = com_google_android_gms_internal_zzcid;
    }

    public final void run() {
        zzcfd zzd = this.zzbua.zzbtU;
        if (zzd == null) {
            this.zzbua.zzwF().zzyx().log("Failed to send measurementEnabled to service");
            return;
        }
        try {
            zzd.zzb(this.zzbua.zzwu().zzdV(this.zzbua.zzwF().zzyE()));
            this.zzbua.zzkP();
        } catch (RemoteException e) {
            this.zzbua.zzwF().zzyx().zzj("Failed to send measurementEnabled to the service", e);
        }
    }
}
