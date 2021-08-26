package j$.wrappers;

import j$.util.function.q;
import java.util.function.LongFunction;

/* renamed from: j$.wrappers.i0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi0 implements LongFunction {
    final /* synthetic */ q a;

    private /* synthetic */ CLASSNAMEi0(q qVar) {
        this.a = qVar;
    }

    public static /* synthetic */ LongFunction a(q qVar) {
        if (qVar == null) {
            return null;
        }
        return qVar instanceof CLASSNAMEh0 ? ((CLASSNAMEh0) qVar).a : new CLASSNAMEi0(qVar);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
