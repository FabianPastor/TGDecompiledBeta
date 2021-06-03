package j$;

import j$.util.function.F;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.q0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq0 implements LongUnaryOperator {
    final /* synthetic */ F a;

    private /* synthetic */ CLASSNAMEq0(F f) {
        this.a = f;
    }

    public static /* synthetic */ LongUnaryOperator a(F f) {
        if (f == null) {
            return null;
        }
        return f instanceof CLASSNAMEp0 ? ((CLASSNAMEp0) f).a : new CLASSNAMEq0(f);
    }

    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return a(this.a.a(CLASSNAMEp0.c(longUnaryOperator)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return a(this.a.b(CLASSNAMEp0.c(longUnaryOperator)));
    }
}
