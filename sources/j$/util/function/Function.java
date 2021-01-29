package j$.util.function;

public interface Function<T, R> {
    Function a(Function function);

    Object apply(Object obj);

    Function b(Function function);
}
