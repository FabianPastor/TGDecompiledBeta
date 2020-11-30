package j$;

import j$.util.function.F;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.t0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEt0 implements LongUnaryOperator {
    final /* synthetic */ F a;

    private /* synthetic */ CLASSNAMEt0(F f) {
        this.a = f;
    }

    public static /* synthetic */ LongUnaryOperator a(F f) {
        if (f == null) {
            return null;
        }
        return f instanceof CLASSNAMEs0 ? ((CLASSNAMEs0) f).a : new CLASSNAMEt0(f);
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
