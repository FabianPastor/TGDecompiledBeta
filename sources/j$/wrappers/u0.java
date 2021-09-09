package j$.wrappers;

import j$.util.function.v;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class u0 implements ObjIntConsumer {
    final /* synthetic */ v a;

    private /* synthetic */ u0(v vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ ObjIntConsumer a(v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof t0 ? ((t0) vVar).a : new u0(vVar);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.a.accept(obj, i);
    }
}
