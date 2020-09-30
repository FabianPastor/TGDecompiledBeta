package j$;

import j$.util.function.P;
import java.util.function.LongUnaryOperator;

public final /* synthetic */ class Z implements LongUnaryOperator {
    final /* synthetic */ P a;

    private /* synthetic */ Z(P p) {
        this.a = p;
    }

    public static /* synthetic */ LongUnaryOperator a(P p) {
        if (p == null) {
            return null;
        }
        return p instanceof Y ? ((Y) p).a : new Z(p);
    }

    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return a(this.a.a(Y.c(longUnaryOperator)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return a(this.a.b(Y.c(longUnaryOperator)));
    }
}
