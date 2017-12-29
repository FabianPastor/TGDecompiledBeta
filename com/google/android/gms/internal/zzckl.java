package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.measurement.AppMeasurement.zzb;

final class zzckl implements Runnable {
    private /* synthetic */ zzckg zzjij;
    private /* synthetic */ zzb zzjil;

    zzckl(zzckg com_google_android_gms_internal_zzckg, zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        this.zzjij = com_google_android_gms_internal_zzckg;
        this.zzjil = com_google_android_gms_measurement_AppMeasurement_zzb;
    }

    public final void run() {
        zzche zzd = this.zzjij.zzjid;
        if (zzd == null) {
            this.zzjij.zzawy().zzazd().log("Failed to send current screen to service");
            return;
        }
        try {
            if (this.zzjil == null) {
                zzd.zza(0, null, null, this.zzjij.getContext().getPackageName());
            } else {
                zzd.zza(this.zzjil.zziwm, this.zzjil.zziwk, this.zzjil.zziwl, this.zzjij.getContext().getPackageName());
            }
            this.zzjij.zzxr();
        } catch (RemoteException e) {
            this.zzjij.zzawy().zzazd().zzj("Failed to send current screen to the service", e);
        }
    }
}
