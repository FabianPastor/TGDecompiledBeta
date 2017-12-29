package com.google.android.gms.common.stats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.google.android.gms.common.util.zzc;
import java.util.Collections;
import java.util.List;

public final class zza {
    private static final Object zzgai = new Object();
    private static volatile zza zzgcx;
    private static boolean zzgcy = false;
    private final List<String> zzgcz = Collections.EMPTY_LIST;
    private final List<String> zzgda = Collections.EMPTY_LIST;
    private final List<String> zzgdb = Collections.EMPTY_LIST;
    private final List<String> zzgdc = Collections.EMPTY_LIST;

    private zza() {
    }

    public static zza zzamc() {
        if (zzgcx == null) {
            synchronized (zzgai) {
                if (zzgcx == null) {
                    zzgcx = new zza();
                }
            }
        }
        return zzgcx;
    }

    public final boolean zza(Context context, Intent intent, ServiceConnection serviceConnection, int i) {
        return zza(context, context.getClass().getName(), intent, serviceConnection, i);
    }

    public final boolean zza(Context context, String str, Intent intent, ServiceConnection serviceConnection, int i) {
        ComponentName component = intent.getComponent();
        if (!(component == null ? false : zzc.zzz(context, component.getPackageName()))) {
            return context.bindService(intent, serviceConnection, i);
        }
        Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
        return false;
    }
}
