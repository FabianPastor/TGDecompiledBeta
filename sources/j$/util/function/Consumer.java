package j$.util.function;

public interface Consumer {
    void accept(Object obj);

    Consumer f(Consumer consumer);
}
