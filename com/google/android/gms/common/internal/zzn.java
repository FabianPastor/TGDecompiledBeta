package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;

public abstract class zzn {
    private static zzn CA;
    private static final Object Cz = new Object();

    public static zzn zzcf(Context context) {
        synchronized (Cz) {
            if (CA == null) {
                CA = new zzo(context.getApplicationContext());
            }
        }
        return CA;
    }

    public abstract boolean zza(ComponentName componentName, ServiceConnection serviceConnection, String str);

    public abstract boolean zza(String str, String str2, ServiceConnection serviceConnection, String str3);

    public abstract void zzb(ComponentName componentName, ServiceConnection serviceConnection, String str);

    public abstract void zzb(String str, String str2, ServiceConnection serviceConnection, String str3);
}
