package j$;

import j$.util.function.P;
import java.util.function.LongUnaryOperator;

public final /* synthetic */ class j0 implements P {
    final /* synthetic */ LongUnaryOperator a;

    private /* synthetic */ j0(LongUnaryOperator longUnaryOperator) {
        this.a = longUnaryOperator;
    }

    public static /* synthetic */ P c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof k0 ? ((k0) longUnaryOperator).a : new j0(longUnaryOperator);
    }

    public /* synthetic */ P a(P p) {
        return c(this.a.andThen(k0.a(p)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ P b(P p) {
        return c(this.a.compose(k0.a(p)));
    }
}
