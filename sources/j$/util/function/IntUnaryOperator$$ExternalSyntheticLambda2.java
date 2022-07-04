package j$.util.function;

import j$.util.function.IntUnaryOperator;

public final /* synthetic */ class IntUnaryOperator$$ExternalSyntheticLambda2 implements IntUnaryOperator {
    public static final /* synthetic */ IntUnaryOperator$$ExternalSyntheticLambda2 INSTANCE = new IntUnaryOperator$$ExternalSyntheticLambda2();

    private /* synthetic */ IntUnaryOperator$$ExternalSyntheticLambda2() {
    }

    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return IntUnaryOperator.CC.$default$andThen(this, intUnaryOperator);
    }

    public final int applyAsInt(int i) {
        return IntUnaryOperator.CC.lambda$identity$2(i);
    }

    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return IntUnaryOperator.CC.$default$compose(this, intUnaryOperator);
    }
}
