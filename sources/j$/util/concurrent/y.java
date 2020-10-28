package j$.util.concurrent;

import java.security.PrivilegedAction;

class y implements PrivilegedAction {
    y() {
    }

    public Object run() {
        return Boolean.valueOf(Boolean.getBoolean("java.util.secureRandomSeed"));
    }
}
