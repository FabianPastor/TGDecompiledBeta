package j$.util.function;

public final /* synthetic */ class l implements Predicate {
    public final /* synthetic */ Predicate a;

    public /* synthetic */ l(Predicate predicate) {
        this.a = predicate;
    }

    public Predicate a(Predicate predicate) {
        predicate.getClass();
        return new k(this, predicate);
    }

    public Predicate b(Predicate predicate) {
        predicate.getClass();
        return new m(this, predicate);
    }

    public Predicate negate() {
        return new l(this);
    }

    public final boolean test(Object obj) {
        return !this.a.test(obj);
    }
}
