package j$;

import j$.util.function.H;
import java.util.function.LongBinaryOperator;

public final /* synthetic */ class b0 implements H {
    final /* synthetic */ LongBinaryOperator a;

    private /* synthetic */ b0(LongBinaryOperator longBinaryOperator) {
        this.a = longBinaryOperator;
    }

    public static /* synthetic */ H b(LongBinaryOperator longBinaryOperator) {
        if (longBinaryOperator == null) {
            return null;
        }
        return new b0(longBinaryOperator);
    }

    public /* synthetic */ long a(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
