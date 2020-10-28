package j$;

import j$.util.function.x;
import java.util.function.LongBinaryOperator;

/* renamed from: j$.h0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh0 implements LongBinaryOperator {
    final /* synthetic */ x a;

    private /* synthetic */ CLASSNAMEh0(x xVar) {
        this.a = xVar;
    }

    public static /* synthetic */ LongBinaryOperator a(x xVar) {
        if (xVar == null) {
            return null;
        }
        return xVar instanceof CLASSNAMEg0 ? ((CLASSNAMEg0) xVar).a : new CLASSNAMEh0(xVar);
    }

    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
