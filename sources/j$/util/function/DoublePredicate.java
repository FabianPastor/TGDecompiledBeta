package j$.util.function;

public interface DoublePredicate {
    DoublePredicate and(DoublePredicate doublePredicate);

    DoublePredicate negate();

    DoublePredicate or(DoublePredicate doublePredicate);

    boolean test(double d);

    /* renamed from: j$.util.function.DoublePredicate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static DoublePredicate $default$and(DoublePredicate _this, DoublePredicate other) {
            other.getClass();
            return new DoublePredicate$$ExternalSyntheticLambda1(_this, other);
        }

        public static /* synthetic */ boolean lambda$and$0(DoublePredicate _this, DoublePredicate other, double value) {
            return _this.test(value) && other.test(value);
        }

        public static DoublePredicate $default$negate(DoublePredicate _this) {
            return new DoublePredicate$$ExternalSyntheticLambda0(_this);
        }

        public static /* synthetic */ boolean lambda$negate$1(DoublePredicate _this, double value) {
            return !_this.test(value);
        }

        public static DoublePredicate $default$or(DoublePredicate _this, DoublePredicate other) {
            other.getClass();
            return new DoublePredicate$$ExternalSyntheticLambda2(_this, other);
        }

        public static /* synthetic */ boolean lambda$or$2(DoublePredicate _this, DoublePredicate other, double value) {
            return _this.test(value) || other.test(value);
        }
    }
}
