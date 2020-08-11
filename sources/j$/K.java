package j$;

import j$.util.function.D;
import java.util.function.IntPredicate;

public final /* synthetic */ class K implements IntPredicate {
    final /* synthetic */ D a;

    private /* synthetic */ K(D d) {
        this.a = d;
    }

    public static /* synthetic */ IntPredicate a(D d) {
        if (d == null) {
            return null;
        }
        return d instanceof J ? ((J) d).a : new K(d);
    }

    public /* synthetic */ IntPredicate and(IntPredicate intPredicate) {
        return a(((J) this.a).a(J.b(intPredicate)));
    }

    public /* synthetic */ IntPredicate negate() {
        return a(((J) this.a).c());
    }

    public /* synthetic */ IntPredicate or(IntPredicate intPredicate) {
        return a(((J) this.a).d(J.b(intPredicate)));
    }

    public /* synthetic */ boolean test(int i) {
        return ((J) this.a).e(i);
    }
}
