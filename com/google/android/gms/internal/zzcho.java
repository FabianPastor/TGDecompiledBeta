package com.google.android.gms.internal;

import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;

final class zzcho implements Runnable {
    private /* synthetic */ zzchl zzbtt;
    private /* synthetic */ ConditionalUserProperty zzbtu;

    zzcho(zzchl com_google_android_gms_internal_zzchl, ConditionalUserProperty conditionalUserProperty) {
        this.zzbtt = com_google_android_gms_internal_zzchl;
        this.zzbtu = conditionalUserProperty;
    }

    public final void run() {
        this.zzbtt.zzc(this.zzbtu);
    }
}
