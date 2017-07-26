package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;

final class zzbet implements Runnable {
    private /* synthetic */ Result zzaFh;
    private /* synthetic */ zzbes zzaFi;

    zzbet(zzbes com_google_android_gms_internal_zzbes, Result result) {
        this.zzaFi = com_google_android_gms_internal_zzbes;
        this.zzaFh = result;
    }

    @WorkerThread
    public final void run() {
        GoogleApiClient googleApiClient;
        try {
            zzbbe.zzaBV.set(Boolean.valueOf(true));
            this.zzaFi.zzaFf.sendMessage(this.zzaFi.zzaFf.obtainMessage(0, this.zzaFi.zzaFa.onSuccess(this.zzaFh)));
            zzbbe.zzaBV.set(Boolean.valueOf(false));
            zzbes.zzc(this.zzaFh);
            googleApiClient = (GoogleApiClient) this.zzaFi.zzaBY.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzaFi);
            }
        } catch (RuntimeException e) {
            this.zzaFi.zzaFf.sendMessage(this.zzaFi.zzaFf.obtainMessage(1, e));
            zzbbe.zzaBV.set(Boolean.valueOf(false));
            zzbes.zzc(this.zzaFh);
            googleApiClient = (GoogleApiClient) this.zzaFi.zzaBY.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzaFi);
            }
        } catch (Throwable th) {
            Throwable th2 = th;
            zzbbe.zzaBV.set(Boolean.valueOf(false));
            zzbes.zzc(this.zzaFh);
            googleApiClient = (GoogleApiClient) this.zzaFi.zzaBY.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzaFi);
            }
        }
    }
}
