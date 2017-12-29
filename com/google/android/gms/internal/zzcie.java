package com.google.android.gms.internal;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.measurement.AppMeasurement;

final class zzcie implements Runnable {
    private /* synthetic */ Context val$context;
    private /* synthetic */ PendingResult zzdop;
    private /* synthetic */ zzcim zzjdt;
    private /* synthetic */ long zzjdu;
    private /* synthetic */ Bundle zzjdv;
    private /* synthetic */ zzchm zzjdw;

    zzcie(zzcid com_google_android_gms_internal_zzcid, zzcim com_google_android_gms_internal_zzcim, long j, Bundle bundle, Context context, zzchm com_google_android_gms_internal_zzchm, PendingResult pendingResult) {
        this.zzjdt = com_google_android_gms_internal_zzcim;
        this.zzjdu = j;
        this.zzjdv = bundle;
        this.val$context = context;
        this.zzjdw = com_google_android_gms_internal_zzchm;
        this.zzdop = pendingResult;
    }

    public final void run() {
        zzclp zzag = this.zzjdt.zzaws().zzag(this.zzjdt.zzawn().getAppId(), "_fot");
        long longValue = (zzag == null || !(zzag.mValue instanceof Long)) ? 0 : ((Long) zzag.mValue).longValue();
        long j = this.zzjdu;
        longValue = (longValue <= 0 || (j < longValue && j > 0)) ? j : longValue - 1;
        if (longValue > 0) {
            this.zzjdv.putLong("click_timestamp", longValue);
        }
        AppMeasurement.getInstance(this.val$context).logEventInternal("auto", "_cmp", this.zzjdv);
        this.zzjdw.zzazj().log("Install campaign recorded");
        if (this.zzdop != null) {
            this.zzdop.finish();
        }
    }
}
