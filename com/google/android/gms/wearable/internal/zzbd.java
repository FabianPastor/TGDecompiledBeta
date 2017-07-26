package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.zzbo;

public final class zzbd extends zzdh {
    private final Object mLock = new Object();
    private zzah zzbSr;
    private zzbe zzbSv;

    public final void zza(zzbe com_google_android_gms_wearable_internal_zzbe) {
        synchronized (this.mLock) {
            this.zzbSv = (zzbe) zzbo.zzu(com_google_android_gms_wearable_internal_zzbe);
            zzah com_google_android_gms_wearable_internal_zzah = this.zzbSr;
        }
        if (com_google_android_gms_wearable_internal_zzah != null) {
            com_google_android_gms_wearable_internal_zzbe.zzb(com_google_android_gms_wearable_internal_zzah);
        }
    }

    public final void zzm(int i, int i2) {
        synchronized (this.mLock) {
            zzbe com_google_android_gms_wearable_internal_zzbe = this.zzbSv;
            zzah com_google_android_gms_wearable_internal_zzah = new zzah(i, i2);
            this.zzbSr = com_google_android_gms_wearable_internal_zzah;
        }
        if (com_google_android_gms_wearable_internal_zzbe != null) {
            com_google_android_gms_wearable_internal_zzbe.zzb(com_google_android_gms_wearable_internal_zzah);
        }
    }
}
