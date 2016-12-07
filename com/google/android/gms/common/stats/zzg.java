package com.google.android.gms.common.stats;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.stats.zzb.zzb;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.common.util.zzj;
import java.util.List;

public class zzg {
    private static zzg Gn = new zzg();
    private static Boolean Go;
    private static String TAG = "WakeLockTracker";

    public static zzg zzayg() {
        return Gn;
    }

    private static boolean zzcg(Context context) {
        if (Go == null) {
            Go = Boolean.valueOf(zzch(context));
        }
        return Go.booleanValue();
    }

    private static boolean zzch(Context context) {
        try {
            if (!zzd.zzayi()) {
                return false;
            }
            return ((Integer) zzb.FJ.get()).intValue() != zzc.LOG_LEVEL_OFF;
        } catch (SecurityException e) {
            return false;
        }
    }

    public void zza(Context context, String str, int i, String str2, String str3, String str4, int i2, List<String> list) {
        zza(context, str, i, str2, str3, str4, i2, list, 0);
    }

    public void zza(Context context, String str, int i, String str2, String str3, String str4, int i2, List<String> list, long j) {
        if (!zzcg(context)) {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            String str5 = TAG;
            String str6 = "missing wakeLock key. ";
            String valueOf = String.valueOf(str);
            Log.e(str5, valueOf.length() != 0 ? str6.concat(valueOf) : new String(str6));
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (7 == i || 8 == i || 10 == i || 11 == i) {
            try {
                context.startService(new Intent().setComponent(zzc.FP).putExtra("com.google.android.gms.common.stats.EXTRA_LOG_EVENT", new WakeLockEvent(currentTimeMillis, i, str2, i2, zze.zzz(list), str, SystemClock.elapsedRealtime(), zzj.zzck(context), str3, zze.zzih(context.getPackageName()), zzj.zzcl(context), j, str4)));
            } catch (Throwable e) {
                Log.wtf(TAG, e);
            }
        }
    }
}
