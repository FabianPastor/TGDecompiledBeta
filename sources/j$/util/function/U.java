package j$.util.function;

public final /* synthetic */ class U {
    public static Predicate a(Predicate _this, Predicate predicate) {
        predicate.getClass();
        return new CLASSNAMEk(_this, predicate);
    }

    public static /* synthetic */ boolean d(Predicate _this, Predicate other, Object t) {
        return _this.test(t) && other.test(t);
    }

    public static Predicate b(Predicate _this) {
        return new CLASSNAMEl(_this);
    }

    public static /* synthetic */ boolean e(Predicate _this, Object t) {
        return !_this.test(t);
    }

    public static Predicate c(Predicate _this, Predicate predicate) {
        predicate.getClass();
        return new CLASSNAMEj(_this, predicate);
    }

    public static /* synthetic */ boolean f(Predicate _this, Predicate other, Object t) {
        return _this.test(t) || other.test(t);
    }
}
