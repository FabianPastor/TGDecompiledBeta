package j$;

import j$.util.function.B;
import java.util.function.ObjDoubleConsumer;

/* renamed from: j$.v0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv0 implements ObjDoubleConsumer {
    final /* synthetic */ B a;

    private /* synthetic */ CLASSNAMEv0(B b) {
        this.a = b;
    }

    public static /* synthetic */ ObjDoubleConsumer a(B b) {
        if (b == null) {
            return null;
        }
        return b instanceof CLASSNAMEu0 ? ((CLASSNAMEu0) b).a : new CLASSNAMEv0(b);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}
