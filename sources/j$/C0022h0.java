package j$;

import j$.util.function.D;
import java.util.function.LongFunction;

/* renamed from: j$.h0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh0 implements D {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ CLASSNAMEh0(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ D a(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof CLASSNAMEi0 ? ((CLASSNAMEi0) longFunction).a : new CLASSNAMEh0(longFunction);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
