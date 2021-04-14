package j$.util.stream;

import java.security.PrivilegedAction;

public final /* synthetic */ class I0 implements PrivilegedAction {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ I0 var_a = new I0();

    private /* synthetic */ I0() {
    }

    public final Object run() {
        boolean z = i3.var_a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
