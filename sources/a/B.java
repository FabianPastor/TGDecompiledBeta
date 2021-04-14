package a;

import j$.util.function.p;
import java.util.function.DoubleBinaryOperator;

public final /* synthetic */ class B implements p {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DoubleBinaryOperator f7a;

    private /* synthetic */ B(DoubleBinaryOperator doubleBinaryOperator) {
        this.f7a = doubleBinaryOperator;
    }

    public static /* synthetic */ p a(DoubleBinaryOperator doubleBinaryOperator) {
        if (doubleBinaryOperator == null) {
            return null;
        }
        return doubleBinaryOperator instanceof C ? ((C) doubleBinaryOperator).f9a : new B(doubleBinaryOperator);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.f7a.applyAsDouble(d, d2);
    }
}
