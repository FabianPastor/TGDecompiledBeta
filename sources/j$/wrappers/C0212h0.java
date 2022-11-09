package j$.wrappers;

import java.util.function.LongFunction;
/* renamed from: j$.wrappers.h0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEh0 implements j$.util.function.r {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ CLASSNAMEh0(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ j$.util.function.r a(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof CLASSNAMEi0 ? ((CLASSNAMEi0) longFunction).a : new CLASSNAMEh0(longFunction);
    }

    @Override // j$.util.function.r
    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
