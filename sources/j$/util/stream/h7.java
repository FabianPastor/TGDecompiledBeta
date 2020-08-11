package j$.util.stream;

import java.security.AccessController;

final class h7 {
    static final boolean a = ((Boolean) AccessController.doPrivileged(F0.a)).booleanValue();

    static void b(Class trippingClass, String msg) {
        throw new UnsupportedOperationException(trippingClass + " tripwire tripped but logging not supported: " + msg);
    }
}
