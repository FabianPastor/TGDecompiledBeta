package j$.util.stream;

import java.security.AccessController;

final class i3 {

    /* renamed from: a  reason: collision with root package name */
    static final boolean var_a = ((Boolean) AccessController.doPrivileged(I0.var_a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
