package j$.wrappers;

import j$.util.function.g;
import java.util.function.DoubleFunction;

public final /* synthetic */ class D implements DoubleFunction {
    final /* synthetic */ g a;

    private /* synthetic */ D(g gVar) {
        this.a = gVar;
    }

    public static /* synthetic */ DoubleFunction a(g gVar) {
        if (gVar == null) {
            return null;
        }
        return gVar instanceof C ? ((C) gVar).a : new D(gVar);
    }

    public /* synthetic */ Object apply(double d) {
        return this.a.apply(d);
    }
}
