package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

public final class zzi {
    private static Boolean EL;
    private static Boolean EM;
    private static Boolean EN;
    private static Boolean EO;

    public static boolean zzb(Resources resources) {
        boolean z = false;
        if (resources == null) {
            return false;
        }
        if (EL == null) {
            boolean z2 = (resources.getConfiguration().screenLayout & 15) > 3;
            if ((zzs.zzaxk() && z2) || zzc(resources)) {
                z = true;
            }
            EL = Boolean.valueOf(z);
        }
        return EL.booleanValue();
    }

    @TargetApi(13)
    private static boolean zzc(Resources resources) {
        if (EM == null) {
            Configuration configuration = resources.getConfiguration();
            boolean z = zzs.zzaxm() && (configuration.screenLayout & 15) <= 3 && configuration.smallestScreenWidthDp >= 600;
            EM = Boolean.valueOf(z);
        }
        return EM.booleanValue();
    }

    @TargetApi(20)
    public static boolean zzcl(Context context) {
        if (EN == null) {
            boolean z = zzs.zzaxs() && context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
            EN = Boolean.valueOf(z);
        }
        return EN.booleanValue();
    }

    @TargetApi(21)
    public static boolean zzcm(Context context) {
        if (EO == null) {
            boolean z = zzs.zzaxu() && context.getPackageManager().hasSystemFeature("cn.google");
            EO = Boolean.valueOf(z);
        }
        return EO.booleanValue();
    }
}
