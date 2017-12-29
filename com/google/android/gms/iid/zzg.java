package com.google.android.gms.iid;

import android.util.Log;

final class zzg implements Runnable {
    private /* synthetic */ zzd zziez;
    private /* synthetic */ zzf zzifa;

    zzg(zzf com_google_android_gms_iid_zzf, zzd com_google_android_gms_iid_zzd) {
        this.zzifa = com_google_android_gms_iid_zzf;
        this.zziez = com_google_android_gms_iid_zzd;
    }

    public final void run() {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "bg processing of the intent starting now");
        }
        this.zzifa.zziey.handleIntent(this.zziez.intent);
        this.zziez.finish();
    }
}
