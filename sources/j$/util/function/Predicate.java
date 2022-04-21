package j$.util.function;

public interface Predicate<T> {
    Predicate<T> and(Predicate<? super T> predicate);

    Predicate<T> negate();

    Predicate<T> or(Predicate<? super T> predicate);

    boolean test(T t);

    /* renamed from: j$.util.function.Predicate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Predicate $default$and(Predicate _this, Predicate predicate) {
            predicate.getClass();
            return new Predicate$$ExternalSyntheticLambda2(_this, predicate);
        }

        public static /* synthetic */ boolean lambda$and$0(Predicate _this, Predicate other, Object t) {
            return _this.test(t) && other.test(t);
        }

        public static Predicate $default$negate(Predicate _this) {
            return new Predicate$$ExternalSyntheticLambda1(_this);
        }

        public static /* synthetic */ boolean lambda$negate$1(Predicate _this, Object t) {
            return !_this.test(t);
        }

        public static Predicate $default$or(Predicate _this, Predicate predicate) {
            predicate.getClass();
            return new Predicate$$ExternalSyntheticLambda3(_this, predicate);
        }

        public static /* synthetic */ boolean lambda$or$2(Predicate _this, Predicate other, Object t) {
            return _this.test(t) || other.test(t);
        }

        public static <T> Predicate<T> isEqual(Object targetRef) {
            if (targetRef == null) {
                return Predicate$$ExternalSyntheticLambda4.INSTANCE;
            }
            return new Predicate$$ExternalSyntheticLambda0(targetRef);
        }
    }
}
