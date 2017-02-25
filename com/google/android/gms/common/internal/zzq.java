package com.google.android.gms.common.internal;

import android.util.Log;

public final class zzq {
    public static final int zzaGp = (23 - " PII_LOG".length());
    private static final String zzaGq = null;
    private final String zzaGr;
    private final String zzaGs;

    public zzq(String str) {
        this(str, null);
    }

    public zzq(String str, String str2) {
        zzac.zzb((Object) str, (Object) "log tag cannot be null");
        zzac.zzb(str.length() <= 23, "tag \"%s\" is longer than the %d character maximum", str, Integer.valueOf(23));
        this.zzaGr = str;
        if (str2 == null || str2.length() <= 0) {
            this.zzaGs = null;
        } else {
            this.zzaGs = str2;
        }
    }

    private String zzdq(String str) {
        return this.zzaGs == null ? str : this.zzaGs.concat(str);
    }

    public void zzE(String str, String str2) {
        if (zzcW(3)) {
            Log.d(str, zzdq(str2));
        }
    }

    public void zzF(String str, String str2) {
        if (zzcW(5)) {
            Log.w(str, zzdq(str2));
        }
    }

    public void zzG(String str, String str2) {
        if (zzcW(6)) {
            Log.e(str, zzdq(str2));
        }
    }

    public void zzb(String str, String str2, Throwable th) {
        if (zzcW(4)) {
            Log.i(str, zzdq(str2), th);
        }
    }

    public void zzc(String str, String str2, Throwable th) {
        if (zzcW(5)) {
            Log.w(str, zzdq(str2), th);
        }
    }

    public boolean zzcW(int i) {
        return Log.isLoggable(this.zzaGr, i);
    }

    public void zzd(String str, String str2, Throwable th) {
        if (zzcW(6)) {
            Log.e(str, zzdq(str2), th);
        }
    }

    public void zze(String str, String str2, Throwable th) {
        if (zzcW(7)) {
            Log.e(str, zzdq(str2), th);
            Log.wtf(str, zzdq(str2), th);
        }
    }
}
