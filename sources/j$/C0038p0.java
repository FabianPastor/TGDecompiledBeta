package j$;

import j$.util.function.F;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.p0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp0 implements F {
    final /* synthetic */ LongUnaryOperator a;

    private /* synthetic */ CLASSNAMEp0(LongUnaryOperator longUnaryOperator) {
        this.a = longUnaryOperator;
    }

    public static /* synthetic */ F c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof CLASSNAMEq0 ? ((CLASSNAMEq0) longUnaryOperator).a : new CLASSNAMEp0(longUnaryOperator);
    }

    public /* synthetic */ F a(F f) {
        return c(this.a.andThen(CLASSNAMEq0.a(f)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ F b(F f) {
        return c(this.a.compose(CLASSNAMEq0.a(f)));
    }
}
