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
    private static final Object El = new Object();
    private static Integer FF;
    private static zza Fz;
    private final List<String> FA;
    private final List<String> FB;
    private final List<String> FC;
    private final List<String> FD;
    private zzd FE;
    private zzd FG;

    private zza() {
        if (zzaxs()) {
            this.FA = Collections.EMPTY_LIST;
            this.FB = Collections.EMPTY_LIST;
            this.FC = Collections.EMPTY_LIST;
            this.FD = Collections.EMPTY_LIST;
            return;
        }
        String str = (String) com.google.android.gms.common.stats.zzb.zza.FK.get();
        this.FA = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        str = (String) com.google.android.gms.common.stats.zzb.zza.FL.get();
        this.FB = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        str = (String) com.google.android.gms.common.stats.zzb.zza.FM.get();
        this.FC = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        str = (String) com.google.android.gms.common.stats.zzb.zza.FN.get();
        this.FD = str == null ? Collections.EMPTY_LIST : Arrays.asList(str.split(","));
        this.FE = new zzd(1024, ((Long) com.google.android.gms.common.stats.zzb.zza.FO.get()).longValue());
        this.FG = new zzd(1024, ((Long) com.google.android.gms.common.stats.zzb.zza.FO.get()).longValue());
    }

    private static int getLogLevel() {
        if (FF == null) {
            try {
                FF = Integer.valueOf(zzd.zzayi() ? ((Integer) com.google.android.gms.common.stats.zzb.zza.FJ.get()).intValue() : zzc.LOG_LEVEL_OFF);
            } catch (SecurityException e) {
                FF = Integer.valueOf(zzc.LOG_LEVEL_OFF);
            }
        }
        return FF.intValue();
    }

    public static zza zzaxr() {
        synchronized (El) {
            if (Fz == null) {
                Fz = new zza();
            }
        }
        return Fz;
    }

    private boolean zzaxs() {
        return getLogLevel() == zzc.LOG_LEVEL_OFF;
    }

    private boolean zzc(Context context, Intent intent) {
        ComponentName component = intent.getComponent();
        return component == null ? false : zzd.zzx(context, component.getPackageName());
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
