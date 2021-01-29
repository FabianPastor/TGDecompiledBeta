package j$.util.function;

public interface Consumer<T> {
    void accept(Object obj);

    Consumer f(Consumer consumer);
}
