package com.google.android.gms.measurement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import com.google.android.gms.internal.zzatm;
import com.google.android.gms.internal.zzatm.zza;

public final class AppMeasurementInstallReferrerReceiver extends BroadcastReceiver implements zza {
    private zzatm zzbpD;

    private zzatm zzJa() {
        if (this.zzbpD == null) {
            this.zzbpD = new zzatm(this);
        }
        return this.zzbpD;
    }

    public void doStartService(Context context, Intent intent) {
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        zzJa().onReceive(context, intent);
    }
}
