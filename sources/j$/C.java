package j$;

import j$.util.function.y;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class C implements DoubleUnaryOperator {
    final /* synthetic */ y a;

    private /* synthetic */ C(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ DoubleUnaryOperator a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof B ? ((B) yVar).a : new C(yVar);
    }

    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return a(((B) this.a).a(B.d(doubleUnaryOperator)));
    }

    public /* synthetic */ double applyAsDouble(double d) {
        return ((B) this.a).b(d);
    }

    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return a(((B) this.a).c(B.d(doubleUnaryOperator)));
    }
}
