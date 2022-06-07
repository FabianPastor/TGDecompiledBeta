package j$.wrappers;

import j$.util.function.t;
import java.util.function.LongUnaryOperator;

public final /* synthetic */ class q0 implements LongUnaryOperator {
    final /* synthetic */ t a;

    private /* synthetic */ q0(t tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ LongUnaryOperator a(t tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof CLASSNAMEp0 ? ((CLASSNAMEp0) tVar).a : new q0(tVar);
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
