package j$.util;

import java.security.AccessController;

final class v {
    static final boolean a = ((Boolean) AccessController.doPrivileged(i.a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
