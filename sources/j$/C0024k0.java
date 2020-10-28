package j$;

import j$.util.function.z;
import java.util.function.LongFunction;

/* renamed from: j$.k0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk0 implements z {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ CLASSNAMEk0(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ z a(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof CLASSNAMEl0 ? ((CLASSNAMEl0) longFunction).a : new CLASSNAMEk0(longFunction);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
