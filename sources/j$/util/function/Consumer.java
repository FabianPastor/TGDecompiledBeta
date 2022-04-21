package j$.util.function;

public interface Consumer<T> {
    void accept(T t);

    Consumer<T> andThen(Consumer<? super T> consumer);

    /* renamed from: j$.util.function.Consumer$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Consumer $default$andThen(Consumer _this, Consumer consumer) {
            consumer.getClass();
            return new Consumer$$ExternalSyntheticLambda0(_this, consumer);
        }

        public static /* synthetic */ void lambda$andThen$0(Consumer _this, Consumer after, Object t) {
            _this.accept(t);
            after.accept(t);
        }
    }
}
