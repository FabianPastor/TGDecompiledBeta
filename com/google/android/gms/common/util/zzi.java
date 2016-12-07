package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import com.google.android.gms.common.zze;

public final class zzi {
    private static Boolean Gr;
    private static Boolean Gs;
    private static Boolean Gt;
    private static Boolean Gu;

    public static boolean zzaym() {
        boolean z = zze.xb;
        return "user".equals(Build.TYPE);
    }

    public static boolean zzb(Resources resources) {
        boolean z = false;
        if (resources == null) {
            return false;
        }
        if (Gr == null) {
            boolean z2 = (resources.getConfiguration().screenLayout & 15) > 3;
            if ((zzs.zzayn() && z2) || zzc(resources)) {
                z = true;
            }
            Gr = Boolean.valueOf(z);
        }
        return Gr.booleanValue();
    }

    @TargetApi(13)
    private static boolean zzc(Resources resources) {
        if (Gs == null) {
            Configuration configuration = resources.getConfiguration();
            boolean z = zzs.zzayp() && (configuration.screenLayout & 15) <= 3 && configuration.smallestScreenWidthDp >= 600;
            Gs = Boolean.valueOf(z);
        }
        return Gs.booleanValue();
    }

    @TargetApi(20)
    public static boolean zzci(Context context) {
        if (Gt == null) {
            boolean z = zzs.zzayv() && context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
            Gt = Boolean.valueOf(z);
        }
        return Gt.booleanValue();
    }

    @TargetApi(21)
    public static boolean zzcj(Context context) {
        if (Gu == null) {
            boolean z = zzs.zzayx() && context.getPackageManager().hasSystemFeature("cn.google");
            Gu = Boolean.valueOf(z);
        }
        return Gu.booleanValue();
    }
}
