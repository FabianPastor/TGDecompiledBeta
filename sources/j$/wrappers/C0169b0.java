package j$.wrappers;

import java.util.function.IntUnaryOperator;

/* renamed from: j$.wrappers.b0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb0 {
    final /* synthetic */ IntUnaryOperator a;

    private /* synthetic */ CLASSNAMEb0(IntUnaryOperator intUnaryOperator) {
        this.a = intUnaryOperator;
    }

    public static /* synthetic */ CLASSNAMEb0 b(IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof CLASSNAMEc0 ? ((CLASSNAMEc0) intUnaryOperator).a : new CLASSNAMEb0(intUnaryOperator);
    }

    public int a(int i) {
        return this.a.applyAsInt(i);
    }
}
