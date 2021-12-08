package j$.util.function;

public interface LongPredicate {
    LongPredicate and(LongPredicate longPredicate);

    LongPredicate negate();

    LongPredicate or(LongPredicate longPredicate);

    boolean test(long j);

    /* renamed from: j$.util.function.LongPredicate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static LongPredicate $default$and(LongPredicate _this, LongPredicate other) {
            other.getClass();
            return new LongPredicate$$ExternalSyntheticLambda1(_this, other);
        }

        public static /* synthetic */ boolean lambda$and$0(LongPredicate _this, LongPredicate other, long value) {
            return _this.test(value) && other.test(value);
        }

        public static LongPredicate $default$negate(LongPredicate _this) {
            return new LongPredicate$$ExternalSyntheticLambda0(_this);
        }

        public static /* synthetic */ boolean lambda$negate$1(LongPredicate _this, long value) {
            return !_this.test(value);
        }

        public static LongPredicate $default$or(LongPredicate _this, LongPredicate other) {
            other.getClass();
            return new LongPredicate$$ExternalSyntheticLambda2(_this, other);
        }

        public static /* synthetic */ boolean lambda$or$2(LongPredicate _this, LongPredicate other, long value) {
            return _this.test(value) || other.test(value);
        }
    }
}
