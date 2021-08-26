package j$.util.stream;

import java.security.AccessController;

abstract class R4 {
    static final boolean a = ((Boolean) AccessController.doPrivileged(Q4.a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
