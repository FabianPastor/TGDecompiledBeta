package com.google.android.gms.measurement;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.internal.zzatm;
import com.google.android.gms.internal.zzatm.zza;

public final class AppMeasurementReceiver extends WakefulBroadcastReceiver implements zza {
    private zzatm zzbpD;

    private zzatm zzJa() {
        if (this.zzbpD == null) {
            this.zzbpD = new zzatm(this);
        }
        return this.zzbpD;
    }

    @MainThread
    public void doStartService(Context context, Intent intent) {
        WakefulBroadcastReceiver.startWakefulService(context, intent);
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        zzJa().onReceive(context, intent);
    }
}
