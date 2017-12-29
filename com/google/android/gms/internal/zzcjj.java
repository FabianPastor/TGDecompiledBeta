package com.google.android.gms.internal;

import com.google.android.gms.measurement.AppMeasurement.zzb;

final class zzcjj implements Runnable {
    private /* synthetic */ String zzimf;
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ String zzjgu;
    private /* synthetic */ String zzjgv;
    private /* synthetic */ long zzjgw;

    zzcjj(zzcir com_google_android_gms_internal_zzcir, String str, String str2, String str3, long j) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgu = str;
        this.zzimf = str2;
        this.zzjgv = str3;
        this.zzjgw = j;
    }

    public final void run() {
        if (this.zzjgu == null) {
            this.zzjgo.zziwf.zzawq().zza(this.zzimf, null);
            return;
        }
        zzb com_google_android_gms_measurement_AppMeasurement_zzb = new zzb();
        com_google_android_gms_measurement_AppMeasurement_zzb.zziwk = this.zzjgv;
        com_google_android_gms_measurement_AppMeasurement_zzb.zziwl = this.zzjgu;
        com_google_android_gms_measurement_AppMeasurement_zzb.zziwm = this.zzjgw;
        this.zzjgo.zziwf.zzawq().zza(this.zzimf, com_google_android_gms_measurement_AppMeasurement_zzb);
    }
}
