package com.google.android.gms.common.util;

import android.os.SystemClock;

public class zzi implements zze {
    private static zzi zzaHY = new zzi();

    private zzi() {
    }

    public static zze zzzc() {
        return zzaHY;
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
