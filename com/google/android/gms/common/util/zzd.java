package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.google.android.gms.internal.zzsz;

public class zzd {
    public static int zza(PackageInfo packageInfo) {
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return -1;
        }
        Bundle bundle = packageInfo.applicationInfo.metaData;
        return bundle != null ? bundle.getInt("com.google.android.gms.version", -1) : -1;
    }

    public static boolean zzayi() {
        return false;
    }

    public static int zzv(Context context, String str) {
        return zza(zzw(context, str));
    }

    @Nullable
    public static PackageInfo zzw(Context context, String str) {
        try {
            return zzsz.zzco(context).getPackageInfo(str, 128);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    @TargetApi(12)
    public static boolean zzx(Context context, String str) {
        if (!zzs.zzayo()) {
            return false;
        }
        if ("com.google.android.gms".equals(str) && zzayi()) {
            return false;
        }
        try {
            return (zzsz.zzco(context).getApplicationInfo(str, 0).flags & 2097152) != 0;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
