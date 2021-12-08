package j$.util.stream;

import java.security.PrivilegedAction;

public final /* synthetic */ class Q4 implements PrivilegedAction {
    public static final /* synthetic */ Q4 a = new Q4();

    private /* synthetic */ Q4() {
    }

    public final Object run() {
        boolean z = R4.a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
