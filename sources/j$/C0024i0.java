package j$;

import j$.util.function.D;
import java.util.function.LongFunction;

/* renamed from: j$.i0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi0 implements LongFunction {
    final /* synthetic */ D a;

    private /* synthetic */ CLASSNAMEi0(D d) {
        this.a = d;
    }

    public static /* synthetic */ LongFunction a(D d) {
        if (d == null) {
            return null;
        }
        return d instanceof CLASSNAMEh0 ? ((CLASSNAMEh0) d).a : new CLASSNAMEi0(d);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
