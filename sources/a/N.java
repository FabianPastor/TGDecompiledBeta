package a;

import j$.util.function.u;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class N implements u {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DoubleUnaryOperator var_a;

    private /* synthetic */ N(DoubleUnaryOperator doubleUnaryOperator) {
        this.var_a = doubleUnaryOperator;
    }

    public static /* synthetic */ u b(DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof O ? ((O) doubleUnaryOperator).var_a : new N(doubleUnaryOperator);
    }

    public /* synthetic */ double a(double d) {
        return this.var_a.applyAsDouble(d);
    }
}
