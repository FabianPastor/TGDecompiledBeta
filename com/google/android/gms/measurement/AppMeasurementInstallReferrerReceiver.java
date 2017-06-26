package com.google.android.gms.measurement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import com.google.android.gms.internal.zzcgb;
import com.google.android.gms.internal.zzcgd;

public final class AppMeasurementInstallReferrerReceiver extends BroadcastReceiver implements zzcgd {
    private zzcgb zzboo;

    public final void doStartService(Context context, Intent intent) {
    }

    @MainThread
    public final void onReceive(Context context, Intent intent) {
        if (this.zzboo == null) {
            this.zzboo = new zzcgb(this);
        }
        this.zzboo.onReceive(context, intent);
    }
}
