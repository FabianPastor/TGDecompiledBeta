package j$;

import j$.util.function.r;
import java.util.function.DoubleFunction;

public final /* synthetic */ class D implements DoubleFunction {
    final /* synthetic */ r a;

    private /* synthetic */ D(r rVar) {
        this.a = rVar;
    }

    public static /* synthetic */ DoubleFunction a(r rVar) {
        if (rVar == null) {
            return null;
        }
        return rVar instanceof C ? ((C) rVar).a : new D(rVar);
    }

    public /* synthetic */ Object apply(double d) {
        return this.a.apply(d);
    }
}
