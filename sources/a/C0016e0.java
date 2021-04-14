package a;

import j$.util.function.A;
import java.util.function.IntUnaryOperator;

/* renamed from: a.e0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe0 implements A {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IntUnaryOperator var_a;

    private /* synthetic */ CLASSNAMEe0(IntUnaryOperator intUnaryOperator) {
        this.var_a = intUnaryOperator;
    }

    public static /* synthetic */ A b(IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof CLASSNAMEf0 ? ((CLASSNAMEf0) intUnaryOperator).var_a : new CLASSNAMEe0(intUnaryOperator);
    }

    public /* synthetic */ int a(int i) {
        return this.var_a.applyAsInt(i);
    }
}
