package j$.util.function;

public interface Consumer<T> {

    /* renamed from: j$.util.function.Consumer$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Consumer $default$andThen(Consumer consumer, Consumer consumer2) {
            consumer2.getClass();
            return new CLASSNAMEe(consumer, consumer2);
        }
    }

    void accept(Object obj);

    Consumer andThen(Consumer consumer);
}
