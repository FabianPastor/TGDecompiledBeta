package com.google.android.gms.measurement;

import android.app.Service;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.internal.zzciw;
import com.google.android.gms.internal.zzciz;

public final class AppMeasurementService extends Service implements zzciz {
    private zzciw zzbop;

    private final zzciw zzwm() {
        if (this.zzbop == null) {
            this.zzbop = new zzciw(this);
        }
        return this.zzbop;
    }

    public final boolean callServiceStopSelfResult(int i) {
        return stopSelfResult(i);
    }

    public final Context getContext() {
        return this;
    }

    @MainThread
    public final IBinder onBind(Intent intent) {
        return zzwm().onBind(intent);
    }

    @MainThread
    public final void onCreate() {
        super.onCreate();
        zzwm().onCreate();
    }

    @MainThread
    public final void onDestroy() {
        zzwm().onDestroy();
        super.onDestroy();
    }

    @MainThread
    public final void onRebind(Intent intent) {
        zzwm().onRebind(intent);
    }

    @MainThread
    public final int onStartCommand(Intent intent, int i, int i2) {
        zzwm().onStartCommand(intent, i, i2);
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        return 2;
    }

    @MainThread
    public final boolean onUnbind(Intent intent) {
        return zzwm().onUnbind(intent);
    }

    public final void zza(JobParameters jobParameters, boolean z) {
        throw new UnsupportedOperationException();
    }
}
