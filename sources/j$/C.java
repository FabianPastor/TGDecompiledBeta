package j$;

import j$.util.function.p;
import java.util.function.DoubleBinaryOperator;

public final /* synthetic */ class C implements DoubleBinaryOperator {
    final /* synthetic */ p a;

    private /* synthetic */ C(p pVar) {
        this.a = pVar;
    }

    public static /* synthetic */ DoubleBinaryOperator a(p pVar) {
        if (pVar == null) {
            return null;
        }
        return pVar instanceof B ? ((B) pVar).a : new C(pVar);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
