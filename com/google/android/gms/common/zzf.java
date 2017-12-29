package com.google.android.gms.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzak;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.internal.zzbhf;

public class zzf {
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zzp.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    private static final zzf zzfkx = new zzf();

    zzf() {
    }

    public static Intent zza(Context context, int i, String str) {
        switch (i) {
            case 1:
            case 2:
                return (context == null || !zzi.zzct(context)) ? zzak.zzt("com.google.android.gms", zzu(context, str)) : zzak.zzaln();
            case 3:
                return zzak.zzgk("com.google.android.gms");
            default:
                return null;
        }
    }

    public static zzf zzafy() {
        return zzfkx;
    }

    public static void zzce(Context context) {
        zzp.zzce(context);
    }

    public static int zzcf(Context context) {
        return zzp.zzcf(context);
    }

    public static boolean zze(Context context, int i) {
        return zzp.zze(context, i);
    }

    private static String zzu(Context context, String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("gcore_");
        stringBuilder.append(GOOGLE_PLAY_SERVICES_VERSION_CODE);
        stringBuilder.append("-");
        if (!TextUtils.isEmpty(str)) {
            stringBuilder.append(str);
        }
        stringBuilder.append("-");
        if (context != null) {
            stringBuilder.append(context.getPackageName());
        }
        stringBuilder.append("-");
        if (context != null) {
            try {
                stringBuilder.append(zzbhf.zzdb(context).getPackageInfo(context.getPackageName(), 0).versionCode);
            } catch (NameNotFoundException e) {
            }
        }
        return stringBuilder.toString();
    }

    public PendingIntent getErrorResolutionPendingIntent(Context context, int i, int i2) {
        return zza(context, i, i2, null);
    }

    public String getErrorString(int i) {
        return zzp.getErrorString(i);
    }

    public int isGooglePlayServicesAvailable(Context context) {
        int isGooglePlayServicesAvailable = zzp.isGooglePlayServicesAvailable(context);
        return zzp.zze(context, isGooglePlayServicesAvailable) ? 18 : isGooglePlayServicesAvailable;
    }

    public boolean isUserResolvableError(int i) {
        return zzp.isUserRecoverableError(i);
    }

    public final PendingIntent zza(Context context, int i, int i2, String str) {
        Intent zza = zza(context, i, str);
        return zza == null ? null : PendingIntent.getActivity(context, i2, zza, 268435456);
    }

    @Deprecated
    public final Intent zzbp(int i) {
        return zza(null, i, null);
    }
}
