package com.google.android.gms.measurement;

import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.internal.zzcid;
import com.google.android.gms.internal.zzcif;

public final class AppMeasurementInstallReferrerReceiver extends BroadcastReceiver implements zzcif {
    private zzcid zziwp;

    public final PendingResult doGoAsync() {
        return goAsync();
    }

    public final void doStartService(Context context, Intent intent) {
    }

    public final void onReceive(Context context, Intent intent) {
        if (this.zziwp == null) {
            this.zziwp = new zzcid(this);
        }
        this.zziwp.onReceive(context, intent);
    }
}
