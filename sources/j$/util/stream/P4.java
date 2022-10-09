package j$.util.stream;

import java.security.PrivilegedAction;
/* loaded from: classes2.dex */
public final /* synthetic */ class P4 implements PrivilegedAction {
    public static final /* synthetic */ P4 a = new P4();

    private /* synthetic */ P4() {
    }

    @Override // java.security.PrivilegedAction
    public final Object run() {
        boolean z = Q4.a;
        return Boolean.valueOf(Boolean.getBoolean("org.openjdk.java.util.stream.tripwire"));
    }
}
