package j$;

import j$.util.function.r;
import java.util.function.DoubleBinaryOperator;

public final /* synthetic */ class E implements r {
    final /* synthetic */ DoubleBinaryOperator a;

    private /* synthetic */ E(DoubleBinaryOperator doubleBinaryOperator) {
        this.a = doubleBinaryOperator;
    }

    public static /* synthetic */ r b(DoubleBinaryOperator doubleBinaryOperator) {
        if (doubleBinaryOperator == null) {
            return null;
        }
        return new E(doubleBinaryOperator);
    }

    public /* synthetic */ double a(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
