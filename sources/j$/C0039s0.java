package j$;

import j$.util.function.A;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.s0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs0 implements A {
    final /* synthetic */ LongUnaryOperator a;

    private /* synthetic */ CLASSNAMEs0(LongUnaryOperator longUnaryOperator) {
        this.a = longUnaryOperator;
    }

    public static /* synthetic */ A c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof CLASSNAMEt0 ? ((CLASSNAMEt0) longUnaryOperator).a : new CLASSNAMEs0(longUnaryOperator);
    }

    public /* synthetic */ A a(A a2) {
        return c(this.a.andThen(CLASSNAMEt0.a(a2)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ A b(A a2) {
        return c(this.a.compose(CLASSNAMEt0.a(a2)));
    }
}
