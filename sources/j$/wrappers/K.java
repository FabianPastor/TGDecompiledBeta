package j$.wrappers;

import java.util.function.DoubleUnaryOperator;
/* loaded from: classes2.dex */
public final /* synthetic */ class K {
    final /* synthetic */ DoubleUnaryOperator a;

    private /* synthetic */ K(DoubleUnaryOperator doubleUnaryOperator) {
        this.a = doubleUnaryOperator;
    }

    public static /* synthetic */ K b(DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof L ? ((L) doubleUnaryOperator).a : new K(doubleUnaryOperator);
    }

    public double a(double d) {
        return this.a.applyAsDouble(d);
    }
}
