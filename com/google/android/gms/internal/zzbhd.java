package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.util.zzq;

public final class zzbhd {
    private static Context zzgfe;
    private static Boolean zzgff;

    public static synchronized boolean zzcz(Context context) {
        boolean booleanValue;
        synchronized (zzbhd.class) {
            Context applicationContext = context.getApplicationContext();
            if (zzgfe == null || zzgff == null || zzgfe != applicationContext) {
                zzgff = null;
                if (zzq.isAtLeastO()) {
                    zzgff = Boolean.valueOf(applicationContext.getPackageManager().isInstantApp());
                } else {
                    try {
                        context.getClassLoader().loadClass("com.google.android.instantapps.supervisor.InstantAppsRuntime");
                        zzgff = Boolean.valueOf(true);
                    } catch (ClassNotFoundException e) {
                        zzgff = Boolean.valueOf(false);
                    }
                }
                zzgfe = applicationContext;
                booleanValue = zzgff.booleanValue();
            } else {
                booleanValue = zzgff.booleanValue();
            }
        }
        return booleanValue;
    }
}
