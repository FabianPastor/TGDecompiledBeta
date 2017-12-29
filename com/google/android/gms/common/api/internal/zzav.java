package com.google.android.gms.common.api.internal;

import com.google.android.gms.internal.zzcxi;
import com.google.android.gms.internal.zzcxq;
import java.lang.ref.WeakReference;

final class zzav extends zzcxi {
    private final WeakReference<zzao> zzfrm;

    zzav(zzao com_google_android_gms_common_api_internal_zzao) {
        this.zzfrm = new WeakReference(com_google_android_gms_common_api_internal_zzao);
    }

    public final void zzb(zzcxq com_google_android_gms_internal_zzcxq) {
        zzao com_google_android_gms_common_api_internal_zzao = (zzao) this.zzfrm.get();
        if (com_google_android_gms_common_api_internal_zzao != null) {
            com_google_android_gms_common_api_internal_zzao.zzfqv.zza(new zzaw(this, com_google_android_gms_common_api_internal_zzao, com_google_android_gms_common_api_internal_zzao, com_google_android_gms_internal_zzcxq));
        }
    }
}
