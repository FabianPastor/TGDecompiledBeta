package j$.util.function;

import j$.util.function.LongPredicate;

public final /* synthetic */ class LongPredicate$$ExternalSyntheticLambda1 implements LongPredicate {
    public final /* synthetic */ LongPredicate f$0;
    public final /* synthetic */ LongPredicate f$1;

    public /* synthetic */ LongPredicate$$ExternalSyntheticLambda1(LongPredicate longPredicate, LongPredicate longPredicate2) {
        this.f$0 = longPredicate;
        this.f$1 = longPredicate2;
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
        return LongPredicate.CC.lambda$and$0(this.f$0, this.f$1, j);
    }
}
