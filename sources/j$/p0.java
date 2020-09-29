package j$;

import java.util.function.Predicate;

public final /* synthetic */ class p0 implements Predicate {
    final /* synthetic */ j$.util.function.Predicate a;

    private /* synthetic */ p0(j$.util.function.Predicate predicate) {
        this.a = predicate;
    }

    public static /* synthetic */ Predicate a(j$.util.function.Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof o0 ? ((o0) predicate).a : new p0(predicate);
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return a(this.a.b(o0.c(predicate)));
    }

    public /* synthetic */ Predicate negate() {
        return a(this.a.negate());
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return a(this.a.a(o0.c(predicate)));
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.a.test(obj);
    }
}
