package j$.util;

import java.security.AccessController;

final class w {

    /* renamed from: a  reason: collision with root package name */
    static final boolean var_a = ((Boolean) AccessController.doPrivileged(i.var_a)).booleanValue();

    static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
