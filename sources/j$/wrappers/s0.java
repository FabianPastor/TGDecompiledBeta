package j$.wrappers;

import j$.util.function.u;
import java.util.function.ObjDoubleConsumer;

public final /* synthetic */ class s0 implements ObjDoubleConsumer {
    final /* synthetic */ u a;

    private /* synthetic */ s0(u uVar) {
        this.a = uVar;
    }

    public static /* synthetic */ ObjDoubleConsumer a(u uVar) {
        if (uVar == null) {
            return null;
        }
        return uVar instanceof CLASSNAMEr0 ? ((CLASSNAMEr0) uVar).a : new s0(uVar);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}
