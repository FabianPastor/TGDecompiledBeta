package com.google.android.gms.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.google.android.gms.internal.zzadg;

public class zzd {
    public static int zzC(Context context, String str) {
        return zzc(zzD(context, str));
    }

    @Nullable
    public static PackageInfo zzD(Context context, String str) {
        try {
            return zzadg.zzbi(context).getPackageInfo(str, 128);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static boolean zzE(Context context, String str) {
        "com.google.android.gms".equals(str);
        try {
            return (zzadg.zzbi(context).getApplicationInfo(str, 0).flags & 2097152) != 0;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static int zzc(PackageInfo packageInfo) {
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return -1;
        }
        Bundle bundle = packageInfo.applicationInfo.metaData;
        return bundle != null ? bundle.getInt("com.google.android.gms.version", -1) : -1;
    }
}
