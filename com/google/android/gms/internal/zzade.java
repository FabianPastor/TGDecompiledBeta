package com.google.android.gms.internal;

import android.content.Context;

public class zzade {
    private static Context zzaIy;
    private static Boolean zzaIz;

    public static synchronized boolean zzbg(Context context) {
        boolean booleanValue;
        synchronized (zzade.class) {
            Context applicationContext = context.getApplicationContext();
            if (zzaIy == null || zzaIz == null || zzaIy != applicationContext) {
                zzaIz = null;
                try {
                    context.getClassLoader().loadClass("com.google.android.instantapps.supervisor.InstantAppsRuntime");
                    zzaIz = Boolean.valueOf(true);
                } catch (ClassNotFoundException e) {
                    zzaIz = Boolean.valueOf(false);
                }
                zzaIy = applicationContext;
                booleanValue = zzaIz.booleanValue();
            } else {
                booleanValue = zzaIz.booleanValue();
            }
        }
        return booleanValue;
    }
}
