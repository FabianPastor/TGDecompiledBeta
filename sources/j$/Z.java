package j$;

import j$.util.function.y;
import java.util.function.IntPredicate;

public final /* synthetic */ class Z implements IntPredicate {
    final /* synthetic */ y a;

    private /* synthetic */ Z(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ IntPredicate a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof Y ? ((Y) yVar).a : new Z(yVar);
    }

    public /* synthetic */ IntPredicate and(IntPredicate intPredicate) {
        return a(Y.a(((Y) this.a).a.and(a(Y.a(intPredicate)))));
    }

    public /* synthetic */ IntPredicate negate() {
        return a(Y.a(((Y) this.a).a.negate()));
    }

    public /* synthetic */ IntPredicate or(IntPredicate intPredicate) {
        return a(Y.a(((Y) this.a).a.or(a(Y.a(intPredicate)))));
    }

    public /* synthetic */ boolean test(int i) {
        return ((Y) this.a).a.test(i);
    }
}
