package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.zzn;

class zzgm<T> extends zza {
    private zzn<T> zzeay;

    public zzgm(zzn<T> com_google_android_gms_common_api_internal_zzn_T) {
        this.zzeay = com_google_android_gms_common_api_internal_zzn_T;
    }

    public final void zzav(T t) {
        zzn com_google_android_gms_common_api_internal_zzn = this.zzeay;
        if (com_google_android_gms_common_api_internal_zzn != null) {
            com_google_android_gms_common_api_internal_zzn.setResult(t);
            this.zzeay = null;
        }
    }
}
