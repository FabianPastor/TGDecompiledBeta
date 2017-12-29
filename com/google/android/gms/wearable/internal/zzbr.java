package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.zzbq;

public final class zzbr extends zzej {
    private final Object mLock = new Object();
    private zzav zzljm;
    private zzbs zzljq;

    public final void zza(zzbs com_google_android_gms_wearable_internal_zzbs) {
        synchronized (this.mLock) {
            this.zzljq = (zzbs) zzbq.checkNotNull(com_google_android_gms_wearable_internal_zzbs);
            zzav com_google_android_gms_wearable_internal_zzav = this.zzljm;
        }
        if (com_google_android_gms_wearable_internal_zzav != null) {
            com_google_android_gms_wearable_internal_zzbs.zzb(com_google_android_gms_wearable_internal_zzav);
        }
    }

    public final void zzs(int i, int i2) {
        synchronized (this.mLock) {
            zzbs com_google_android_gms_wearable_internal_zzbs = this.zzljq;
            zzav com_google_android_gms_wearable_internal_zzav = new zzav(i, i2);
            this.zzljm = com_google_android_gms_wearable_internal_zzav;
        }
        if (com_google_android_gms_wearable_internal_zzbs != null) {
            com_google_android_gms_wearable_internal_zzbs.zzb(com_google_android_gms_wearable_internal_zzav);
        }
    }
}
