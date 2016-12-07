package com.google.android.gms.common.internal;

import android.util.Log;

public final class zzq {
    public static final int CO = (23 - " PII_LOG".length());
    private static final String CP = null;
    private final String CQ;
    private final String CR;

    public zzq(String str) {
        this(str, null);
    }

    public zzq(String str, String str2) {
        zzac.zzb((Object) str, (Object) "log tag cannot be null");
        zzac.zzb(str.length() <= 23, "tag \"%s\" is longer than the %d character maximum", str, Integer.valueOf(23));
        this.CQ = str;
        if (str2 == null || str2.length() <= 0) {
            this.CR = null;
        } else {
            this.CR = str2;
        }
    }

    private String zzhx(String str) {
        return this.CR == null ? str : this.CR.concat(str);
    }

    public void zzae(String str, String str2) {
        if (zzgp(3)) {
            Log.d(str, zzhx(str2));
        }
    }

    public void zzaf(String str, String str2) {
        if (zzgp(5)) {
            Log.w(str, zzhx(str2));
        }
    }

    public void zzag(String str, String str2) {
        if (zzgp(6)) {
            Log.e(str, zzhx(str2));
        }
    }

    public void zzb(String str, String str2, Throwable th) {
        if (zzgp(4)) {
            Log.i(str, zzhx(str2), th);
        }
    }

    public void zzc(String str, String str2, Throwable th) {
        if (zzgp(5)) {
            Log.w(str, zzhx(str2), th);
        }
    }

    public void zzd(String str, String str2, Throwable th) {
        if (zzgp(6)) {
            Log.e(str, zzhx(str2), th);
        }
    }

    public void zze(String str, String str2, Throwable th) {
        if (zzgp(7)) {
            Log.e(str, zzhx(str2), th);
            Log.wtf(str, zzhx(str2), th);
        }
    }

    public boolean zzgp(int i) {
        return Log.isLoggable(this.CQ, i);
    }
}
