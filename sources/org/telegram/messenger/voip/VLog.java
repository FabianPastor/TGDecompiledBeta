package org.telegram.messenger.voip;

import android.text.TextUtils;
import java.io.PrintWriter;
import java.io.StringWriter;

class VLog {
    public static native void d(String str);

    public static native void e(String str);

    public static native void i(String str);

    public static native void v(String str);

    public static native void w(String str);

    VLog() {
    }

    public static void e(Throwable x) {
        e((String) null, x);
    }

    public static void e(String msg, Throwable x) {
        StringWriter sw = new StringWriter();
        if (!TextUtils.isEmpty(msg)) {
            sw.append(msg);
            sw.append(": ");
        }
        x.printStackTrace(new PrintWriter(sw));
        for (String line : sw.toString().split("\n")) {
            e(line);
        }
    }
}
