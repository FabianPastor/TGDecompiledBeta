package com.google.android.gms.measurement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import com.google.android.gms.internal.zzcgc;
import com.google.android.gms.internal.zzcge;

public final class AppMeasurementInstallReferrerReceiver extends BroadcastReceiver implements zzcge {
    private zzcgc zzboo;

    public final void doStartService(Context context, Intent intent) {
    }

    @MainThread
    public final void onReceive(Context context, Intent intent) {
        if (this.zzboo == null) {
            this.zzboo = new zzcgc(this);
        }
        this.zzboo.onReceive(context, intent);
    }
}
