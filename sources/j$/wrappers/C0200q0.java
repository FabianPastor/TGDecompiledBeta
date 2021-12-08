package j$.wrappers;

import j$.util.function.t;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.wrappers.q0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq0 implements LongUnaryOperator {
    final /* synthetic */ t a;

    private /* synthetic */ CLASSNAMEq0(t tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ LongUnaryOperator a(t tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof CLASSNAMEp0 ? ((CLASSNAMEp0) tVar).a : new CLASSNAMEq0(tVar);
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
