package com.google.android.gms.common.util;

import android.os.SystemClock;

public final class zzh implements zzd {
    private static zzh zzgef = new zzh();

    private zzh() {
    }

    public static zzd zzamg() {
        return zzgef;
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
