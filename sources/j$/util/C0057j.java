package j$.util;

import java.security.PrivilegedAction;

/* renamed from: j$.util.j  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj implements PrivilegedAction {
    public static final /* synthetic */ CLASSNAMEj a = new CLASSNAMEj();

    private /* synthetic */ CLASSNAMEj() {
    }

    public final Object run() {
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
