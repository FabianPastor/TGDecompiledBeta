package j$.util.function;

public interface LongUnaryOperator {
    LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator);

    long applyAsLong(long j);

    LongUnaryOperator compose(LongUnaryOperator longUnaryOperator);

    /* renamed from: j$.util.function.LongUnaryOperator$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static LongUnaryOperator $default$compose(LongUnaryOperator _this, LongUnaryOperator before) {
            before.getClass();
            return new LongUnaryOperator$$ExternalSyntheticLambda1(_this, before);
        }

        public static LongUnaryOperator $default$andThen(LongUnaryOperator _this, LongUnaryOperator after) {
            after.getClass();
            return new LongUnaryOperator$$ExternalSyntheticLambda0(_this, after);
        }

        public static LongUnaryOperator identity() {
            return LongUnaryOperator$$ExternalSyntheticLambda2.INSTANCE;
        }

        public static /* synthetic */ long lambda$identity$2(long t) {
            return t;
        }
    }
}
