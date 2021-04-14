package j$.util;

import java.security.PrivilegedAction;

public final /* synthetic */ class i implements PrivilegedAction {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ i var_a = new i();

    private /* synthetic */ i() {
    }

    public final Object run() {
        boolean z = w.var_a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
