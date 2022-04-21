package j$.util.function;

import j$.util.function.LongUnaryOperator;

public final /* synthetic */ class LongUnaryOperator$$ExternalSyntheticLambda2 implements LongUnaryOperator {
    public static final /* synthetic */ LongUnaryOperator$$ExternalSyntheticLambda2 INSTANCE = new LongUnaryOperator$$ExternalSyntheticLambda2();

    private /* synthetic */ LongUnaryOperator$$ExternalSyntheticLambda2() {
    }

    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return LongUnaryOperator.CC.$default$andThen(this, longUnaryOperator);
    }

    public final long applyAsLong(long j) {
        return LongUnaryOperator.CC.lambda$identity$2(j);
    }

    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return LongUnaryOperator.CC.$default$compose(this, longUnaryOperator);
    }
}
