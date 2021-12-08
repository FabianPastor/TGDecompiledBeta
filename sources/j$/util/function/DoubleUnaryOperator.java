package j$.util.function;

public interface DoubleUnaryOperator {
    DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator);

    double applyAsDouble(double d);

    DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator);

    /* renamed from: j$.util.function.DoubleUnaryOperator$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static DoubleUnaryOperator $default$compose(DoubleUnaryOperator _this, DoubleUnaryOperator before) {
            before.getClass();
            return new DoubleUnaryOperator$$ExternalSyntheticLambda1(_this, before);
        }

        public static DoubleUnaryOperator $default$andThen(DoubleUnaryOperator _this, DoubleUnaryOperator after) {
            after.getClass();
            return new DoubleUnaryOperator$$ExternalSyntheticLambda0(_this, after);
        }

        public static DoubleUnaryOperator identity() {
            return DoubleUnaryOperator$$ExternalSyntheticLambda2.INSTANCE;
        }

        public static /* synthetic */ double lambda$identity$2(double t) {
            return t;
        }
    }
}
