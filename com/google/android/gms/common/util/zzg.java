package com.google.android.gms.common.util;

import android.content.Context;
import android.os.DropBoxManager;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;

public final class zzg {
    private static final String[] zzaHT = new String[]{"android.", "com.android.", "dalvik.", "java.", "javax."};
    private static DropBoxManager zzaHU = null;
    private static boolean zzaHV = false;
    private static int zzaHW = -1;
    private static int zzaHX = 0;

    public static boolean zza(Context context, Throwable th) {
        boolean zzzb;
        try {
            zzac.zzw(context);
            zzac.zzw(th);
        } catch (Throwable e) {
            try {
                zzzb = zzzb();
            } catch (Throwable e2) {
                Log.e("CrashUtils", "Error determining which process we're running in!", e2);
                zzzb = false;
            }
            if (zzzb) {
                throw e;
            }
            Log.e("CrashUtils", "Error adding exception to DropBox!", e);
        }
        return false;
    }

    private static boolean zzzb() {
        return false;
    }
}
