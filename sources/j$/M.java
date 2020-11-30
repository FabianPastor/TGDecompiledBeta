package j$;

import j$.util.function.t;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class M implements DoubleToLongFunction {
    final /* synthetic */ t a;

    private /* synthetic */ M(t tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ DoubleToLongFunction a(t tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof L ? ((L) tVar).a : new M(tVar);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
