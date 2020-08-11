package j$.util.concurrent;

import java.security.PrivilegedAction;

class A implements PrivilegedAction {
    A() {
    }

    /* renamed from: a */
    public Boolean run() {
        return Boolean.valueOf(Boolean.getBoolean("java.util.secureRandomSeed"));
    }
}
