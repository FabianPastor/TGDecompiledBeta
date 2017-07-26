package com.google.android.gms.internal;

import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;

final class zzchn implements Runnable {
    private /* synthetic */ zzchl zzbtt;
    private /* synthetic */ ConditionalUserProperty zzbtu;

    zzchn(zzchl com_google_android_gms_internal_zzchl, ConditionalUserProperty conditionalUserProperty) {
        this.zzbtt = com_google_android_gms_internal_zzchl;
        this.zzbtu = conditionalUserProperty;
    }

    public final void run() {
        this.zzbtt.zzb(this.zzbtu);
    }
}
