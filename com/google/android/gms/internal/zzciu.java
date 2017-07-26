package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;

final class zzciu implements Runnable {
    private /* synthetic */ zzciq zzbuk;

    zzciu(zzciq com_google_android_gms_internal_zzciq) {
        this.zzbuk = com_google_android_gms_internal_zzciq;
    }

    public final void run() {
        zzcid com_google_android_gms_internal_zzcid = this.zzbuk.zzbua;
        Context context = this.zzbuk.zzbua.getContext();
        zzcem.zzxE();
        com_google_android_gms_internal_zzcid.onServiceDisconnected(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
    }
}
