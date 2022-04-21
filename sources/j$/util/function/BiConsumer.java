package j$.util.function;

public interface BiConsumer<T, U> {
    void accept(T t, U u);

    BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> biConsumer);

    /* renamed from: j$.util.function.BiConsumer$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static BiConsumer $default$andThen(BiConsumer _this, BiConsumer biConsumer) {
            biConsumer.getClass();
            return new BiConsumer$$ExternalSyntheticLambda0(_this, biConsumer);
        }

        public static /* synthetic */ void lambda$andThen$0(BiConsumer _this, BiConsumer after, Object l, Object r) {
            _this.accept(l, r);
            after.accept(l, r);
        }
    }
}
