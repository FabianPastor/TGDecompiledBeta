package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build.VERSION;
import java.io.File;

public class zzad {
    public static zzm zza(Context context) {
        return zza(context, null);
    }

    public static zzm zza(Context context, zzz com_google_android_gms_internal_zzz) {
        File file = new File(context.getCacheDir(), "volley");
        String str = "volley/0";
        try {
            String packageName = context.getPackageName();
            str = packageName + "/" + context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (NameNotFoundException e) {
        }
        if (com_google_android_gms_internal_zzz == null) {
            com_google_android_gms_internal_zzz = VERSION.SDK_INT >= 9 ? new zzaa() : new zzx(AndroidHttpClient.newInstance(str));
        }
        zzm com_google_android_gms_internal_zzm = new zzm(new zzw(file), new zzu(com_google_android_gms_internal_zzz));
        com_google_android_gms_internal_zzm.start();
        return com_google_android_gms_internal_zzm;
    }
}
