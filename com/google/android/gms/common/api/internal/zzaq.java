package com.google.android.gms.common.api.internal;

import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzj;
import java.lang.ref.WeakReference;

final class zzaq implements zzj {
    private final Api<?> zzfin;
    private final boolean zzfpg;
    private final WeakReference<zzao> zzfrm;

    public zzaq(zzao com_google_android_gms_common_api_internal_zzao, Api<?> api, boolean z) {
        this.zzfrm = new WeakReference(com_google_android_gms_common_api_internal_zzao);
        this.zzfin = api;
        this.zzfpg = z;
    }

    public final void zzf(ConnectionResult connectionResult) {
        boolean z = false;
        zzao com_google_android_gms_common_api_internal_zzao = (zzao) this.zzfrm.get();
        if (com_google_android_gms_common_api_internal_zzao != null) {
            if (Looper.myLooper() == com_google_android_gms_common_api_internal_zzao.zzfqv.zzfpi.getLooper()) {
                z = true;
            }
            zzbq.zza(z, "onReportServiceBinding must be called on the GoogleApiClient handler thread");
            com_google_android_gms_common_api_internal_zzao.zzfps.lock();
            try {
                if (com_google_android_gms_common_api_internal_zzao.zzbt(0)) {
                    if (!connectionResult.isSuccess()) {
                        com_google_android_gms_common_api_internal_zzao.zzb(connectionResult, this.zzfin, this.zzfpg);
                    }
                    if (com_google_android_gms_common_api_internal_zzao.zzaic()) {
                        com_google_android_gms_common_api_internal_zzao.zzaid();
                    }
                    com_google_android_gms_common_api_internal_zzao.zzfps.unlock();
                }
            } finally {
                com_google_android_gms_common_api_internal_zzao.zzfps.unlock();
            }
        }
    }
}
