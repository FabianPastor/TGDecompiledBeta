package j$.util.function;

import j$.util.function.IntPredicate;

public final /* synthetic */ class IntPredicate$$ExternalSyntheticLambda0 implements IntPredicate {
    public final /* synthetic */ IntPredicate f$0;

    public /* synthetic */ IntPredicate$$ExternalSyntheticLambda0(IntPredicate intPredicate) {
        this.f$0 = intPredicate;
    }

    public /* synthetic */ IntPredicate and(IntPredicate intPredicate) {
        return IntPredicate.CC.$default$and(this, intPredicate);
    }

    public /* synthetic */ IntPredicate negate() {
        return IntPredicate.CC.$default$negate(this);
    }

    public /* synthetic */ IntPredicate or(IntPredicate intPredicate) {
        return IntPredicate.CC.$default$or(this, intPredicate);
    }

    public final boolean test(int i) {
        return IntPredicate.CC.lambda$negate$1(this.f$0, i);
    }
}
