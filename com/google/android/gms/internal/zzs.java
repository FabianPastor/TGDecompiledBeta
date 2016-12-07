package com.google.android.gms.internal;

import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class zzs {
    public static boolean DEBUG = Log.isLoggable(TAG, 2);
    public static String TAG = "Volley";

    static class zza {
        public static final boolean zzai = zzs.DEBUG;
        private final List<zza> zzaj = new ArrayList();
        private boolean zzak = false;

        private static class zza {
            public final String name;
            public final long time;
            public final long zzal;

            public zza(String str, long j, long j2) {
                this.name = str;
                this.zzal = j;
                this.time = j2;
            }
        }

        zza() {
        }

        private long getTotalDuration() {
            if (this.zzaj.size() == 0) {
                return 0;
            }
            return ((zza) this.zzaj.get(this.zzaj.size() - 1)).time - ((zza) this.zzaj.get(0)).time;
        }

        protected void finalize() throws Throwable {
            if (!this.zzak) {
                zzd("Request on the loose");
                zzs.zzc("Marker log finalized without finish() - uncaught exit point for request", new Object[0]);
            }
        }

        public synchronized void zza(String str, long j) {
            if (this.zzak) {
                throw new IllegalStateException("Marker added to finished log");
            }
            this.zzaj.add(new zza(str, j, SystemClock.elapsedRealtime()));
        }

        public synchronized void zzd(String str) {
            this.zzak = true;
            if (getTotalDuration() > 0) {
                long j = ((zza) this.zzaj.get(0)).time;
                zzs.zzb("(%-4d ms) %s", Long.valueOf(r2), str);
                long j2 = j;
                for (zza com_google_android_gms_internal_zzs_zza_zza : this.zzaj) {
                    zzs.zzb("(+%-4d) [%2d] %s", Long.valueOf(com_google_android_gms_internal_zzs_zza_zza.time - j2), Long.valueOf(com_google_android_gms_internal_zzs_zza_zza.zzal), com_google_android_gms_internal_zzs_zza_zza.name);
                    j2 = com_google_android_gms_internal_zzs_zza_zza.time;
                }
            }
        }
    }

    public static void zza(String str, Object... objArr) {
        if (DEBUG) {
            Log.v(TAG, zzd(str, objArr));
        }
    }

    public static void zza(Throwable th, String str, Object... objArr) {
        Log.e(TAG, zzd(str, objArr), th);
    }

    public static void zzb(String str, Object... objArr) {
        Log.d(TAG, zzd(str, objArr));
    }

    public static void zzc(String str, Object... objArr) {
        Log.e(TAG, zzd(str, objArr));
    }

    private static String zzd(String str, Object... objArr) {
        String valueOf;
        if (objArr != null) {
            str = String.format(Locale.US, str, objArr);
        }
        StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
        String str2 = "<unknown>";
        for (int i = 2; i < stackTrace.length; i++) {
            if (!stackTrace[i].getClass().equals(zzs.class)) {
                str2 = stackTrace[i].getClassName();
                str2 = str2.substring(str2.lastIndexOf(46) + 1);
                str2 = str2.substring(str2.lastIndexOf(36) + 1);
                valueOf = String.valueOf(stackTrace[i].getMethodName());
                valueOf = new StringBuilder((String.valueOf(str2).length() + 1) + String.valueOf(valueOf).length()).append(str2).append(".").append(valueOf).toString();
                break;
            }
        }
        valueOf = str2;
        return String.format(Locale.US, "[%d] %s: %s", new Object[]{Long.valueOf(Thread.currentThread().getId()), valueOf, str});
    }
}
