package j$.wrappers;

import j$.util.function.t;
import java.util.function.ObjDoubleConsumer;

public final /* synthetic */ class s0 implements ObjDoubleConsumer {
    final /* synthetic */ t a;

    private /* synthetic */ s0(t tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ ObjDoubleConsumer a(t tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof CLASSNAMEr0 ? ((CLASSNAMEr0) tVar).a : new s0(tVar);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}
