package j$.util.function;

/* renamed from: j$.util.function.l  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl implements Predicate {
    public final /* synthetic */ Predicate a;

    public /* synthetic */ CLASSNAMEl(Predicate predicate) {
        this.a = predicate;
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
        return U.e(this.a, obj);
    }
}
