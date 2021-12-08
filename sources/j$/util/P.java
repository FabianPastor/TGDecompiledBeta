package j$.util;

import java.security.AccessController;

abstract class P {
    static final boolean a = ((Boolean) AccessController.doPrivileged(O.a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
