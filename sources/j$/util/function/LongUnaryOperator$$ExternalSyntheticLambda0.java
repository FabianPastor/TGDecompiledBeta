package j$.util.function;

import j$.util.function.LongUnaryOperator;

public final /* synthetic */ class LongUnaryOperator$$ExternalSyntheticLambda0 implements LongUnaryOperator {
    public final /* synthetic */ LongUnaryOperator f$0;
    public final /* synthetic */ LongUnaryOperator f$1;

    public /* synthetic */ LongUnaryOperator$$ExternalSyntheticLambda0(LongUnaryOperator longUnaryOperator, LongUnaryOperator longUnaryOperator2) {
        this.f$0 = longUnaryOperator;
        this.f$1 = longUnaryOperator2;
    }

    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return LongUnaryOperator.CC.$default$andThen(this, longUnaryOperator);
    }

    public final long applyAsLong(long j) {
        return this.f$1.applyAsLong(this.f$0.applyAsLong(j));
    }

    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return LongUnaryOperator.CC.$default$compose(this, longUnaryOperator);
    }
}
