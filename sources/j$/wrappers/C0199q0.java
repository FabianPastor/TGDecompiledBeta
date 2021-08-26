package j$.wrappers;

import j$.util.function.s;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.wrappers.q0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq0 implements LongUnaryOperator {
    final /* synthetic */ s a;

    private /* synthetic */ CLASSNAMEq0(s sVar) {
        this.a = sVar;
    }

    public static /* synthetic */ LongUnaryOperator a(s sVar) {
        if (sVar == null) {
            return null;
        }
        return sVar instanceof CLASSNAMEp0 ? ((CLASSNAMEp0) sVar).a : new CLASSNAMEq0(sVar);
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
