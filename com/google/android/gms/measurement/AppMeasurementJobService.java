package com.google.android.gms.measurement;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import com.google.android.gms.internal.zzcla;
import com.google.android.gms.internal.zzcle;

@TargetApi(24)
public final class AppMeasurementJobService extends JobService implements zzcle {
    private zzcla<AppMeasurementJobService> zziwq;

    private final zzcla<AppMeasurementJobService> zzawh() {
        if (this.zziwq == null) {
            this.zziwq = new zzcla(this);
        }
        return this.zziwq;
    }

    public final boolean callServiceStopSelfResult(int i) {
        throw new UnsupportedOperationException();
    }

    public final void onCreate() {
        super.onCreate();
        zzawh().onCreate();
    }

    public final void onDestroy() {
        zzawh().onDestroy();
        super.onDestroy();
    }

    public final void onRebind(Intent intent) {
        zzawh().onRebind(intent);
    }

    public final boolean onStartJob(JobParameters jobParameters) {
        return zzawh().onStartJob(jobParameters);
    }

    public final boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public final boolean onUnbind(Intent intent) {
        return zzawh().onUnbind(intent);
    }

    @TargetApi(24)
    public final void zza(JobParameters jobParameters, boolean z) {
        jobFinished(jobParameters, false);
    }

    public final void zzm(Intent intent) {
    }
}
