package org.telegram.messenger;

import android.os.Build;

public class OneUIUtilities {
    private static Boolean isOneUI;

    public static boolean isOneUI() {
        Boolean bool = isOneUI;
        if (bool != null) {
            return bool.booleanValue();
        }
        try {
            Build.VERSION.class.getDeclaredField("SEM_PLATFORM_INT");
            isOneUI = Boolean.TRUE;
        } catch (NoSuchFieldException unused) {
            isOneUI = Boolean.FALSE;
        }
        return isOneUI.booleanValue();
    }
}
