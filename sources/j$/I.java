package j$;

import java.util.function.DoublePredicate;

public final /* synthetic */ class I implements DoublePredicate {
    final /* synthetic */ H a;

    public static /* synthetic */ DoublePredicate a(H h) {
        if (h == null) {
            return null;
        }
        return h.a;
    }

    public DoublePredicate and(DoublePredicate doublePredicate) {
        return a(H.a(this.a.a.and(a(H.a(doublePredicate)))));
    }

    public DoublePredicate negate() {
        return a(H.a(this.a.a.negate()));
    }

    public DoublePredicate or(DoublePredicate doublePredicate) {
        return a(H.a(this.a.a.or(a(H.a(doublePredicate)))));
    }

    public boolean test(double d) {
        return this.a.a.test(d);
    }
}
