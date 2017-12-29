package com.google.android.gms.internal;

import android.content.ComponentName;

final class zzcky implements Runnable {
    private /* synthetic */ zzcku zzjit;

    zzcky(zzcku com_google_android_gms_internal_zzcku) {
        this.zzjit = com_google_android_gms_internal_zzcku;
    }

    public final void run() {
        this.zzjit.zzjij.onServiceDisconnected(new ComponentName(this.zzjit.zzjij.getContext(), "com.google.android.gms.measurement.AppMeasurementService"));
    }
}
