package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build.VERSION;
import java.io.File;

public final class zzas {
    public static zzs zza(Context context, zzan com_google_android_gms_internal_zzan) {
        zzan com_google_android_gms_internal_zzao;
        File file = new File(context.getCacheDir(), "volley");
        String str = "volley/0";
        try {
            String packageName = context.getPackageName();
            str = new StringBuilder(String.valueOf(packageName).length() + 12).append(packageName).append("/").append(context.getPackageManager().getPackageInfo(packageName, 0).versionCode).toString();
        } catch (NameNotFoundException e) {
        }
        if (VERSION.SDK_INT >= 9) {
            com_google_android_gms_internal_zzao = new zzao();
        } else {
            Object com_google_android_gms_internal_zzak = new zzak(AndroidHttpClient.newInstance(str));
        }
        zzs com_google_android_gms_internal_zzs = new zzs(new zzag(file), new zzad(com_google_android_gms_internal_zzao));
        com_google_android_gms_internal_zzs.start();
        return com_google_android_gms_internal_zzs;
    }
}
