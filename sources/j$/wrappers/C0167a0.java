package j$.wrappers;

import j$.util.function.m;
import java.util.function.IntToLongFunction;

/* renamed from: j$.wrappers.a0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa0 implements IntToLongFunction {
    final /* synthetic */ m a;

    private /* synthetic */ CLASSNAMEa0(m mVar) {
        this.a = mVar;
    }

    public static /* synthetic */ IntToLongFunction a(m mVar) {
        if (mVar == null) {
            return null;
        }
        return mVar instanceof Z ? ((Z) mVar).a : new CLASSNAMEa0(mVar);
    }

    public /* synthetic */ long applyAsLong(int i) {
        return this.a.applyAsLong(i);
    }
}
