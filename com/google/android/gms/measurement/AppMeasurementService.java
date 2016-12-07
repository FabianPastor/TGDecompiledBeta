package com.google.android.gms.measurement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.measurement.internal.zzae;
import com.google.android.gms.measurement.internal.zzae.zza;

public final class AppMeasurementService extends Service implements zza {
    private zzae anv;

    private zzae zzbsp() {
        if (this.anv == null) {
            this.anv = new zzae(this);
        }
        return this.anv;
    }

    public boolean callServiceStopSelfResult(int i) {
        return stopSelfResult(i);
    }

    public Context getContext() {
        return this;
    }

    @MainThread
    public IBinder onBind(Intent intent) {
        return zzbsp().onBind(intent);
    }

    @MainThread
    public void onCreate() {
        super.onCreate();
        zzbsp().onCreate();
    }

    @MainThread
    public void onDestroy() {
        zzbsp().onDestroy();
        super.onDestroy();
    }

    @MainThread
    public void onRebind(Intent intent) {
        zzbsp().onRebind(intent);
    }

    @MainThread
    public int onStartCommand(Intent intent, int i, int i2) {
        int onStartCommand = zzbsp().onStartCommand(intent, i, i2);
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        return onStartCommand;
    }

    @MainThread
    public boolean onUnbind(Intent intent) {
        return zzbsp().onUnbind(intent);
    }
}
