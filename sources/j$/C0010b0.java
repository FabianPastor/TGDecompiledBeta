package j$;

import j$.util.function.A;
import java.util.function.IntUnaryOperator;

/* renamed from: j$.b0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb0 implements A {
    final /* synthetic */ IntUnaryOperator a;

    private /* synthetic */ CLASSNAMEb0(IntUnaryOperator intUnaryOperator) {
        this.a = intUnaryOperator;
    }

    public static /* synthetic */ A b(IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof CLASSNAMEc0 ? ((CLASSNAMEc0) intUnaryOperator).a : new CLASSNAMEb0(intUnaryOperator);
    }

    public /* synthetic */ int a(int i) {
        return this.a.applyAsInt(i);
    }
}
