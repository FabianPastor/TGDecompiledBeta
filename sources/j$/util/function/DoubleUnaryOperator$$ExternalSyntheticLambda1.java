package j$.util.function;

import j$.util.function.DoubleUnaryOperator;

public final /* synthetic */ class DoubleUnaryOperator$$ExternalSyntheticLambda1 implements DoubleUnaryOperator {
    public final /* synthetic */ DoubleUnaryOperator f$0;
    public final /* synthetic */ DoubleUnaryOperator f$1;

    public /* synthetic */ DoubleUnaryOperator$$ExternalSyntheticLambda1(DoubleUnaryOperator doubleUnaryOperator, DoubleUnaryOperator doubleUnaryOperator2) {
        this.f$0 = doubleUnaryOperator;
        this.f$1 = doubleUnaryOperator2;
    }

    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return DoubleUnaryOperator.CC.$default$andThen(this, doubleUnaryOperator);
    }

    public final double applyAsDouble(double d) {
        return this.f$0.applyAsDouble(this.f$1.applyAsDouble(d));
    }

    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return DoubleUnaryOperator.CC.$default$compose(this, doubleUnaryOperator);
    }
}
