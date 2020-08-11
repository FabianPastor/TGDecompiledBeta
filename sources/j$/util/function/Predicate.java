package j$.util.function;

public interface Predicate {
    Predicate a(Predicate predicate);

    Predicate b(Predicate predicate);

    Predicate negate();

    boolean test(Object obj);
}
