package j$.util.function;

public interface IntConsumer {
    void accept(int i);

    IntConsumer andThen(IntConsumer intConsumer);

    /* renamed from: j$.util.function.IntConsumer$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static IntConsumer $default$andThen(IntConsumer _this, IntConsumer after) {
            after.getClass();
            return new IntConsumer$$ExternalSyntheticLambda0(_this, after);
        }

        public static /* synthetic */ void lambda$andThen$0(IntConsumer _this, IntConsumer after, int t) {
            _this.accept(t);
            after.accept(t);
        }
    }
}
