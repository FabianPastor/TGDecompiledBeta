package a;

import j$.util.function.Predicate;

public final /* synthetic */ class A0 implements Predicate {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.function.Predicate f6a;

    private /* synthetic */ A0(java.util.function.Predicate predicate) {
        this.f6a = predicate;
    }

    public static /* synthetic */ Predicate c(java.util.function.Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof B0 ? ((B0) predicate).f8a : new A0(predicate);
    }

    public /* synthetic */ Predicate a(Predicate predicate) {
        return c(this.f6a.or(B0.a(predicate)));
    }

    public /* synthetic */ Predicate b(Predicate predicate) {
        return c(this.f6a.and(B0.a(predicate)));
    }

    public /* synthetic */ Predicate negate() {
        return c(this.f6a.negate());
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.f6a.test(obj);
    }
}
