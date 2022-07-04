package j$.util.stream;

import java.security.AccessController;

abstract class Q4 {
    static final boolean a = ((Boolean) AccessController.doPrivileged(P4.a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
