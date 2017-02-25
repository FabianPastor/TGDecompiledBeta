package com.google.android.gms.measurement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.internal.zzaum;
import com.google.android.gms.internal.zzaum.zza;

public final class AppMeasurementService extends Service implements zza {
    private zzaum zzbqo;

    private zzaum zzJS() {
        if (this.zzbqo == null) {
            this.zzbqo = new zzaum(this);
        }
        return this.zzbqo;
    }

    public boolean callServiceStopSelfResult(int i) {
        return stopSelfResult(i);
    }

    public Context getContext() {
        return this;
    }

    @MainThread
    public IBinder onBind(Intent intent) {
        return zzJS().onBind(intent);
    }

    @MainThread
    public void onCreate() {
        super.onCreate();
        zzJS().onCreate();
    }

    @MainThread
    public void onDestroy() {
        zzJS().onDestroy();
        super.onDestroy();
    }

    @MainThread
    public void onRebind(Intent intent) {
        zzJS().onRebind(intent);
    }

    @MainThread
    public int onStartCommand(Intent intent, int i, int i2) {
        zzJS().onStartCommand(intent, i, i2);
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        return 2;
    }

    @MainThread
    public boolean onUnbind(Intent intent) {
        return zzJS().onUnbind(intent);
    }
}
