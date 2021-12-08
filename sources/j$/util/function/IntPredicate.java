package j$.util.function;

public interface IntPredicate {
    IntPredicate and(IntPredicate intPredicate);

    IntPredicate negate();

    IntPredicate or(IntPredicate intPredicate);

    boolean test(int i);

    /* renamed from: j$.util.function.IntPredicate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static IntPredicate $default$and(IntPredicate _this, IntPredicate other) {
            other.getClass();
            return new IntPredicate$$ExternalSyntheticLambda1(_this, other);
        }

        public static /* synthetic */ boolean lambda$and$0(IntPredicate _this, IntPredicate other, int value) {
            return _this.test(value) && other.test(value);
        }

        public static IntPredicate $default$negate(IntPredicate _this) {
            return new IntPredicate$$ExternalSyntheticLambda0(_this);
        }

        public static /* synthetic */ boolean lambda$negate$1(IntPredicate _this, int value) {
            return !_this.test(value);
        }

        public static IntPredicate $default$or(IntPredicate _this, IntPredicate other) {
            other.getClass();
            return new IntPredicate$$ExternalSyntheticLambda2(_this, other);
        }

        public static /* synthetic */ boolean lambda$or$2(IntPredicate _this, IntPredicate other, int value) {
            return _this.test(value) || other.test(value);
        }
    }
}
