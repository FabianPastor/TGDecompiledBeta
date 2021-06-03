package j$.util.stream;

import java.security.PrivilegedAction;

public final /* synthetic */ class I0 implements PrivilegedAction {
    public static final /* synthetic */ I0 a = new I0();

    private /* synthetic */ I0() {
    }

    public final Object run() {
        boolean z = i3.a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
