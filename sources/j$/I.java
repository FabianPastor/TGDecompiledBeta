package j$;

import j$.util.function.s;
import java.util.function.DoublePredicate;

public final /* synthetic */ class I implements DoublePredicate {
    final /* synthetic */ s a;

    private /* synthetic */ I(s sVar) {
        this.a = sVar;
    }

    public static /* synthetic */ DoublePredicate a(s sVar) {
        if (sVar == null) {
            return null;
        }
        return sVar instanceof H ? ((H) sVar).a : new I(sVar);
    }

    public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
        return a(H.a(((H) this.a).a.and(a(H.a(doublePredicate)))));
    }

    public /* synthetic */ DoublePredicate negate() {
        return a(H.a(((H) this.a).a.negate()));
    }

    public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
        return a(H.a(((H) this.a).a.or(a(H.a(doublePredicate)))));
    }

    public /* synthetic */ boolean test(double d) {
        return ((H) this.a).a.test(d);
    }
}
