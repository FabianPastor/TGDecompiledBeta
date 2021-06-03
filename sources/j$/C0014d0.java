package j$;

import j$.util.function.B;
import java.util.function.LongBinaryOperator;

/* renamed from: j$.d0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd0 implements B {
    final /* synthetic */ LongBinaryOperator a;

    private /* synthetic */ CLASSNAMEd0(LongBinaryOperator longBinaryOperator) {
        this.a = longBinaryOperator;
    }

    public static /* synthetic */ B a(LongBinaryOperator longBinaryOperator) {
        if (longBinaryOperator == null) {
            return null;
        }
        return longBinaryOperator instanceof CLASSNAMEe0 ? ((CLASSNAMEe0) longBinaryOperator).a : new CLASSNAMEd0(longBinaryOperator);
    }

    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
