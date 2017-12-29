package com.google.android.gms.iid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

final class zzn extends BroadcastReceiver {
    private /* synthetic */ zzl zzigd;

    zzn(zzl com_google_android_gms_iid_zzl) {
        this.zzigd = com_google_android_gms_iid_zzl;
    }

    public final void onReceive(Context context, Intent intent) {
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            Log.d("InstanceID/Rpc", "Received GSF callback via dynamic receiver");
        }
        this.zzigd.zzk(intent);
    }
}
