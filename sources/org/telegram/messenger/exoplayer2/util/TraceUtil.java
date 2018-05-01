package org.telegram.messenger.exoplayer2.util;

import android.annotation.TargetApi;
import android.os.Trace;

public final class TraceUtil {
    private TraceUtil() {
    }

    public static void beginSection(String str) {
        if (Util.SDK_INT >= 18) {
            beginSectionV18(str);
        }
    }

    public static void endSection() {
        if (Util.SDK_INT >= 18) {
            endSectionV18();
        }
    }

    @TargetApi(18)
    private static void beginSectionV18(String str) {
        Trace.beginSection(str);
    }

    @TargetApi(18)
    private static void endSectionV18() {
        Trace.endSection();
    }
}
