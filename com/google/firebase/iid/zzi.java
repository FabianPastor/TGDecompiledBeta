package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class zzi {
    private static zzi zznyx;
    private final Context zzair;
    private final ScheduledExecutorService zznyy;
    private zzk zznyz = new zzk();
    private int zznza = 1;

    private zzi(Context context, ScheduledExecutorService scheduledExecutorService) {
        this.zznyy = scheduledExecutorService;
        this.zzair = context.getApplicationContext();
    }

    private final synchronized <T> Task<T> zza(zzr<T> com_google_firebase_iid_zzr_T) {
        if (Log.isLoggable("MessengerIpcClient", 3)) {
            String valueOf = String.valueOf(com_google_firebase_iid_zzr_T);
            Log.d("MessengerIpcClient", new StringBuilder(String.valueOf(valueOf).length() + 9).append("Queueing ").append(valueOf).toString());
        }
        if (!this.zznyz.zzb(com_google_firebase_iid_zzr_T)) {
            this.zznyz = new zzk();
            this.zznyz.zzb(com_google_firebase_iid_zzr_T);
        }
        return com_google_firebase_iid_zzr_T.zzgrq.getTask();
    }

    private final synchronized int zzcja() {
        int i;
        i = this.zznza;
        this.zznza = i + 1;
        return i;
    }

    public static synchronized zzi zzev(Context context) {
        zzi com_google_firebase_iid_zzi;
        synchronized (zzi.class) {
            if (zznyx == null) {
                zznyx = new zzi(context, Executors.newSingleThreadScheduledExecutor());
            }
            com_google_firebase_iid_zzi = zznyx;
        }
        return com_google_firebase_iid_zzi;
    }

    public final Task<Bundle> zzi(int i, Bundle bundle) {
        return zza(new zzt(zzcja(), 1, bundle));
    }
}
