package com.google.android.gms.measurement;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.internal.zzcgb;
import com.google.android.gms.internal.zzcgd;

public final class AppMeasurementReceiver extends WakefulBroadcastReceiver implements zzcgd {
    private zzcgb zzboo;

    @MainThread
    public final void doStartService(Context context, Intent intent) {
        WakefulBroadcastReceiver.startWakefulService(context, intent);
    }

    @MainThread
    public final void onReceive(Context context, Intent intent) {
        if (this.zzboo == null) {
            this.zzboo = new zzcgb(this);
        }
        this.zzboo.onReceive(context, intent);
    }
}
