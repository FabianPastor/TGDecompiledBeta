package j$;

import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class N {
    final /* synthetic */ DoubleUnaryOperator a;

    private /* synthetic */ N(DoubleUnaryOperator doubleUnaryOperator) {
        this.a = doubleUnaryOperator;
    }

    public static /* synthetic */ N b(DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof O ? ((O) doubleUnaryOperator).a : new N(doubleUnaryOperator);
    }

    public double a(double d) {
        return this.a.applyAsDouble(d);
    }
}
