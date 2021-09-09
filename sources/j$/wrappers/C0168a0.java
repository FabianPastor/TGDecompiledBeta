package j$.wrappers;

import j$.util.function.n;
import java.util.function.IntToLongFunction;

/* renamed from: j$.wrappers.a0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa0 implements IntToLongFunction {
    final /* synthetic */ n a;

    private /* synthetic */ CLASSNAMEa0(n nVar) {
        this.a = nVar;
    }

    public static /* synthetic */ IntToLongFunction a(n nVar) {
        if (nVar == null) {
            return null;
        }
        return nVar instanceof Z ? ((Z) nVar).a : new CLASSNAMEa0(nVar);
    }

    public /* synthetic */ long applyAsLong(int i) {
        return this.a.applyAsLong(i);
    }
}
