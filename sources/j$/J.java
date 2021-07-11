package j$;

import j$.util.function.t;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class J implements DoubleToLongFunction {
    final /* synthetic */ t a;

    private /* synthetic */ J(t tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ DoubleToLongFunction a(t tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof I ? ((I) tVar).a : new J(tVar);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
