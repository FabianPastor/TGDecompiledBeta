package j$.wrappers;

import j$.util.function.q;
import java.util.function.LongFunction;

/* renamed from: j$.wrappers.h0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh0 implements q {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ CLASSNAMEh0(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ q a(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof CLASSNAMEi0 ? ((CLASSNAMEi0) longFunction).a : new CLASSNAMEh0(longFunction);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
