package org.telegram.messenger.voip;

import android.text.TextUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
/* loaded from: classes.dex */
class VLog {
    public static native void d(String str);

    public static native void e(String str);

    public static native void i(String str);

    public static native void v(String str);

    public static native void w(String str);

    VLog() {
    }

    public static void e(Throwable th) {
        e(null, th);
    }

    public static void e(String str, Throwable th) {
        StringWriter stringWriter = new StringWriter();
        if (!TextUtils.isEmpty(str)) {
            stringWriter.append((CharSequence) str);
            stringWriter.append((CharSequence) ": ");
        }
        th.printStackTrace(new PrintWriter(stringWriter));
        String[] split = stringWriter.toString().split("\n");
        for (String str2 : split) {
            e(str2);
        }
    }
}
