package j$.util.stream;

import java.security.PrivilegedAction;

public final /* synthetic */ class F0 implements PrivilegedAction {
    public static final /* synthetic */ F0 a = new F0();

    private /* synthetic */ F0() {
    }

    public final Object run() {
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
