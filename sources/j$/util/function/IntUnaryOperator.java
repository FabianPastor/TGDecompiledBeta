package j$.util.function;

public interface IntUnaryOperator {
    IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator);

    int applyAsInt(int i);

    IntUnaryOperator compose(IntUnaryOperator intUnaryOperator);

    /* renamed from: j$.util.function.IntUnaryOperator$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static IntUnaryOperator $default$compose(IntUnaryOperator _this, IntUnaryOperator before) {
            before.getClass();
            return new IntUnaryOperator$$ExternalSyntheticLambda1(_this, before);
        }

        public static IntUnaryOperator $default$andThen(IntUnaryOperator _this, IntUnaryOperator after) {
            after.getClass();
            return new IntUnaryOperator$$ExternalSyntheticLambda0(_this, after);
        }

        public static IntUnaryOperator identity() {
            return IntUnaryOperator$$ExternalSyntheticLambda2.INSTANCE;
        }

        public static /* synthetic */ int lambda$identity$2(int t) {
            return t;
        }
    }
}
