package com.google.android.gms.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzn;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.internal.zzsz;

public class zzc {
    public static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    private static final zzc wT = new zzc();

    zzc() {
    }

    public static zzc zzaql() {
        return wT;
    }

    static String zzt(@Nullable Context context, @Nullable String str) {
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
                stringBuilder.append(zzsz.zzco(context).getPackageInfo(context.getPackageName(), 0).versionCode);
            } catch (NameNotFoundException e) {
            }
        }
        return stringBuilder.toString();
    }

    @Nullable
    public PendingIntent getErrorResolutionPendingIntent(Context context, int i, int i2) {
        return zza(context, i, i2, null);
    }

    public String getErrorString(int i) {
        return zze.getErrorString(i);
    }

    @Nullable
    public String getOpenSourceSoftwareLicenseInfo(Context context) {
        return zze.getOpenSourceSoftwareLicenseInfo(context);
    }

    public int isGooglePlayServicesAvailable(Context context) {
        int isGooglePlayServicesAvailable = zze.isGooglePlayServicesAvailable(context);
        return zze.zzd(context, isGooglePlayServicesAvailable) ? 18 : isGooglePlayServicesAvailable;
    }

    public boolean isUserResolvableError(int i) {
        return zze.isUserRecoverableError(i);
    }

    @Nullable
    public PendingIntent zza(Context context, int i, int i2, @Nullable String str) {
        Intent zzb = zzb(context, i, str);
        return zzb == null ? null : PendingIntent.getActivity(context, i2, zzb, 268435456);
    }

    @Nullable
    public Intent zzb(Context context, int i, @Nullable String str) {
        switch (i) {
            case 1:
            case 2:
                return (context == null || !zzi.zzci(context)) ? zzn.zzac("com.google.android.gms", zzt(context, str)) : zzn.zzawg();
            case 3:
                return zzn.zzhy("com.google.android.gms");
            default:
                return null;
        }
    }

    public int zzbk(Context context) {
        return zze.zzbk(context);
    }

    public void zzbm(Context context) throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        zze.zzaz(context);
    }

    public void zzbn(Context context) {
        zze.zzbn(context);
    }

    public boolean zzd(Context context, int i) {
        return zze.zzd(context, i);
    }

    @Nullable
    @Deprecated
    public Intent zzfp(int i) {
        return zzb(null, i, null);
    }

    public boolean zzs(Context context, String str) {
        return zze.zzs(context, str);
    }
}
