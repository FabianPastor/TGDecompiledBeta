package com.google.android.gms.common.util;

import com.google.android.gms.common.internal.zzg;
import java.util.regex.Pattern;

public class zzw {
    private static final Pattern EZ = Pattern.compile("\\$\\{(.*?)\\}");

    public static boolean zzij(String str) {
        return str == null || zzg.BB.zzb(str);
    }
}
