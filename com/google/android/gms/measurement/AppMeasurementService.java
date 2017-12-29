package com.google.android.gms.measurement;

import android.app.Service;
import android.app.job.JobParameters;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.internal.zzcla;
import com.google.android.gms.internal.zzcle;

public final class AppMeasurementService extends Service implements zzcle {
    private zzcla<AppMeasurementService> zziwq;

    private final zzcla<AppMeasurementService> zzawh() {
        if (this.zziwq == null) {
            this.zziwq = new zzcla(this);
        }
        return this.zziwq;
    }

    public final boolean callServiceStopSelfResult(int i) {
        return stopSelfResult(i);
    }

    public final IBinder onBind(Intent intent) {
        return zzawh().onBind(intent);
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

    public final int onStartCommand(Intent intent, int i, int i2) {
        return zzawh().onStartCommand(intent, i, i2);
    }

    public final boolean onUnbind(Intent intent) {
        return zzawh().onUnbind(intent);
    }

    public final void zza(JobParameters jobParameters, boolean z) {
        throw new UnsupportedOperationException();
    }

    public final void zzm(Intent intent) {
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
