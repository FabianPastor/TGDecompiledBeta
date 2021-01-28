package j$.util.concurrent;

import java.security.PrivilegedAction;

class d implements PrivilegedAction<Boolean> {
    d() {
    }

    public Object run() {
        return Boolean.valueOf(Boolean.getBoolean("java.util.secureRandomSeed"));
    }
}
