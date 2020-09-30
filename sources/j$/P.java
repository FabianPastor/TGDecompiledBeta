package j$;

import j$.util.function.H;
import java.util.function.LongBinaryOperator;

public final /* synthetic */ class P implements H {
    final /* synthetic */ LongBinaryOperator a;

    private /* synthetic */ P(LongBinaryOperator longBinaryOperator) {
        this.a = longBinaryOperator;
    }

    public static /* synthetic */ H b(LongBinaryOperator longBinaryOperator) {
        if (longBinaryOperator == null) {
            return null;
        }
        return new P(longBinaryOperator);
    }

    public /* synthetic */ long a(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
