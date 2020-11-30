package j$;

import j$.util.function.A;
import java.util.function.IntUnaryOperator;

/* renamed from: j$.e0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe0 implements A {
    final /* synthetic */ IntUnaryOperator a;

    private /* synthetic */ CLASSNAMEe0(IntUnaryOperator intUnaryOperator) {
        this.a = intUnaryOperator;
    }

    public static /* synthetic */ A b(IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof CLASSNAMEf0 ? ((CLASSNAMEf0) intUnaryOperator).a : new CLASSNAMEe0(intUnaryOperator);
    }

    public /* synthetic */ int a(int i) {
        return this.a.applyAsInt(i);
    }
}
