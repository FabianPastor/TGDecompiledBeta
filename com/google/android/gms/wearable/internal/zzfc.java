package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbay;

class zzfc<T> extends zza {
    private zzbay<T> zzajL;

    public zzfc(zzbay<T> com_google_android_gms_internal_zzbay_T) {
        this.zzajL = com_google_android_gms_internal_zzbay_T;
    }

    public final void zzR(T t) {
        zzbay com_google_android_gms_internal_zzbay = this.zzajL;
        if (com_google_android_gms_internal_zzbay != null) {
            com_google_android_gms_internal_zzbay.setResult(t);
            this.zzajL = null;
        }
    }
}
