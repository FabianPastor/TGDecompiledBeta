package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.zzf;
import com.google.android.gms.internal.zzacx;

public final class zzx {
    @TargetApi(19)
    public static boolean zzc(Context context, int i, String str) {
        return zzacx.zzaQ(context).zzg(i, str);
    }

    public static boolean zzf(Context context, int i) {
        boolean z = false;
        if (!zzc(context, i, "com.google.android.gms")) {
            return z;
        }
        try {
            return zzf.zzav(context).zzb(context.getPackageManager(), context.getPackageManager().getPackageInfo("com.google.android.gms", 64));
        } catch (NameNotFoundException e) {
            if (!Log.isLoggable("UidVerifier", 3)) {
                return z;
            }
            Log.d("UidVerifier", "Package manager can't find google play services package, defaulting to false");
            return z;
        }
    }
}
