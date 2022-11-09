package j$.wrappers;

import java.util.function.LongFunction;
/* renamed from: j$.wrappers.i0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEi0 implements LongFunction {
    final /* synthetic */ j$.util.function.r a;

    private /* synthetic */ CLASSNAMEi0(j$.util.function.r rVar) {
        this.a = rVar;
    }

    public static /* synthetic */ LongFunction a(j$.util.function.r rVar) {
        if (rVar == null) {
            return null;
        }
        return rVar instanceof CLASSNAMEh0 ? ((CLASSNAMEh0) rVar).a : new CLASSNAMEi0(rVar);
    }

    @Override // java.util.function.LongFunction
    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
