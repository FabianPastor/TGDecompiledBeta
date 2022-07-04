package j$.util.function;

import j$.util.function.IntPredicate;

public final /* synthetic */ class IntPredicate$$ExternalSyntheticLambda2 implements IntPredicate {
    public final /* synthetic */ IntPredicate f$0;
    public final /* synthetic */ IntPredicate f$1;

    public /* synthetic */ IntPredicate$$ExternalSyntheticLambda2(IntPredicate intPredicate, IntPredicate intPredicate2) {
        this.f$0 = intPredicate;
        this.f$1 = intPredicate2;
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
        return IntPredicate.CC.lambda$or$2(this.f$0, this.f$1, i);
    }
}
