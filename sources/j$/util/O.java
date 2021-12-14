package j$.util;

import java.security.PrivilegedAction;

public final /* synthetic */ class O implements PrivilegedAction {
    public static final /* synthetic */ O a = new O();

    private /* synthetic */ O() {
    }

    public final Object run() {
        boolean z = P.a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
