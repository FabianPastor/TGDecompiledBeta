package j$;

import j$.util.function.D;
import java.util.function.IntPredicate;

public final /* synthetic */ class W implements IntPredicate {
    final /* synthetic */ D a;

    private /* synthetic */ W(D d) {
        this.a = d;
    }

    public static /* synthetic */ IntPredicate a(D d) {
        if (d == null) {
            return null;
        }
        return d instanceof V ? ((V) d).a : new W(d);
    }

    public /* synthetic */ IntPredicate and(IntPredicate intPredicate) {
        return a(((V) this.a).a(V.b(intPredicate)));
    }

    public /* synthetic */ IntPredicate negate() {
        return a(((V) this.a).c());
    }

    public /* synthetic */ IntPredicate or(IntPredicate intPredicate) {
        return a(((V) this.a).d(V.b(intPredicate)));
    }

    public /* synthetic */ boolean test(int i) {
        return ((V) this.a).e(i);
    }
}
