package a;

import java.util.function.Predicate;

public final /* synthetic */ class B0 implements Predicate {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.function.Predicate f8a;

    private /* synthetic */ B0(j$.util.function.Predicate predicate) {
        this.f8a = predicate;
    }

    public static /* synthetic */ Predicate a(j$.util.function.Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof A0 ? ((A0) predicate).f6a : new B0(predicate);
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return a(this.f8a.b(A0.c(predicate)));
    }

    public /* synthetic */ Predicate negate() {
        return a(this.f8a.negate());
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return a(this.f8a.a(A0.c(predicate)));
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.f8a.test(obj);
    }
}
