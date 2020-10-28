package j$;

import java.util.function.DoubleUnaryOperator;

public final /* synthetic */ class O implements DoubleUnaryOperator {
    final /* synthetic */ N a;

    public static /* synthetic */ DoubleUnaryOperator a(N n) {
        if (n == null) {
            return null;
        }
        return n.a;
    }

    public DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return a(N.b(this.a.a.andThen(a(N.b(doubleUnaryOperator)))));
    }

    public double applyAsDouble(double d) {
        return this.a.a.applyAsDouble(d);
    }

    public DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return a(N.b(this.a.a.compose(a(N.b(doubleUnaryOperator)))));
    }
}
