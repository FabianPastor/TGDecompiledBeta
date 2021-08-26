package j$.wrappers;

import j$.util.function.u;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class u0 implements ObjIntConsumer {
    final /* synthetic */ u a;

    private /* synthetic */ u0(u uVar) {
        this.a = uVar;
    }

    public static /* synthetic */ ObjIntConsumer a(u uVar) {
        if (uVar == null) {
            return null;
        }
        return uVar instanceof t0 ? ((t0) uVar).a : new u0(uVar);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.a.accept(obj, i);
    }
}
