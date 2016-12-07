package com.google.android.gms.measurement;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.measurement.internal.zzu;
import com.google.android.gms.measurement.internal.zzu.zza;

public final class AppMeasurementReceiver extends WakefulBroadcastReceiver implements zza {
    private zzu anu;

    private zzu zzbso() {
        if (this.anu == null) {
            this.anu = new zzu(this);
        }
        return this.anu;
    }

    @MainThread
    public void doStartService(Context context, Intent intent) {
        WakefulBroadcastReceiver.startWakefulService(context, intent);
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        zzbso().onReceive(context, intent);
    }
}
