package com.google.android.gms.internal;

import android.os.Build.VERSION;

final class zzciy implements Runnable {
    private /* synthetic */ zzcix zzbuq;

    zzciy(zzcix com_google_android_gms_internal_zzcix) {
        this.zzbuq = com_google_android_gms_internal_zzcix;
    }

    public final void run() {
        if (this.zzbuq.zzbun == null) {
            zzcem.zzxE();
            if (VERSION.SDK_INT >= 24) {
                this.zzbuq.zzbrP.zzyD().log("AppMeasurementJobService processed last upload request.");
                this.zzbuq.zzbup.zzbum.zza(this.zzbuq.zzbuo, false);
            }
        } else if (this.zzbuq.zzbup.zzbum.callServiceStopSelfResult(this.zzbuq.zzbun.intValue())) {
            zzcem.zzxE();
            this.zzbuq.zzbrP.zzyD().zzj("Local AppMeasurementService processed last upload request. StartId", this.zzbuq.zzbun);
        }
    }
}
