package j$;

import j$.util.function.Predicate;

public final /* synthetic */ class x0 implements Predicate {
    final /* synthetic */ java.util.function.Predicate a;

    private /* synthetic */ x0(java.util.function.Predicate predicate) {
        this.a = predicate;
    }

    public static /* synthetic */ Predicate c(java.util.function.Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof y0 ? ((y0) predicate).a : new x0(predicate);
    }

    public /* synthetic */ Predicate a(Predicate predicate) {
        return c(this.a.or(y0.a(predicate)));
    }

    public /* synthetic */ Predicate b(Predicate predicate) {
        return c(this.a.and(y0.a(predicate)));
    }

    public /* synthetic */ Predicate negate() {
        return c(this.a.negate());
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.a.test(obj);
    }
}
