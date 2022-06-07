package j$.wrappers;

import j$.util.function.t;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.wrappers.p0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp0 implements t {
    final /* synthetic */ LongUnaryOperator a;

    private /* synthetic */ CLASSNAMEp0(LongUnaryOperator longUnaryOperator) {
        this.a = longUnaryOperator;
    }

    public static /* synthetic */ t c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof q0 ? ((q0) longUnaryOperator).a : new CLASSNAMEp0(longUnaryOperator);
    }

    public /* synthetic */ t a(t tVar) {
        return c(this.a.andThen(q0.a(tVar)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ t b(t tVar) {
        return c(this.a.compose(q0.a(tVar)));
    }
}
