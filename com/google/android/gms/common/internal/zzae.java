package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;

public abstract class zzae {
    private static final Object zzaHL = new Object();
    private static zzae zzaHM;

    public static zzae zzaC(Context context) {
        synchronized (zzaHL) {
            if (zzaHM == null) {
                zzaHM = new zzag(context.getApplicationContext());
            }
        }
        return zzaHM;
    }

    public final void zza(String str, String str2, ServiceConnection serviceConnection, String str3) {
        zzb(new zzaf(str, str2), serviceConnection, str3);
    }

    public final boolean zza(ComponentName componentName, ServiceConnection serviceConnection, String str) {
        return zza(new zzaf(componentName), serviceConnection, str);
    }

    protected abstract boolean zza(zzaf com_google_android_gms_common_internal_zzaf, ServiceConnection serviceConnection, String str);

    public final void zzb(ComponentName componentName, ServiceConnection serviceConnection, String str) {
        zzb(new zzaf(componentName), serviceConnection, str);
    }

    protected abstract void zzb(zzaf com_google_android_gms_common_internal_zzaf, ServiceConnection serviceConnection, String str);
}
