package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzcbc;

public final class zzj {
    private static SharedPreferences zzhje = null;

    public static SharedPreferences zzdi(Context context) throws Exception {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (zzhje == null) {
                zzhje = (SharedPreferences) zzcbc.zzb(new zzk(context));
            }
            sharedPreferences = zzhje;
        }
        return sharedPreferences;
    }
}
