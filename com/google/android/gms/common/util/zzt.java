package com.google.android.gms.common.util;

import android.os.Build.VERSION;

public final class zzt {
    public static boolean isAtLeastN() {
        return VERSION.SDK_INT >= 24;
    }

    public static boolean zzze() {
        int i = VERSION.SDK_INT;
        return true;
    }

    public static boolean zzzf() {
        int i = VERSION.SDK_INT;
        return true;
    }

    public static boolean zzzg() {
        int i = VERSION.SDK_INT;
        return true;
    }

    public static boolean zzzh() {
        return VERSION.SDK_INT >= 15;
    }

    public static boolean zzzi() {
        return VERSION.SDK_INT >= 16;
    }

    public static boolean zzzj() {
        return VERSION.SDK_INT >= 17;
    }

    public static boolean zzzk() {
        return VERSION.SDK_INT >= 18;
    }

    public static boolean zzzl() {
        return VERSION.SDK_INT >= 19;
    }

    public static boolean zzzm() {
        return VERSION.SDK_INT >= 20;
    }

    @Deprecated
    public static boolean zzzn() {
        return zzzo();
    }

    public static boolean zzzo() {
        return VERSION.SDK_INT >= 21;
    }

    public static boolean zzzp() {
        return VERSION.SDK_INT >= 23;
    }

    public static boolean zzzq() {
        return VERSION.SDK_INT > 25 || "O".equals(VERSION.CODENAME) || VERSION.CODENAME.startsWith("OMR");
    }
}
