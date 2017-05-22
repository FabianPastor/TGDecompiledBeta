package com.google.android.gms.measurement;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.internal.zzaub;
import com.google.android.gms.internal.zzaub.zza;

public final class AppMeasurementReceiver extends WakefulBroadcastReceiver implements zza {
    private zzaub zzbqi;

    private zzaub zzJS() {
        if (this.zzbqi == null) {
            this.zzbqi = new zzaub(this);
        }
        return this.zzbqi;
    }

    @MainThread
    public void doStartService(Context context, Intent intent) {
        WakefulBroadcastReceiver.startWakefulService(context, intent);
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        zzJS().onReceive(context, intent);
    }
}
