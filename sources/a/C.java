package a;

import j$.util.function.p;
import java.util.function.DoubleBinaryOperator;

public final /* synthetic */ class C implements DoubleBinaryOperator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ p f9a;

    private /* synthetic */ C(p pVar) {
        this.f9a = pVar;
    }

    public static /* synthetic */ DoubleBinaryOperator a(p pVar) {
        if (pVar == null) {
            return null;
        }
        return pVar instanceof B ? ((B) pVar).f7a : new C(pVar);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.f9a.applyAsDouble(d, d2);
    }
}
