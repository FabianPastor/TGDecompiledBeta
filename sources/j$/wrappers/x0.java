package j$.wrappers;

import j$.util.function.Predicate;
/* loaded from: classes2.dex */
public final /* synthetic */ class x0 implements Predicate {
    final /* synthetic */ java.util.function.Predicate a;

    private /* synthetic */ x0(java.util.function.Predicate predicate) {
        this.a = predicate;
    }

    public static /* synthetic */ Predicate a(java.util.function.Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof y0 ? ((y0) predicate).a : new x0(predicate);
    }

    @Override // j$.util.function.Predicate
    public /* synthetic */ Predicate and(Predicate predicate) {
        return a(this.a.and(y0.a(predicate)));
    }

    @Override // j$.util.function.Predicate
    public /* synthetic */ Predicate negate() {
        return a(this.a.negate());
    }

    @Override // j$.util.function.Predicate
    public /* synthetic */ Predicate or(Predicate predicate) {
        return a(this.a.or(y0.a(predicate)));
    }

    @Override // j$.util.function.Predicate
    public /* synthetic */ boolean test(Object obj) {
        return this.a.test(obj);
    }
}
