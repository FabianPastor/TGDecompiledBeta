package com.google.android.gms.internal;

import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;

final class zzchm implements Runnable {
    private /* synthetic */ zzchk zzbtt;
    private /* synthetic */ ConditionalUserProperty zzbtu;

    zzchm(zzchk com_google_android_gms_internal_zzchk, ConditionalUserProperty conditionalUserProperty) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.zzbtu = conditionalUserProperty;
    }

    public final void run() {
        this.zzbtt.zzb(this.zzbtu);
    }
}
