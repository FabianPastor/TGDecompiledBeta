package j$.wrappers;

import j$.util.function.r;
import java.util.function.LongFunction;

/* renamed from: j$.wrappers.i0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi0 implements LongFunction {
    final /* synthetic */ r a;

    private /* synthetic */ CLASSNAMEi0(r rVar) {
        this.a = rVar;
    }

    public static /* synthetic */ LongFunction a(r rVar) {
        if (rVar == null) {
            return null;
        }
        return rVar instanceof CLASSNAMEh0 ? ((CLASSNAMEh0) rVar).a : new CLASSNAMEi0(rVar);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
