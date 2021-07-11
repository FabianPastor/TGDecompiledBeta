package j$.util.stream;

import java.security.AccessController;

final class i3 {
    static final boolean a = ((Boolean) AccessController.doPrivileged(I0.a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
