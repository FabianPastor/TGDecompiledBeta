package org.telegram.messenger.exoplayer2.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RepeatModeUtil {
    public static final int REPEAT_TOGGLE_MODE_ALL = 2;
    public static final int REPEAT_TOGGLE_MODE_NONE = 0;
    public static final int REPEAT_TOGGLE_MODE_ONE = 1;

    @Retention(RetentionPolicy.SOURCE)
    public @interface RepeatToggleModes {
    }

    public static boolean isRepeatModeEnabled(int i, int i2) {
        boolean z = false;
        switch (i) {
            case 0:
                return true;
            case 1:
                if ((i2 & 1) != 0) {
                    z = true;
                }
                return z;
            case 2:
                if ((i2 & 2) != 0) {
                    z = true;
                }
                return z;
            default:
                return false;
        }
    }

    private RepeatModeUtil() {
    }

    public static int getNextRepeatMode(int i, int i2) {
        for (int i3 = 1; i3 <= 2; i3++) {
            int i4 = (i + i3) % 3;
            if (isRepeatModeEnabled(i4, i2)) {
                return i4;
            }
        }
        return i;
    }
}
