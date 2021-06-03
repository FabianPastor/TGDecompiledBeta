package j$.util;

import java.security.PrivilegedAction;

public final /* synthetic */ class i implements PrivilegedAction {
    public static final /* synthetic */ i a = new i();

    private /* synthetic */ i() {
    }

    public final Object run() {
        boolean z = v.a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
