package j$.wrappers;

import j$.util.function.n;
import java.util.function.LongBinaryOperator;

/* renamed from: j$.wrappers.e0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe0 implements LongBinaryOperator {
    final /* synthetic */ n a;

    private /* synthetic */ CLASSNAMEe0(n nVar) {
        this.a = nVar;
    }

    public static /* synthetic */ LongBinaryOperator a(n nVar) {
        if (nVar == null) {
            return null;
        }
        return nVar instanceof CLASSNAMEd0 ? ((CLASSNAMEd0) nVar).a : new CLASSNAMEe0(nVar);
    }

    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
