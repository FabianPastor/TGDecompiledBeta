package com.google.android.gms.measurement;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.internal.zzcid;
import com.google.android.gms.internal.zzcif;

public final class AppMeasurementReceiver extends WakefulBroadcastReceiver implements zzcif {
    private zzcid zziwp;

    public final PendingResult doGoAsync() {
        return goAsync();
    }

    public final void doStartService(Context context, Intent intent) {
        WakefulBroadcastReceiver.startWakefulService(context, intent);
    }

    public final void onReceive(Context context, Intent intent) {
        if (this.zziwp == null) {
            this.zziwp = new zzcid(this);
        }
        this.zziwp.onReceive(context, intent);
    }
}
