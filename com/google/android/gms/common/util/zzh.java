package com.google.android.gms.common.util;

import android.os.SystemClock;

public final class zzh implements zze {
    private static zzh EK;

    public static synchronized zze zzaxj() {
        zze com_google_android_gms_common_util_zze;
        synchronized (zzh.class) {
            if (EK == null) {
                EK = new zzh();
            }
            com_google_android_gms_common_util_zze = EK;
        }
        return com_google_android_gms_common_util_zze;
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public long elapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }

    public long nanoTime() {
        return System.nanoTime();
    }
}
