package j$.util.function;

public final /* synthetic */ class k implements Predicate {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Predicate var_a;
    public final /* synthetic */ Predicate b;

    public /* synthetic */ k(Predicate predicate, Predicate predicate2) {
        this.var_a = predicate;
        this.b = predicate2;
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
        return this.var_a.test(obj) || this.b.test(obj);
    }
}
