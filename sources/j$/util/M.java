package j$.util;

import java.security.PrivilegedAction;
/* loaded from: classes2.dex */
public final /* synthetic */ class M implements PrivilegedAction {
    public static final /* synthetic */ M a = new M();

    private /* synthetic */ M() {
    }

    @Override // java.security.PrivilegedAction
    public final Object run() {
        boolean z = N.a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
