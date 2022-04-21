package j$.util.function;

import j$.util.function.DoubleUnaryOperator;

public final /* synthetic */ class DoubleUnaryOperator$$ExternalSyntheticLambda2 implements DoubleUnaryOperator {
    public static final /* synthetic */ DoubleUnaryOperator$$ExternalSyntheticLambda2 INSTANCE = new DoubleUnaryOperator$$ExternalSyntheticLambda2();

    private /* synthetic */ DoubleUnaryOperator$$ExternalSyntheticLambda2() {
    }

    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return DoubleUnaryOperator.CC.$default$andThen(this, doubleUnaryOperator);
    }

    public final double applyAsDouble(double d) {
        return DoubleUnaryOperator.CC.lambda$identity$2(d);
    }

    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return DoubleUnaryOperator.CC.$default$compose(this, doubleUnaryOperator);
    }
}
