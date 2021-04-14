package a;

import j$.util.function.t;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class M implements DoubleToLongFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t var_a;

    private /* synthetic */ M(t tVar) {
        this.var_a = tVar;
    }

    public static /* synthetic */ DoubleToLongFunction a(t tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof L ? ((L) tVar).var_a : new M(tVar);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.var_a.applyAsLong(d);
    }
}
