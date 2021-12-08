package j$.util.function;

public interface BiPredicate<T, U> {
    BiPredicate<T, U> and(BiPredicate<? super T, ? super U> biPredicate);

    BiPredicate<T, U> negate();

    BiPredicate<T, U> or(BiPredicate<? super T, ? super U> biPredicate);

    boolean test(T t, U u);

    /* renamed from: j$.util.function.BiPredicate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static BiPredicate $default$and(BiPredicate _this, BiPredicate biPredicate) {
            biPredicate.getClass();
            return new BiPredicate$$ExternalSyntheticLambda1(_this, biPredicate);
        }

        public static /* synthetic */ boolean lambda$and$0(BiPredicate _this, BiPredicate other, Object t, Object u) {
            return _this.test(t, u) && other.test(t, u);
        }

        public static BiPredicate $default$negate(BiPredicate _this) {
            return new BiPredicate$$ExternalSyntheticLambda0(_this);
        }

        public static /* synthetic */ boolean lambda$negate$1(BiPredicate _this, Object t, Object u) {
            return !_this.test(t, u);
        }

        public static BiPredicate $default$or(BiPredicate _this, BiPredicate biPredicate) {
            biPredicate.getClass();
            return new BiPredicate$$ExternalSyntheticLambda2(_this, biPredicate);
        }

        public static /* synthetic */ boolean lambda$or$2(BiPredicate _this, BiPredicate other, Object t, Object u) {
            return _this.test(t, u) || other.test(t, u);
        }
    }
}
