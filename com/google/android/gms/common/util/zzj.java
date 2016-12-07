package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;

public final class zzj {
    private static IntentFilter EP = new IntentFilter("android.intent.action.BATTERY_CHANGED");
    private static long EQ;
    private static float ER = Float.NaN;

    @TargetApi(20)
    public static boolean zzb(PowerManager powerManager) {
        return zzs.zzaxs() ? powerManager.isInteractive() : powerManager.isScreenOn();
    }

    @TargetApi(20)
    public static int zzcn(Context context) {
        int i = 1;
        if (context == null || context.getApplicationContext() == null) {
            return -1;
        }
        Intent registerReceiver = context.getApplicationContext().registerReceiver(null, EP);
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

    public static synchronized float zzco(Context context) {
        float f;
        synchronized (zzj.class) {
            if (SystemClock.elapsedRealtime() - EQ >= HlsChunkSource.DEFAULT_PLAYLIST_BLACKLIST_MS || Float.isNaN(ER)) {
                Intent registerReceiver = context.getApplicationContext().registerReceiver(null, EP);
                if (registerReceiver != null) {
                    ER = ((float) registerReceiver.getIntExtra(Param.LEVEL, -1)) / ((float) registerReceiver.getIntExtra("scale", -1));
                }
                EQ = SystemClock.elapsedRealtime();
                f = ER;
            } else {
                f = ER;
            }
        }
        return f;
    }
}
