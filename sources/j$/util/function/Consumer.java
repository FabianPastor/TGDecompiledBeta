package j$.util.function;

import j$.util.concurrent.a;

public interface Consumer<T> {

    /* renamed from: j$.util.function.Consumer$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Consumer $default$andThen(Consumer consumer, Consumer consumer2) {
            consumer2.getClass();
            return new a(consumer, consumer2);
        }
    }

    void accept(T t);

    Consumer<T> andThen(Consumer<? super T> consumer);
}
