package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

public final class zzj {
    private static Boolean zzaJJ;
    private static Boolean zzaJK;
    private static Boolean zzaJL;
    private static Boolean zzaJM;
    private static Boolean zzaJN;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean zza(Resources resources) {
        boolean z = false;
        if (resources == null) {
            return false;
        }
        if (zzaJJ == null) {
            if (!((resources.getConfiguration().screenLayout & 15) > 3)) {
                if (zzaJK == null) {
                    Configuration configuration = resources.getConfiguration();
                    boolean z2 = (configuration.screenLayout & 15) <= 3 && configuration.smallestScreenWidthDp >= 600;
                    zzaJK = Boolean.valueOf(z2);
                }
            }
            z = true;
            zzaJJ = Boolean.valueOf(z);
        }
        return zzaJJ.booleanValue();
    }

    @TargetApi(20)
    public static boolean zzaG(Context context) {
        if (zzaJL == null) {
            boolean z = zzq.zzsd() && context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
            zzaJL = Boolean.valueOf(z);
        }
        return zzaJL.booleanValue();
    }

    @TargetApi(24)
    public static boolean zzaH(Context context) {
        return (!zzq.isAtLeastN() || zzaI(context)) && zzaG(context);
    }

    @TargetApi(21)
    public static boolean zzaI(Context context) {
        if (zzaJM == null) {
            boolean z = zzq.zzse() && context.getPackageManager().hasSystemFeature("cn.google");
            zzaJM = Boolean.valueOf(z);
        }
        return zzaJM.booleanValue();
    }

    public static boolean zzaJ(Context context) {
        if (zzaJN == null) {
            boolean z = context.getPackageManager().hasSystemFeature("android.hardware.type.iot") || context.getPackageManager().hasSystemFeature("android.hardware.type.embedded");
            zzaJN = Boolean.valueOf(z);
        }
        return zzaJN.booleanValue();
    }
}
