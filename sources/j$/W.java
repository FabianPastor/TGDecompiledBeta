package j$;

import j$.util.function.y;
import java.util.function.IntPredicate;

public final /* synthetic */ class W implements IntPredicate {
    final /* synthetic */ y a;

    private /* synthetic */ W(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ IntPredicate a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof V ? ((V) yVar).a : new W(yVar);
    }

    public /* synthetic */ IntPredicate and(IntPredicate intPredicate) {
        return a(V.a(((V) this.a).a.and(a(V.a(intPredicate)))));
    }

    public /* synthetic */ IntPredicate negate() {
        return a(V.a(((V) this.a).a.negate()));
    }

    public /* synthetic */ IntPredicate or(IntPredicate intPredicate) {
        return a(V.a(((V) this.a).a.or(a(V.a(intPredicate)))));
    }

    public /* synthetic */ boolean test(int i) {
        return ((V) this.a).a.test(i);
    }
}
