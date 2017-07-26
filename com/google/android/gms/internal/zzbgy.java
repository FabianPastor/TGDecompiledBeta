package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import com.google.android.gms.common.util.zzq;
import java.lang.reflect.InvocationTargetException;

public final class zzbgy {
    private static Context zzaKh;
    private static Boolean zzaKi;

    public static synchronized boolean zzaN(Context context) {
        boolean booleanValue;
        synchronized (zzbgy.class) {
            Context applicationContext = context.getApplicationContext();
            if (zzaKh == null || zzaKi == null || zzaKh != applicationContext) {
                zzaKi = null;
                if (zzq.isAtLeastO()) {
                    try {
                        zzaKi = (Boolean) PackageManager.class.getDeclaredMethod("isInstantApp", new Class[0]).invoke(applicationContext.getPackageManager(), new Object[0]);
                    } catch (NoSuchMethodException e) {
                        zzaKi = Boolean.valueOf(false);
                        zzaKh = applicationContext;
                        booleanValue = zzaKi.booleanValue();
                        return booleanValue;
                    } catch (InvocationTargetException e2) {
                        zzaKi = Boolean.valueOf(false);
                        zzaKh = applicationContext;
                        booleanValue = zzaKi.booleanValue();
                        return booleanValue;
                    } catch (IllegalAccessException e3) {
                        zzaKi = Boolean.valueOf(false);
                        zzaKh = applicationContext;
                        booleanValue = zzaKi.booleanValue();
                        return booleanValue;
                    }
                }
                try {
                    context.getClassLoader().loadClass("com.google.android.instantapps.supervisor.InstantAppsRuntime");
                    zzaKi = Boolean.valueOf(true);
                } catch (ClassNotFoundException e4) {
                    zzaKi = Boolean.valueOf(false);
                }
                zzaKh = applicationContext;
                booleanValue = zzaKi.booleanValue();
            } else {
                booleanValue = zzaKi.booleanValue();
            }
        }
        return booleanValue;
    }
}
