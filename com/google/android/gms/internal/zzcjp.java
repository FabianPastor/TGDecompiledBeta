package com.google.android.gms.internal;

import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;

final class zzcjp implements Runnable {
    private /* synthetic */ zzcjn zzjhc;
    private /* synthetic */ ConditionalUserProperty zzjhd;

    zzcjp(zzcjn com_google_android_gms_internal_zzcjn, ConditionalUserProperty conditionalUserProperty) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzjhd = conditionalUserProperty;
    }

    public final void run() {
        this.zzjhc.zzb(this.zzjhd);
    }
}
