package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;

final class zzcit implements Runnable {
    private /* synthetic */ zzcip zzbuk;

    zzcit(zzcip com_google_android_gms_internal_zzcip) {
        this.zzbuk = com_google_android_gms_internal_zzcip;
    }

    public final void run() {
        zzcic com_google_android_gms_internal_zzcic = this.zzbuk.zzbua;
        Context context = this.zzbuk.zzbua.getContext();
        zzcel.zzxE();
        com_google_android_gms_internal_zzcic.onServiceDisconnected(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
    }
}
