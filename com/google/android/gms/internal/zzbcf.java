package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzj;
import java.lang.ref.WeakReference;

final class zzbcf implements zzj {
    private final boolean zzaCj;
    private final WeakReference<zzbcd> zzaDq;
    private final Api<?> zzayW;

    public zzbcf(zzbcd com_google_android_gms_internal_zzbcd, Api<?> api, boolean z) {
        this.zzaDq = new WeakReference(com_google_android_gms_internal_zzbcd);
        this.zzayW = api;
        this.zzaCj = z;
    }

    public final void zzf(@NonNull ConnectionResult connectionResult) {
        boolean z = false;
        zzbcd com_google_android_gms_internal_zzbcd = (zzbcd) this.zzaDq.get();
        if (com_google_android_gms_internal_zzbcd != null) {
            if (Looper.myLooper() == com_google_android_gms_internal_zzbcd.zzaCZ.zzaCl.getLooper()) {
                z = true;
            }
            zzbo.zza(z, (Object) "onReportServiceBinding must be called on the GoogleApiClient handler thread");
            com_google_android_gms_internal_zzbcd.zzaCv.lock();
            try {
                if (com_google_android_gms_internal_zzbcd.zzan(0)) {
                    if (!connectionResult.isSuccess()) {
                        com_google_android_gms_internal_zzbcd.zzb(connectionResult, this.zzayW, this.zzaCj);
                    }
                    if (com_google_android_gms_internal_zzbcd.zzpW()) {
                        com_google_android_gms_internal_zzbcd.zzpX();
                    }
                    com_google_android_gms_internal_zzbcd.zzaCv.unlock();
                }
            } finally {
                com_google_android_gms_internal_zzbcd.zzaCv.unlock();
            }
        }
    }
}
