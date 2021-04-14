package a;

import j$.util.function.B;
import java.util.function.LongBinaryOperator;

/* renamed from: a.g0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg0 implements B {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LongBinaryOperator var_a;

    private /* synthetic */ CLASSNAMEg0(LongBinaryOperator longBinaryOperator) {
        this.var_a = longBinaryOperator;
    }

    public static /* synthetic */ B a(LongBinaryOperator longBinaryOperator) {
        if (longBinaryOperator == null) {
            return null;
        }
        return longBinaryOperator instanceof CLASSNAMEh0 ? ((CLASSNAMEh0) longBinaryOperator).var_a : new CLASSNAMEg0(longBinaryOperator);
    }

    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.var_a.applyAsLong(j, j2);
    }
}
