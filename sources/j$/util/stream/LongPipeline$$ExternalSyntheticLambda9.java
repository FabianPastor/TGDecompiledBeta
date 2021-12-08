package j$.util.stream;

import j$.util.function.LongUnaryOperator;

public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda9 implements LongUnaryOperator {
    public static final /* synthetic */ LongPipeline$$ExternalSyntheticLambda9 INSTANCE = new LongPipeline$$ExternalSyntheticLambda9();

    private /* synthetic */ LongPipeline$$ExternalSyntheticLambda9() {
    }

    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return LongUnaryOperator.CC.$default$andThen(this, longUnaryOperator);
    }

    public final long applyAsLong(long j) {
        return LongPipeline.lambda$count$4(j);
    }

    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return LongUnaryOperator.CC.$default$compose(this, longUnaryOperator);
    }
}
