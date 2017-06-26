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

public final class zza {
    private static final Object zzaHL = new Object();
    private static volatile zza zzaJa;
    private final List<String> zzaJb = Collections.EMPTY_LIST;
    private final List<String> zzaJc = Collections.EMPTY_LIST;
    private final List<String> zzaJd = Collections.EMPTY_LIST;
    private final List<String> zzaJe = Collections.EMPTY_LIST;

    private zza() {
    }

    @SuppressLint({"UntrackedBindService"})
    public static boolean zza(Context context, String str, Intent intent, ServiceConnection serviceConnection, int i) {
        ComponentName component = intent.getComponent();
        if (!(component == null ? false : zzd.zzC(context, component.getPackageName()))) {
            return context.bindService(intent, serviceConnection, i);
        }
        Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
        return false;
    }

    public static zza zzrU() {
        if (zzaJa == null) {
            synchronized (zzaHL) {
                if (zzaJa == null) {
                    zzaJa = new zza();
                }
            }
        }
        return zzaJa;
    }

    public final boolean zza(Context context, Intent intent, ServiceConnection serviceConnection, int i) {
        return zza(context, context.getClass().getName(), intent, serviceConnection, i);
    }
}
