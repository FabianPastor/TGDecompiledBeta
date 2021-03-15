package a;

import j$.util.function.s;
import java.util.function.DoublePredicate;

public final /* synthetic */ class I implements DoublePredicate {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s var_a;

    private /* synthetic */ I(s sVar) {
        this.var_a = sVar;
    }

    public static /* synthetic */ DoublePredicate a(s sVar) {
        if (sVar == null) {
            return null;
        }
        return sVar instanceof H ? ((H) sVar).var_a : new I(sVar);
    }

    public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
        return a(H.a(((H) this.var_a).var_a.and(a(H.a(doublePredicate)))));
    }

    public /* synthetic */ DoublePredicate negate() {
        return a(H.a(((H) this.var_a).var_a.negate()));
    }

    public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
        return a(H.a(((H) this.var_a).var_a.or(a(H.a(doublePredicate)))));
    }

    public /* synthetic */ boolean test(double d) {
        return ((H) this.var_a).var_a.test(d);
    }
}
