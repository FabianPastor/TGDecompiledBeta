package j$;

import j$.util.function.p;
import java.util.function.DoubleBinaryOperator;

public final /* synthetic */ class B implements p {
    final /* synthetic */ DoubleBinaryOperator a;

    private /* synthetic */ B(DoubleBinaryOperator doubleBinaryOperator) {
        this.a = doubleBinaryOperator;
    }

    public static /* synthetic */ p a(DoubleBinaryOperator doubleBinaryOperator) {
        if (doubleBinaryOperator == null) {
            return null;
        }
        return doubleBinaryOperator instanceof C ? ((C) doubleBinaryOperator).a : new B(doubleBinaryOperator);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
