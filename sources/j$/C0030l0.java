package j$;

import j$.util.function.D;
import java.util.function.LongFunction;

/* renamed from: j$.l0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl0 implements LongFunction {
    final /* synthetic */ D a;

    private /* synthetic */ CLASSNAMEl0(D d) {
        this.a = d;
    }

    public static /* synthetic */ LongFunction a(D d) {
        if (d == null) {
            return null;
        }
        return d instanceof CLASSNAMEk0 ? ((CLASSNAMEk0) d).a : new CLASSNAMEl0(d);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
