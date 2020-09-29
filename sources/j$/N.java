package j$;

import j$.util.function.y;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class N implements DoubleUnaryOperator {
    final /* synthetic */ y a;

    private /* synthetic */ N(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ DoubleUnaryOperator a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof M ? ((M) yVar).a : new N(yVar);
    }

    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return a(((M) this.a).a(M.d(doubleUnaryOperator)));
    }

    public /* synthetic */ double applyAsDouble(double d) {
        return ((M) this.a).b(d);
    }

    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return a(((M) this.a).c(M.d(doubleUnaryOperator)));
    }
}
