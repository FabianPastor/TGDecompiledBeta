package j$.util.function;

public interface DoubleConsumer {
    void accept(double d);

    DoubleConsumer andThen(DoubleConsumer doubleConsumer);

    /* renamed from: j$.util.function.DoubleConsumer$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static DoubleConsumer $default$andThen(DoubleConsumer _this, DoubleConsumer after) {
            after.getClass();
            return new DoubleConsumer$$ExternalSyntheticLambda0(_this, after);
        }

        public static /* synthetic */ void lambda$andThen$0(DoubleConsumer _this, DoubleConsumer after, double t) {
            _this.accept(t);
            after.accept(t);
        }
    }
}
