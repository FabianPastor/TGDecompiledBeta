package j$;

import j$.util.function.F;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.s0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs0 implements F {
    final /* synthetic */ LongUnaryOperator a;

    private /* synthetic */ CLASSNAMEs0(LongUnaryOperator longUnaryOperator) {
        this.a = longUnaryOperator;
    }

    public static /* synthetic */ F c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof CLASSNAMEt0 ? ((CLASSNAMEt0) longUnaryOperator).a : new CLASSNAMEs0(longUnaryOperator);
    }

    public /* synthetic */ F a(F f) {
        return c(this.a.andThen(CLASSNAMEt0.a(f)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ F b(F f) {
        return c(this.a.compose(CLASSNAMEt0.a(f)));
    }
}
