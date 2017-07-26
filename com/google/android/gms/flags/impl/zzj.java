package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzcag;

public final class zzj {
    private static SharedPreferences zzaXQ = null;

    public static SharedPreferences zzaW(Context context) throws Exception {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (zzaXQ == null) {
                zzaXQ = (SharedPreferences) zzcag.zzb(new zzk(context));
            }
            sharedPreferences = zzaXQ;
        }
        return sharedPreferences;
    }
}
