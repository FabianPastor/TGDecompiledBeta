package com.google.android.gms.internal;

import android.os.RemoteException;

final class zzcif implements Runnable {
    private /* synthetic */ zzcic zzbua;

    zzcif(zzcic com_google_android_gms_internal_zzcic) {
        this.zzbua = com_google_android_gms_internal_zzcic;
    }

    public final void run() {
        zzcfc zzd = this.zzbua.zzbtU;
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
