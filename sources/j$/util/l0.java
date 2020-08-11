package j$.util;

import java.security.AccessController;

final class l0 {
    static final boolean a = ((Boolean) AccessController.doPrivileged(CLASSNAMEj.a)).booleanValue();

    static void b(Class trippingClass, String msg) {
        throw new UnsupportedOperationException(trippingClass + " tripwire tripped but logging not supported: " + msg);
    }
}
