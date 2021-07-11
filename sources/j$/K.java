package j$;

import j$.util.function.u;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class K implements u {
    final /* synthetic */ DoubleUnaryOperator a;

    private /* synthetic */ K(DoubleUnaryOperator doubleUnaryOperator) {
        this.a = doubleUnaryOperator;
    }

    public static /* synthetic */ u b(DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof L ? ((L) doubleUnaryOperator).a : new K(doubleUnaryOperator);
    }

    public /* synthetic */ double a(double d) {
        return this.a.applyAsDouble(d);
    }
}
