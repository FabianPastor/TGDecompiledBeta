package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import com.google.android.gms.common.zzg;

public final class zzj {
    private static Boolean zzaHZ;
    private static Boolean zzaIa;
    private static Boolean zzaIb;
    private static Boolean zzaIc;
    private static Boolean zzaId;

    @TargetApi(20)
    public static boolean zzaZ(Context context) {
        if (zzaIb == null) {
            boolean z = zzt.zzzm() && context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
            zzaIb = Boolean.valueOf(z);
        }
        return zzaIb.booleanValue();
    }

    public static boolean zzb(Resources resources) {
        boolean z = false;
        if (resources == null) {
            return false;
        }
        if (zzaHZ == null) {
            if (((resources.getConfiguration().screenLayout & 15) > 3) || zzc(resources)) {
                z = true;
            }
            zzaHZ = Boolean.valueOf(z);
        }
        return zzaHZ.booleanValue();
    }

    @TargetApi(24)
    public static boolean zzba(Context context) {
        return (!zzt.isAtLeastN() || zzbb(context)) && zzaZ(context);
    }

    @TargetApi(21)
    public static boolean zzbb(Context context) {
        if (zzaIc == null) {
            boolean z = zzt.zzzo() && context.getPackageManager().hasSystemFeature("cn.google");
            zzaIc = Boolean.valueOf(z);
        }
        return zzaIc.booleanValue();
    }

    public static boolean zzbc(Context context) {
        if (zzaId == null) {
            zzaId = Boolean.valueOf(context.getPackageManager().hasSystemFeature("android.hardware.type.iot"));
        }
        return zzaId.booleanValue();
    }

    private static boolean zzc(Resources resources) {
        if (zzaIa == null) {
            Configuration configuration = resources.getConfiguration();
            boolean z = (configuration.screenLayout & 15) <= 3 && configuration.smallestScreenWidthDp >= 600;
            zzaIa = Boolean.valueOf(z);
        }
        return zzaIa.booleanValue();
    }

    public static boolean zzzd() {
        boolean z = zzg.zzayx;
        return "user".equals(Build.TYPE);
    }
}
