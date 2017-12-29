package com.google.android.gms.iid;

import android.os.Binder;
import android.os.Process;
import android.util.Log;

public final class zzf extends Binder {
    private final zzb zziey;

    zzf(zzb com_google_android_gms_iid_zzb) {
        this.zziey = com_google_android_gms_iid_zzb;
    }

    public final void zza(zzd com_google_android_gms_iid_zzd) {
        if (Binder.getCallingUid() != Process.myUid()) {
            throw new SecurityException("Binding only allowed within app");
        }
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "service received new intent via bind strategy");
        }
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "intent being queued for bg execution");
        }
        this.zziey.zzieo.execute(new zzg(this, com_google_android_gms_iid_zzd));
    }
}
