package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;

public abstract class zzl {
    private static final Object El = new Object();
    private static zzl Em;

    public static zzl zzcc(Context context) {
        synchronized (El) {
            if (Em == null) {
                Em = new zzm(context.getApplicationContext());
            }
        }
        return Em;
    }

    public abstract boolean zza(ComponentName componentName, ServiceConnection serviceConnection, String str);

    public abstract boolean zza(String str, String str2, ServiceConnection serviceConnection, String str3);

    public abstract void zzb(ComponentName componentName, ServiceConnection serviceConnection, String str);

    public abstract void zzb(String str, String str2, ServiceConnection serviceConnection, String str3);
}
