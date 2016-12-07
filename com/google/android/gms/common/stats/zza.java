package com.google.android.gms.common.stats;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.google.android.gms.common.util.zzd;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class zza {
    private static final Object zzaED = new Object();
    private static zza zzaFT;
    private static Integer zzaFZ;
    private final List<String> zzaFU;
    private final List<String> zzaFV;
    private final List<String> zzaFW;
    private final List<String> zzaFX;
    private zzd zzaFY;
    private zzd zzaGa;

    private zza() {
        if (zzyd()) {
            this.zzaFU = Collections.EMPTY_LIST;
            this.zzaFV = Collections.EMPTY_LIST;
            this.zzaFW = Collections.EMPTY_LIST;
            this.zzaFX = Collections.EMPTY_LIST;
            return;
        }
        String str = (String) com.google.android.gms.common.stats.zzb.zza.zzaGe.get();
        this.zzaFU = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        str = (String) com.google.android.gms.common.stats.zzb.zza.zzaGf.get();
        this.zzaFV = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        str = (String) com.google.android.gms.common.stats.zzb.zza.zzaGg.get();
        this.zzaFW = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        str = (String) com.google.android.gms.common.stats.zzb.zza.zzaGh.get();
        this.zzaFX = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        this.zzaFY = new zzd(1024, ((Long) com.google.android.gms.common.stats.zzb.zza.zzaGi.get()).longValue());
        this.zzaGa = new zzd(1024, ((Long) com.google.android.gms.common.stats.zzb.zza.zzaGi.get()).longValue());
    }

    private static int getLogLevel() {
        if (zzaFZ == null) {
            try {
                zzaFZ = Integer.valueOf(zzc.LOG_LEVEL_OFF);
            } catch (SecurityException e) {
                zzaFZ = Integer.valueOf(zzc.LOG_LEVEL_OFF);
            }
        }
        return zzaFZ.intValue();
    }

    private boolean zzc(Context context, Intent intent) {
        ComponentName component = intent.getComponent();
        return component == null ? false : zzd.zzx(context, component.getPackageName());
    }

    public static zza zzyc() {
        synchronized (zzaED) {
            if (zzaFT == null) {
                zzaFT = new zza();
            }
        }
        return zzaFT;
    }

    private boolean zzyd() {
        return getLogLevel() == zzc.LOG_LEVEL_OFF;
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
