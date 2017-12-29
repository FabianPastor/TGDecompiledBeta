package android.support.v4.os;

import android.os.Build.VERSION;

public class BuildCompat {
    @Deprecated
    public static boolean isAtLeastNMR1() {
        return VERSION.SDK_INT >= 25;
    }
}
