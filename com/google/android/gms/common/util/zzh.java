package com.google.android.gms.common.util;

import android.os.SystemClock;

public class zzh implements zze {
    private static zzh zzaGK = new zzh();

    private zzh() {
    }

    public static zze zzyv() {
        return zzaGK;
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
