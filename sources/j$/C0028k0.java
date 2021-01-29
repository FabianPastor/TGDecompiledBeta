package j$;

import j$.util.function.D;
import java.util.function.LongFunction;

/* renamed from: j$.k0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk0 implements D {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ CLASSNAMEk0(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ D a(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof CLASSNAMEl0 ? ((CLASSNAMEl0) longFunction).a : new CLASSNAMEk0(longFunction);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
