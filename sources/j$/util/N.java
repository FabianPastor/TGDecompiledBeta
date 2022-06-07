package j$.util;

import java.security.AccessController;

abstract class N {
    static final boolean a = ((Boolean) AccessController.doPrivileged(M.a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
