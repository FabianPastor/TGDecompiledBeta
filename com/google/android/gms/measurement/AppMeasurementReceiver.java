package com.google.android.gms.measurement;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.measurement.internal.zzu;
import com.google.android.gms.measurement.internal.zzu.zza;

public final class AppMeasurementReceiver extends WakefulBroadcastReceiver implements zza {
    private zzu aqD;

    private zzu zzbte() {
        if (this.aqD == null) {
            this.aqD = new zzu(this);
        }
        return this.aqD;
    }

    @MainThread
    public void doStartService(Context context, Intent intent) {
        WakefulBroadcastReceiver.startWakefulService(context, intent);
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        zzbte().onReceive(context, intent);
    }
}
