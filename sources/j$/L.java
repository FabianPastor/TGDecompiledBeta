package j$;

import j$.util.function.u;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class L implements DoubleUnaryOperator {
    final /* synthetic */ u a;

    private /* synthetic */ L(u uVar) {
        this.a = uVar;
    }

    public static /* synthetic */ DoubleUnaryOperator a(u uVar) {
        if (uVar == null) {
            return null;
        }
        return uVar instanceof K ? ((K) uVar).a : new L(uVar);
    }

    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return a(K.b(((K) this.a).a.andThen(a(K.b(doubleUnaryOperator)))));
    }

    public /* synthetic */ double applyAsDouble(double d) {
        return ((K) this.a).a.applyAsDouble(d);
    }

    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return a(K.b(((K) this.a).a.compose(a(K.b(doubleUnaryOperator)))));
    }
}
