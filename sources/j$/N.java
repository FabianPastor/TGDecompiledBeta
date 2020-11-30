package j$;

import j$.util.function.u;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class N implements u {
    final /* synthetic */ DoubleUnaryOperator a;

    private /* synthetic */ N(DoubleUnaryOperator doubleUnaryOperator) {
        this.a = doubleUnaryOperator;
    }

    public static /* synthetic */ u b(DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof O ? ((O) doubleUnaryOperator).a : new N(doubleUnaryOperator);
    }

    public /* synthetic */ double a(double d) {
        return this.a.applyAsDouble(d);
    }
}
