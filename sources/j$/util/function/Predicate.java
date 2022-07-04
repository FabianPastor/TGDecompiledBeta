package j$.util.function;

public interface Predicate<T> {

    /* renamed from: j$.util.function.Predicate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Predicate $default$and(Predicate predicate, Predicate predicate2) {
            predicate2.getClass();
            return new x(predicate, predicate2, 0);
        }

        public static Predicate $default$negate(Predicate predicate) {
            return new CLASSNAMEa(predicate);
        }

        public static Predicate $default$or(Predicate predicate, Predicate predicate2) {
            predicate2.getClass();
            return new x(predicate, predicate2, 1);
        }
    }

    Predicate<T> and(Predicate<? super T> predicate);

    Predicate<T> negate();

    Predicate<T> or(Predicate<? super T> predicate);

    boolean test(T t);
}
