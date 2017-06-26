package com.google.android.gms.internal;

import android.os.Build.VERSION;

final class zzcix implements Runnable {
    private /* synthetic */ zzciw zzbuq;

    zzcix(zzciw com_google_android_gms_internal_zzciw) {
        this.zzbuq = com_google_android_gms_internal_zzciw;
    }

    public final void run() {
        if (this.zzbuq.zzbun == null) {
            zzcel.zzxE();
            if (VERSION.SDK_INT >= 24) {
                this.zzbuq.zzbrP.zzyD().log("AppMeasurementJobService processed last upload request.");
                this.zzbuq.zzbup.zzbum.zza(this.zzbuq.zzbuo, false);
            }
        } else if (this.zzbuq.zzbup.zzbum.callServiceStopSelfResult(this.zzbuq.zzbun.intValue())) {
            zzcel.zzxE();
            this.zzbuq.zzbrP.zzyD().zzj("Local AppMeasurementService processed last upload request. StartId", this.zzbuq.zzbun);
        }
    }
}
