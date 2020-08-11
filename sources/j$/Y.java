package j$;

import j$.util.function.P;
import java.util.function.LongUnaryOperator;

public final /* synthetic */ class Y implements P {
    final /* synthetic */ LongUnaryOperator a;

    private /* synthetic */ Y(LongUnaryOperator longUnaryOperator) {
        this.a = longUnaryOperator;
    }

    public static /* synthetic */ P c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof Z ? ((Z) longUnaryOperator).a : new Y(longUnaryOperator);
    }

    public /* synthetic */ P a(P p) {
        return c(this.a.andThen(Z.a(p)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ P b(P p) {
        return c(this.a.compose(Z.a(p)));
    }
}
