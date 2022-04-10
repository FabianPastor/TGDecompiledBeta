package j$.wrappers;

import j$.util.function.o;
import java.util.function.LongBinaryOperator;

/* renamed from: j$.wrappers.e0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe0 implements LongBinaryOperator {
    final /* synthetic */ o a;

    private /* synthetic */ CLASSNAMEe0(o oVar) {
        this.a = oVar;
    }

    public static /* synthetic */ LongBinaryOperator a(o oVar) {
        if (oVar == null) {
            return null;
        }
        return oVar instanceof CLASSNAMEd0 ? ((CLASSNAMEd0) oVar).a : new CLASSNAMEe0(oVar);
    }

    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
