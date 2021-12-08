package j$.util.function;

import j$.util.function.IntUnaryOperator;

public final /* synthetic */ class IntUnaryOperator$$ExternalSyntheticLambda0 implements IntUnaryOperator {
    public final /* synthetic */ IntUnaryOperator f$0;
    public final /* synthetic */ IntUnaryOperator f$1;

    public /* synthetic */ IntUnaryOperator$$ExternalSyntheticLambda0(IntUnaryOperator intUnaryOperator, IntUnaryOperator intUnaryOperator2) {
        this.f$0 = intUnaryOperator;
        this.f$1 = intUnaryOperator2;
    }

    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return IntUnaryOperator.CC.$default$andThen(this, intUnaryOperator);
    }

    public final int applyAsInt(int i) {
        return this.f$1.applyAsInt(this.f$0.applyAsInt(i));
    }

    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return IntUnaryOperator.CC.$default$compose(this, intUnaryOperator);
    }
}
