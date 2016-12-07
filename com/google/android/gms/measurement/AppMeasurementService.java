package com.google.android.gms.measurement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.measurement.internal.zzaf;
import com.google.android.gms.measurement.internal.zzaf.zza;

public final class AppMeasurementService extends Service implements zza {
    private zzaf aqE;

    private zzaf zzbtf() {
        if (this.aqE == null) {
            this.aqE = new zzaf(this);
        }
        return this.aqE;
    }

    public boolean callServiceStopSelfResult(int i) {
        return stopSelfResult(i);
    }

    public Context getContext() {
        return this;
    }

    @MainThread
    public IBinder onBind(Intent intent) {
        return zzbtf().onBind(intent);
    }

    @MainThread
    public void onCreate() {
        super.onCreate();
        zzbtf().onCreate();
    }

    @MainThread
    public void onDestroy() {
        zzbtf().onDestroy();
        super.onDestroy();
    }

    @MainThread
    public void onRebind(Intent intent) {
        zzbtf().onRebind(intent);
    }

    @MainThread
    public int onStartCommand(Intent intent, int i, int i2) {
        zzbtf().onStartCommand(intent, i, i2);
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        return 2;
    }

    @MainThread
    public boolean onUnbind(Intent intent) {
        return zzbtf().onUnbind(intent);
    }
}
