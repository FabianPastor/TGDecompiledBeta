package j$;

import j$.util.function.u;
import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class O implements DoubleUnaryOperator {
    final /* synthetic */ u a;

    private /* synthetic */ O(u uVar) {
        this.a = uVar;
    }

    public static /* synthetic */ DoubleUnaryOperator a(u uVar) {
        if (uVar == null) {
            return null;
        }
        return uVar instanceof N ? ((N) uVar).a : new O(uVar);
    }

    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return a(N.b(((N) this.a).a.andThen(a(N.b(doubleUnaryOperator)))));
    }

    public /* synthetic */ double applyAsDouble(double d) {
        return ((N) this.a).a.applyAsDouble(d);
    }

    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return a(N.b(((N) this.a).a.compose(a(N.b(doubleUnaryOperator)))));
    }
}
