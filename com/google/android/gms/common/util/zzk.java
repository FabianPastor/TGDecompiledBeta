package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

public final class zzk {
    private static IntentFilter zzaIe = new IntentFilter("android.intent.action.BATTERY_CHANGED");
    private static long zzaIf;
    private static float zzaIg = Float.NaN;

    @TargetApi(20)
    public static boolean zzb(PowerManager powerManager) {
        return zzt.zzzm() ? powerManager.isInteractive() : powerManager.isScreenOn();
    }

    @TargetApi(20)
    public static int zzbd(Context context) {
        int i = 1;
        if (context == null || context.getApplicationContext() == null) {
            return -1;
        }
        Intent registerReceiver = context.getApplicationContext().registerReceiver(null, zzaIe);
        int i2 = ((registerReceiver == null ? 0 : registerReceiver.getIntExtra("plugged", 0)) & 7) != 0 ? 1 : 0;
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        if (powerManager == null) {
            return -1;
        }
        int i3 = (zzb(powerManager) ? 1 : 0) << 1;
        if (i2 == 0) {
            i = 0;
        }
        return i3 | i;
    }

    public static synchronized float zzbe(Context context) {
        float f;
        synchronized (zzk.class) {
            if (SystemClock.elapsedRealtime() - zzaIf >= ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS || Float.isNaN(zzaIg)) {
                Intent registerReceiver = context.getApplicationContext().registerReceiver(null, zzaIe);
                if (registerReceiver != null) {
                    zzaIg = ((float) registerReceiver.getIntExtra(Param.LEVEL, -1)) / ((float) registerReceiver.getIntExtra("scale", -1));
                }
                zzaIf = SystemClock.elapsedRealtime();
                f = zzaIg;
            } else {
                f = zzaIg;
            }
        }
        return f;
    }
}
