package j$.util.function;

import j$.util.function.LongPredicate;

public final /* synthetic */ class LongPredicate$$ExternalSyntheticLambda0 implements LongPredicate {
    public final /* synthetic */ LongPredicate f$0;

    public /* synthetic */ LongPredicate$$ExternalSyntheticLambda0(LongPredicate longPredicate) {
        this.f$0 = longPredicate;
    }

    public /* synthetic */ LongPredicate and(LongPredicate longPredicate) {
        return LongPredicate.CC.$default$and(this, longPredicate);
    }

    public /* synthetic */ LongPredicate negate() {
        return LongPredicate.CC.$default$negate(this);
    }

    public /* synthetic */ LongPredicate or(LongPredicate longPredicate) {
        return LongPredicate.CC.$default$or(this, longPredicate);
    }

    public final boolean test(long j) {
        return LongPredicate.CC.lambda$negate$1(this.f$0, j);
    }
}
