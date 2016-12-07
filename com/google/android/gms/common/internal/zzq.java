package com.google.android.gms.common.internal;

import android.util.Log;

public final class zzq {
    public static final int zzaES = (23 - " PII_LOG".length());
    private static final String zzaET = null;
    private final String zzaEU;
    private final String zzaEV;

    public zzq(String str) {
        this(str, null);
    }

    public zzq(String str, String str2) {
        zzac.zzb((Object) str, (Object) "log tag cannot be null");
        zzac.zzb(str.length() <= 23, "tag \"%s\" is longer than the %d character maximum", str, Integer.valueOf(23));
        this.zzaEU = str;
        if (str2 == null || str2.length() <= 0) {
            this.zzaEV = null;
        } else {
            this.zzaEV = str2;
        }
    }

    private String zzdu(String str) {
        return this.zzaEV == null ? str : this.zzaEV.concat(str);
    }

    public void zzD(String str, String str2) {
        if (zzcQ(3)) {
            Log.d(str, zzdu(str2));
        }
    }

    public void zzE(String str, String str2) {
        if (zzcQ(5)) {
            Log.w(str, zzdu(str2));
        }
    }

    public void zzF(String str, String str2) {
        if (zzcQ(6)) {
            Log.e(str, zzdu(str2));
        }
    }

    public void zzb(String str, String str2, Throwable th) {
        if (zzcQ(4)) {
            Log.i(str, zzdu(str2), th);
        }
    }

    public void zzc(String str, String str2, Throwable th) {
        if (zzcQ(5)) {
            Log.w(str, zzdu(str2), th);
        }
    }

    public boolean zzcQ(int i) {
        return Log.isLoggable(this.zzaEU, i);
    }

    public void zzd(String str, String str2, Throwable th) {
        if (zzcQ(6)) {
            Log.e(str, zzdu(str2), th);
        }
    }

    public void zze(String str, String str2, Throwable th) {
        if (zzcQ(7)) {
            Log.e(str, zzdu(str2), th);
            Log.wtf(str, zzdu(str2), th);
        }
    }
}
