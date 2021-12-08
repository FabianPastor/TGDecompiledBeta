package j$.util.function;

public interface LongConsumer {
    void accept(long j);

    LongConsumer andThen(LongConsumer longConsumer);

    /* renamed from: j$.util.function.LongConsumer$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static LongConsumer $default$andThen(LongConsumer _this, LongConsumer after) {
            after.getClass();
            return new LongConsumer$$ExternalSyntheticLambda0(_this, after);
        }

        public static /* synthetic */ void lambda$andThen$0(LongConsumer _this, LongConsumer after, long t) {
            _this.accept(t);
            after.accept(t);
        }
    }
}
