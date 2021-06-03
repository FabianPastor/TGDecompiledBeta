package j$;

import j$.util.function.p;
import java.util.function.DoubleBinaryOperator;

/* renamed from: j$.y  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy implements p {
    final /* synthetic */ DoubleBinaryOperator a;

    private /* synthetic */ CLASSNAMEy(DoubleBinaryOperator doubleBinaryOperator) {
        this.a = doubleBinaryOperator;
    }

    public static /* synthetic */ p a(DoubleBinaryOperator doubleBinaryOperator) {
        if (doubleBinaryOperator == null) {
            return null;
        }
        return doubleBinaryOperator instanceof CLASSNAMEz ? ((CLASSNAMEz) doubleBinaryOperator).a : new CLASSNAMEy(doubleBinaryOperator);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
