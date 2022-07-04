package j$.util.stream;

import java.security.PrivilegedAction;

public final /* synthetic */ class P4 implements PrivilegedAction {
    public static final /* synthetic */ P4 a = new P4();

    private /* synthetic */ P4() {
    }

    public final Object run() {
        boolean z = Q4.a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
