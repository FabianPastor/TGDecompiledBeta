package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;

public abstract class zzn {
    private static final Object zzaED = new Object();
    private static zzn zzaEE;

    public static zzn zzaC(Context context) {
        synchronized (zzaED) {
            if (zzaEE == null) {
                zzaEE = new zzo(context.getApplicationContext());
            }
        }
        return zzaEE;
    }

    public abstract boolean zza(ComponentName componentName, ServiceConnection serviceConnection, String str);

    public abstract boolean zza(String str, String str2, ServiceConnection serviceConnection, String str3);

    public abstract void zzb(ComponentName componentName, ServiceConnection serviceConnection, String str);

    public abstract void zzb(String str, String str2, ServiceConnection serviceConnection, String str3);
}
