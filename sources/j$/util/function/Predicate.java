package j$.util.function;

public interface Predicate<T> {
    Predicate a(Predicate predicate);

    Predicate b(Predicate predicate);

    Predicate negate();

    boolean test(Object obj);
}
