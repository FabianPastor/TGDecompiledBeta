package j$.util;

import java.security.AccessController;

final class W {
    static final boolean a = ((Boolean) AccessController.doPrivileged(CLASSNAMEi.a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
