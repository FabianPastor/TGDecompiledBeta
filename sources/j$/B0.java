package j$;

import java.util.function.Predicate;

public final /* synthetic */ class B0 implements Predicate {
    final /* synthetic */ j$.util.function.Predicate a;

    private /* synthetic */ B0(j$.util.function.Predicate predicate) {
        this.a = predicate;
    }

    public static /* synthetic */ Predicate a(j$.util.function.Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof A0 ? ((A0) predicate).a : new B0(predicate);
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return a(this.a.b(A0.c(predicate)));
    }

    public /* synthetic */ Predicate negate() {
        return a(this.a.negate());
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return a(this.a.a(A0.c(predicate)));
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.a.test(obj);
    }
}
