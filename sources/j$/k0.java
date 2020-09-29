package j$;

import j$.util.function.P;
import java.util.function.LongUnaryOperator;

public final /* synthetic */ class k0 implements LongUnaryOperator {
    final /* synthetic */ P a;

    private /* synthetic */ k0(P p) {
        this.a = p;
    }

    public static /* synthetic */ LongUnaryOperator a(P p) {
        if (p == null) {
            return null;
        }
        return p instanceof j0 ? ((j0) p).a : new k0(p);
    }

    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return a(this.a.a(j0.c(longUnaryOperator)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return a(this.a.b(j0.c(longUnaryOperator)));
    }
}
