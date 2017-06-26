package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;

final class zzbes implements Runnable {
    private /* synthetic */ Result zzaFh;
    private /* synthetic */ zzber zzaFi;

    zzbes(zzber com_google_android_gms_internal_zzber, Result result) {
        this.zzaFi = com_google_android_gms_internal_zzber;
        this.zzaFh = result;
    }

    @WorkerThread
    public final void run() {
        GoogleApiClient googleApiClient;
        try {
            zzbbd.zzaBV.set(Boolean.valueOf(true));
            this.zzaFi.zzaFf.sendMessage(this.zzaFi.zzaFf.obtainMessage(0, this.zzaFi.zzaFa.onSuccess(this.zzaFh)));
            zzbbd.zzaBV.set(Boolean.valueOf(false));
            zzber.zzc(this.zzaFh);
            googleApiClient = (GoogleApiClient) this.zzaFi.zzaBY.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzaFi);
            }
        } catch (RuntimeException e) {
            this.zzaFi.zzaFf.sendMessage(this.zzaFi.zzaFf.obtainMessage(1, e));
            zzbbd.zzaBV.set(Boolean.valueOf(false));
            zzber.zzc(this.zzaFh);
            googleApiClient = (GoogleApiClient) this.zzaFi.zzaBY.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzaFi);
            }
        } catch (Throwable th) {
            Throwable th2 = th;
            zzbbd.zzaBV.set(Boolean.valueOf(false));
            zzber.zzc(this.zzaFh);
            googleApiClient = (GoogleApiClient) this.zzaFi.zzaBY.get();
            if (googleApiClient != null) {
                googleApiClient.zzb(this.zzaFi);
            }
        }
    }
}
