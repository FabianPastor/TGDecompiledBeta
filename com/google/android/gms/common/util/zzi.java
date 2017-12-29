package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;

public final class zzi {
    private static Boolean zzgei;
    private static Boolean zzgej;
    private static Boolean zzgek;

    @TargetApi(20)
    public static boolean zzcs(Context context) {
        if (zzgei == null) {
            boolean z = zzq.zzamm() && context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
            zzgei = Boolean.valueOf(z);
        }
        return zzgei.booleanValue();
    }

    @TargetApi(24)
    public static boolean zzct(Context context) {
        return (!zzq.isAtLeastN() || zzcu(context)) && zzcs(context);
    }

    @TargetApi(21)
    public static boolean zzcu(Context context) {
        if (zzgej == null) {
            boolean z = zzq.zzamn() && context.getPackageManager().hasSystemFeature("cn.google");
            zzgej = Boolean.valueOf(z);
        }
        return zzgej.booleanValue();
    }

    public static boolean zzcv(Context context) {
        if (zzgek == null) {
            boolean z = context.getPackageManager().hasSystemFeature("android.hardware.type.iot") || context.getPackageManager().hasSystemFeature("android.hardware.type.embedded");
            zzgek = Boolean.valueOf(z);
        }
        return zzgek.booleanValue();
    }
}
