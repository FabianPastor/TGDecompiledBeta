package j$;

import j$.util.function.y;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class M implements y {
    final /* synthetic */ DoubleUnaryOperator a;

    private /* synthetic */ M(DoubleUnaryOperator doubleUnaryOperator) {
        this.a = doubleUnaryOperator;
    }

    public static /* synthetic */ y d(DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof N ? ((N) doubleUnaryOperator).a : new M(doubleUnaryOperator);
    }

    public /* synthetic */ y a(y yVar) {
        return d(this.a.andThen(N.a(yVar)));
    }

    public /* synthetic */ double b(double d) {
        return this.a.applyAsDouble(d);
    }

    public /* synthetic */ y c(y yVar) {
        return d(this.a.compose(N.a(yVar)));
    }
}
