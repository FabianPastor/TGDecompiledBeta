package j$.wrappers;

import j$.util.function.r;
import java.util.function.LongFunction;

/* renamed from: j$.wrappers.h0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh0 implements r {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ CLASSNAMEh0(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ r a(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof CLASSNAMEi0 ? ((CLASSNAMEi0) longFunction).a : new CLASSNAMEh0(longFunction);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
