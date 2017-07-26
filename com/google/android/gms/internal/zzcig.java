package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzcig implements Runnable {
    private /* synthetic */ zzcid zzbua;

    zzcig(zzcid com_google_android_gms_internal_zzcid) {
        this.zzbua = com_google_android_gms_internal_zzcid;
    }

    public final void run() {
        zzcfd zzd = this.zzbua.zzbtU;
        if (zzd == null) {
            this.zzbua.zzwF().zzyx().log("Discarding data. Failed to send app launch");
            return;
        }
        try {
            zzd.zza(this.zzbua.zzwu().zzdV(this.zzbua.zzwF().zzyE()));
            this.zzbua.zza(zzd, null);
            this.zzbua.zzkP();
        } catch (RemoteException e) {
            this.zzbua.zzwF().zzyx().zzj("Failed to send app launch to the service", e);
        }
    }
}
