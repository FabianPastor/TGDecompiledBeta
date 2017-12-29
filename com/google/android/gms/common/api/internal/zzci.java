package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.internal.zzbq;

public final class zzci<L> {
    private final zzcj zzfuj;
    private volatile L zzfuk;

    public final void clear() {
        this.zzfuk = null;
    }

    public final void zza(zzcl<? super L> com_google_android_gms_common_api_internal_zzcl__super_L) {
        zzbq.checkNotNull(com_google_android_gms_common_api_internal_zzcl__super_L, "Notifier must not be null");
        this.zzfuj.sendMessage(this.zzfuj.obtainMessage(1, com_google_android_gms_common_api_internal_zzcl__super_L));
    }

    final void zzb(zzcl<? super L> com_google_android_gms_common_api_internal_zzcl__super_L) {
        Object obj = this.zzfuk;
        if (obj == null) {
            com_google_android_gms_common_api_internal_zzcl__super_L.zzahz();
            return;
        }
        try {
            com_google_android_gms_common_api_internal_zzcl__super_L.zzu(obj);
        } catch (RuntimeException e) {
            com_google_android_gms_common_api_internal_zzcl__super_L.zzahz();
            throw e;
        }
    }
}
