package j$.util.function;

/* renamed from: j$.util.function.j  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj implements Predicate {
    public final /* synthetic */ Predicate a;
    public final /* synthetic */ Predicate b;

    public /* synthetic */ CLASSNAMEj(Predicate predicate, Predicate predicate2) {
        this.a = predicate;
        this.b = predicate2;
    }

    public /* synthetic */ Predicate a(Predicate predicate) {
        return U.c(this, predicate);
    }

    public /* synthetic */ Predicate b(Predicate predicate) {
        return U.a(this, predicate);
    }

    public /* synthetic */ Predicate negate() {
        return U.b(this);
    }

    public final boolean test(Object obj) {
        return U.f(this.a, this.b, obj);
    }
}
