package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;

final class zzdh implements Runnable {
    private /* synthetic */ Result zzfve;
    private /* synthetic */ zzdg zzfvf;

    zzdh(zzdg com_google_android_gms_common_api_internal_zzdg, Result result) {
        this.zzfvf = com_google_android_gms_common_api_internal_zzdg;
        this.zzfve = result;
    }

    public final void run() {
        GoogleApiClient googleApiClient;
        try {
            BasePendingResult.zzfot.set(Boolean.valueOf(true));
            this.zzfvf.zzfvc.sendMessage(this.zzfvf.zzfvc.obtainMessage(0, this.zzfvf.zzfux.onSuccess(this.zzfve)));
            BasePendingResult.zzfot.set(Boolean.valueOf(false));
            zzdg.zzd(this.zzfve);
            googleApiClient = (GoogleApiClient) this.zzfvf.zzfow.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzfvf);
            }
        } catch (RuntimeException e) {
            this.zzfvf.zzfvc.sendMessage(this.zzfvf.zzfvc.obtainMessage(1, e));
            BasePendingResult.zzfot.set(Boolean.valueOf(false));
            zzdg.zzd(this.zzfve);
            googleApiClient = (GoogleApiClient) this.zzfvf.zzfow.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzfvf);
            }
        } catch (Throwable th) {
            Throwable th2 = th;
            BasePendingResult.zzfot.set(Boolean.valueOf(false));
            zzdg.zzd(this.zzfve);
            googleApiClient = (GoogleApiClient) this.zzfvf.zzfow.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzfvf);
            }
        }
    }
}
