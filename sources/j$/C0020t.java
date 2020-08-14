package j$;

import j$.util.function.r;
import java.util.function.DoubleBinaryOperator;

/* renamed from: j$.t  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEt implements r {
    final /* synthetic */ DoubleBinaryOperator a;

    private /* synthetic */ CLASSNAMEt(DoubleBinaryOperator doubleBinaryOperator) {
        this.a = doubleBinaryOperator;
    }

    public static /* synthetic */ r b(DoubleBinaryOperator doubleBinaryOperator) {
        if (doubleBinaryOperator == null) {
            return null;
        }
        return new CLASSNAMEt(doubleBinaryOperator);
    }

    public /* synthetic */ double a(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
