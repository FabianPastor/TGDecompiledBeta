package com.google.android.gms.common.stats;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.zzj;
import java.util.List;

public class zzg {
    private static String TAG = "WakeLockTracker";
    private static zzg zzaGH = new zzg();
    private static Boolean zzaGI;

    private static boolean zzaG(Context context) {
        if (zzaGI == null) {
            zzaGI = Boolean.valueOf(zzaH(context));
        }
        return zzaGI.booleanValue();
    }

    private static boolean zzaH(Context context) {
        return false;
    }

    public static zzg zzyr() {
        return zzaGH;
    }

    public void zza(Context context, String str, int i, String str2, String str3, String str4, int i2, List<String> list) {
        zza(context, str, i, str2, str3, str4, i2, list, 0);
    }

    public void zza(Context context, String str, int i, String str2, String str3, String str4, int i2, List<String> list, long j) {
        if (!zzaG(context)) {
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
                context.startService(new Intent().setComponent(zzc.zzaGj).putExtra("com.google.android.gms.common.stats.EXTRA_LOG_EVENT", new WakeLockEvent(currentTimeMillis, i, str2, i2, zze.zzz(list), str, SystemClock.elapsedRealtime(), zzj.zzaM(context), str3, zze.zzdB(context.getPackageName()), zzj.zzaN(context), j, str4)));
            } catch (Throwable e) {
                Log.wtf(TAG, e);
            }
        }
    }
}
