package j$.util.stream;

import java.security.PrivilegedAction;

public final /* synthetic */ class J0 implements PrivilegedAction {
    public static final /* synthetic */ J0 a = new J0();

    private /* synthetic */ J0() {
    }

    public final Object run() {
        boolean z = i3.a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
