package j$;

import j$.util.function.s;
import java.util.function.DoublePredicate;

public final /* synthetic */ class F implements DoublePredicate {
    final /* synthetic */ s a;

    private /* synthetic */ F(s sVar) {
        this.a = sVar;
    }

    public static /* synthetic */ DoublePredicate a(s sVar) {
        if (sVar == null) {
            return null;
        }
        return sVar instanceof E ? ((E) sVar).a : new F(sVar);
    }

    public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
        return a(E.a(((E) this.a).a.and(a(E.a(doublePredicate)))));
    }

    public /* synthetic */ DoublePredicate negate() {
        return a(E.a(((E) this.a).a.negate()));
    }

    public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
        return a(E.a(((E) this.a).a.or(a(E.a(doublePredicate)))));
    }

    public /* synthetic */ boolean test(double d) {
        return ((E) this.a).a.test(d);
    }
}
