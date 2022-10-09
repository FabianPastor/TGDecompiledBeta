package j$.util;

import java.security.AccessController;
/* loaded from: classes2.dex */
abstract class N {
    static final boolean a = ((Boolean) AccessController.doPrivileged(M.a)).booleanValue();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
