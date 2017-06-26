package com.google.android.gms.common.util;

import android.os.SystemClock;

public final class zzi implements zze {
    private static zzi zzaJI = new zzi();

    private zzi() {
    }

    public static zze zzrY() {
        return zzaJI;
    }

    public final long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public final long elapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }

    public final long nanoTime() {
        return System.nanoTime();
    }
}
