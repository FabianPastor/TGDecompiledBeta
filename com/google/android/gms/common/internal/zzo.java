package com.google.android.gms.common.internal;

import android.util.Log;

public final class zzo {
    public static final int EA = (23 - " PII_LOG".length());
    private static final String EB = null;
    private final String EC;
    private final String ED;

    public zzo(String str) {
        this(str, null);
    }

    public zzo(String str, String str2) {
        zzaa.zzb((Object) str, (Object) "log tag cannot be null");
        zzaa.zzb(str.length() <= 23, "tag \"%s\" is longer than the %d character maximum", str, Integer.valueOf(23));
        this.EC = str;
        if (str2 == null || str2.length() <= 0) {
            this.ED = null;
        } else {
            this.ED = str2;
        }
    }

    private String zzhz(String str) {
        return this.ED == null ? str : this.ED.concat(str);
    }

    public void zzad(String str, String str2) {
        if (zzgo(3)) {
            Log.d(str, zzhz(str2));
        }
    }

    public void zzae(String str, String str2) {
        if (zzgo(5)) {
            Log.w(str, zzhz(str2));
        }
    }

    public void zzaf(String str, String str2) {
        if (zzgo(6)) {
            Log.e(str, zzhz(str2));
        }
    }

    public void zzb(String str, String str2, Throwable th) {
        if (zzgo(4)) {
            Log.i(str, zzhz(str2), th);
        }
    }

    public void zzc(String str, String str2, Throwable th) {
        if (zzgo(5)) {
            Log.w(str, zzhz(str2), th);
        }
    }

    public void zzd(String str, String str2, Throwable th) {
        if (zzgo(6)) {
            Log.e(str, zzhz(str2), th);
        }
    }

    public void zze(String str, String str2, Throwable th) {
        if (zzgo(7)) {
            Log.e(str, zzhz(str2), th);
            Log.wtf(str, zzhz(str2), th);
        }
    }

    public boolean zzgo(int i) {
        return Log.isLoggable(this.EC, i);
    }
}
