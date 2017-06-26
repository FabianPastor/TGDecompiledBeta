package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzcii implements Runnable {
    private /* synthetic */ zzcic zzbua;

    zzcii(zzcic com_google_android_gms_internal_zzcic) {
        this.zzbua = com_google_android_gms_internal_zzcic;
    }

    public final void run() {
        zzcfc zzd = this.zzbua.zzbtU;
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
