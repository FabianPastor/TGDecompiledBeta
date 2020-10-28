package j$;

import java.util.function.IntUnaryOperator;

/* renamed from: j$.f0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf0 implements IntUnaryOperator {
    final /* synthetic */ CLASSNAMEe0 a;

    public static /* synthetic */ IntUnaryOperator a(CLASSNAMEe0 e0Var) {
        if (e0Var == null) {
            return null;
        }
        return e0Var.a;
    }

    public IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return a(CLASSNAMEe0.b(this.a.a.andThen(a(CLASSNAMEe0.b(intUnaryOperator)))));
    }

    public int applyAsInt(int i) {
        return this.a.a.applyAsInt(i);
    }

    public IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return a(CLASSNAMEe0.b(this.a.a.compose(a(CLASSNAMEe0.b(intUnaryOperator)))));
    }
}
