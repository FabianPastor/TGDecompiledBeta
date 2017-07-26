package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.measurement.AppMeasurement;

final class zzcgd implements Runnable {
    private /* synthetic */ zzcgl zzbrM;
    private /* synthetic */ long zzbrN;
    private /* synthetic */ Bundle zzbrO;
    private /* synthetic */ zzcfl zzbrP;
    private /* synthetic */ Context zztF;

    zzcgd(zzcgc com_google_android_gms_internal_zzcgc, zzcgl com_google_android_gms_internal_zzcgl, long j, Bundle bundle, Context context, zzcfl com_google_android_gms_internal_zzcfl) {
        this.zzbrM = com_google_android_gms_internal_zzcgl;
        this.zzbrN = j;
        this.zzbrO = bundle;
        this.zztF = context;
        this.zzbrP = com_google_android_gms_internal_zzcfl;
    }

    public final void run() {
        zzcjk zzG = this.zzbrM.zzwz().zzG(this.zzbrM.zzwu().zzhl(), "_fot");
        long longValue = (zzG == null || !(zzG.mValue instanceof Long)) ? 0 : ((Long) zzG.mValue).longValue();
        long j = this.zzbrN;
        longValue = (longValue <= 0 || (j < longValue && j > 0)) ? j : longValue - 1;
        if (longValue > 0) {
            this.zzbrO.putLong("click_timestamp", longValue);
        }
        AppMeasurement.getInstance(this.zztF).logEventInternal("auto", "_cmp", this.zzbrO);
        this.zzbrP.zzyD().log("Install campaign recorded");
    }
}
