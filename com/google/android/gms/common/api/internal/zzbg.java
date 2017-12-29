package com.google.android.gms.common.api.internal;

import java.lang.ref.WeakReference;

final class zzbg extends zzby {
    private WeakReference<zzba> zzfsn;

    zzbg(zzba com_google_android_gms_common_api_internal_zzba) {
        this.zzfsn = new WeakReference(com_google_android_gms_common_api_internal_zzba);
    }

    public final void zzahg() {
        zzba com_google_android_gms_common_api_internal_zzba = (zzba) this.zzfsn.get();
        if (com_google_android_gms_common_api_internal_zzba != null) {
            com_google_android_gms_common_api_internal_zzba.resume();
        }
    }
}
