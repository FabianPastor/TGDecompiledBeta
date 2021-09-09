package j$.util.function;

public interface BiFunction<T, U, R> {
    BiFunction andThen(Function function);

    Object apply(Object obj, Object obj2);
}
