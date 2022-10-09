package j$.util.concurrent;

import java.security.PrivilegedAction;
/* loaded from: classes2.dex */
class d implements PrivilegedAction {
    @Override // java.security.PrivilegedAction
    public Object run() {
        return Boolean.valueOf(Boolean.getBoolean("java.util.secureRandomSeed"));
    }
}
