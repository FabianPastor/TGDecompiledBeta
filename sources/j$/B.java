package j$;

import j$.util.function.y;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class B implements y {
    final /* synthetic */ DoubleUnaryOperator a;

    private /* synthetic */ B(DoubleUnaryOperator doubleUnaryOperator) {
        this.a = doubleUnaryOperator;
    }

    public static /* synthetic */ y d(DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof C ? ((C) doubleUnaryOperator).a : new B(doubleUnaryOperator);
    }

    public /* synthetic */ y a(y yVar) {
        return d(this.a.andThen(C.a(yVar)));
    }

    public /* synthetic */ double b(double d) {
        return this.a.applyAsDouble(d);
    }

    public /* synthetic */ y c(y yVar) {
        return d(this.a.compose(C.a(yVar)));
    }
}
