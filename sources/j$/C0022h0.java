package j$;

import j$.util.function.B;
import java.util.function.LongBinaryOperator;

/* renamed from: j$.h0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh0 implements LongBinaryOperator {
    final /* synthetic */ B a;

    private /* synthetic */ CLASSNAMEh0(B b) {
        this.a = b;
    }

    public static /* synthetic */ LongBinaryOperator a(B b) {
        if (b == null) {
            return null;
        }
        return b instanceof CLASSNAMEg0 ? ((CLASSNAMEg0) b).a : new CLASSNAMEh0(b);
    }

    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
