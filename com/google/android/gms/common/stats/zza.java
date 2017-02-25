package com.google.android.gms.common.stats;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.google.android.gms.common.util.zzd;
import java.util.Collections;
import java.util.List;

public class zza {
    private static final Object zzaGb = new Object();
    private static zza zzaHq;
    private final List<String> zzaHr = Collections.EMPTY_LIST;
    private final List<String> zzaHs = Collections.EMPTY_LIST;
    private final List<String> zzaHt = Collections.EMPTY_LIST;
    private final List<String> zzaHu = Collections.EMPTY_LIST;

    private zza() {
    }

    private boolean zzc(Context context, Intent intent) {
        ComponentName component = intent.getComponent();
        return component == null ? false : zzd.zzE(context, component.getPackageName());
    }

    public static zza zzyJ() {
        synchronized (zzaGb) {
            if (zzaHq == null) {
                zzaHq = new zza();
            }
        }
        return zzaHq;
    }

    @SuppressLint({"UntrackedBindService"})
    public void zza(Context context, ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    public void zza(Context context, ServiceConnection serviceConnection, String str, Intent intent) {
    }

    public boolean zza(Context context, Intent intent, ServiceConnection serviceConnection, int i) {
        return zza(context, context.getClass().getName(), intent, serviceConnection, i);
    }

    @SuppressLint({"UntrackedBindService"})
    public boolean zza(Context context, String str, Intent intent, ServiceConnection serviceConnection, int i) {
        if (!zzc(context, intent)) {
            return context.bindService(intent, serviceConnection, i);
        }
        Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
        return false;
    }

    public void zzb(Context context, ServiceConnection serviceConnection) {
    }
}
