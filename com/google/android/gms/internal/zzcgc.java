package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.measurement.AppMeasurement;

final class zzcgc implements Runnable {
    private /* synthetic */ zzcgk zzbrM;
    private /* synthetic */ long zzbrN;
    private /* synthetic */ Bundle zzbrO;
    private /* synthetic */ zzcfk zzbrP;
    private /* synthetic */ Context zztH;

    zzcgc(zzcgb com_google_android_gms_internal_zzcgb, zzcgk com_google_android_gms_internal_zzcgk, long j, Bundle bundle, Context context, zzcfk com_google_android_gms_internal_zzcfk) {
        this.zzbrM = com_google_android_gms_internal_zzcgk;
        this.zzbrN = j;
        this.zzbrO = bundle;
        this.zztH = context;
        this.zzbrP = com_google_android_gms_internal_zzcfk;
    }

    public final void run() {
        zzcjj zzG = this.zzbrM.zzwz().zzG(this.zzbrM.zzwu().zzhl(), "_fot");
        long longValue = (zzG == null || !(zzG.mValue instanceof Long)) ? 0 : ((Long) zzG.mValue).longValue();
        long j = this.zzbrN;
        longValue = (longValue <= 0 || (j < longValue && j > 0)) ? j : longValue - 1;
        if (longValue > 0) {
            this.zzbrO.putLong("click_timestamp", longValue);
        }
        AppMeasurement.getInstance(this.zztH).logEventInternal("auto", "_cmp", this.zzbrO);
        this.zzbrP.zzyD().log("Install campaign recorded");
    }
}
