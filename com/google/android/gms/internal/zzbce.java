package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzj;
import java.lang.ref.WeakReference;

final class zzbce implements zzj {
    private final boolean zzaCj;
    private final WeakReference<zzbcc> zzaDq;
    private final Api<?> zzayW;

    public zzbce(zzbcc com_google_android_gms_internal_zzbcc, Api<?> api, boolean z) {
        this.zzaDq = new WeakReference(com_google_android_gms_internal_zzbcc);
        this.zzayW = api;
        this.zzaCj = z;
    }

    public final void zzf(@NonNull ConnectionResult connectionResult) {
        boolean z = false;
        zzbcc com_google_android_gms_internal_zzbcc = (zzbcc) this.zzaDq.get();
        if (com_google_android_gms_internal_zzbcc != null) {
            if (Looper.myLooper() == com_google_android_gms_internal_zzbcc.zzaCZ.zzaCl.getLooper()) {
                z = true;
            }
            zzbo.zza(z, (Object) "onReportServiceBinding must be called on the GoogleApiClient handler thread");
            com_google_android_gms_internal_zzbcc.zzaCv.lock();
            try {
                if (com_google_android_gms_internal_zzbcc.zzan(0)) {
                    if (!connectionResult.isSuccess()) {
                        com_google_android_gms_internal_zzbcc.zzb(connectionResult, this.zzayW, this.zzaCj);
                    }
                    if (com_google_android_gms_internal_zzbcc.zzpW()) {
                        com_google_android_gms_internal_zzbcc.zzpX();
                    }
                    com_google_android_gms_internal_zzbcc.zzaCv.unlock();
                }
            } finally {
                com_google_android_gms_internal_zzbcc.zzaCv.unlock();
            }
        }
    }
}
