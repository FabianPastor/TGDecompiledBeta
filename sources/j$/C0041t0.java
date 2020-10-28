package j$;

import j$.util.function.A;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.t0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEt0 implements LongUnaryOperator {
    final /* synthetic */ A a;

    private /* synthetic */ CLASSNAMEt0(A a2) {
        this.a = a2;
    }

    public static /* synthetic */ LongUnaryOperator a(A a2) {
        if (a2 == null) {
            return null;
        }
        return a2 instanceof CLASSNAMEs0 ? ((CLASSNAMEs0) a2).a : new CLASSNAMEt0(a2);
    }

    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return a(this.a.a(CLASSNAMEs0.c(longUnaryOperator)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return a(this.a.b(CLASSNAMEs0.c(longUnaryOperator)));
    }
}
