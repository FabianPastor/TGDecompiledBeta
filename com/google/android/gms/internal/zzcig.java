package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.measurement.AppMeasurement.zzb;

final class zzcig implements Runnable {
    private /* synthetic */ zzcic zzbua;
    private /* synthetic */ zzb zzbuc;

    zzcig(zzcic com_google_android_gms_internal_zzcic, zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        this.zzbua = com_google_android_gms_internal_zzcic;
        this.zzbuc = com_google_android_gms_measurement_AppMeasurement_zzb;
    }

    public final void run() {
        zzcfc zzd = this.zzbua.zzbtU;
        if (zzd == null) {
            this.zzbua.zzwF().zzyx().log("Failed to send current screen to service");
            return;
        }
        try {
            if (this.zzbuc == null) {
                zzd.zza(0, null, null, this.zzbua.getContext().getPackageName());
            } else {
                zzd.zza(this.zzbuc.zzbol, this.zzbuc.zzboj, this.zzbuc.zzbok, this.zzbua.getContext().getPackageName());
            }
            this.zzbua.zzkP();
        } catch (RemoteException e) {
            this.zzbua.zzwF().zzyx().zzj("Failed to send current screen to the service", e);
        }
    }
}
