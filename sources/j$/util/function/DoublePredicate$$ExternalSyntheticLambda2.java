package j$.util.function;

import j$.util.function.DoublePredicate;

public final /* synthetic */ class DoublePredicate$$ExternalSyntheticLambda2 implements DoublePredicate {
    public final /* synthetic */ DoublePredicate f$0;
    public final /* synthetic */ DoublePredicate f$1;

    public /* synthetic */ DoublePredicate$$ExternalSyntheticLambda2(DoublePredicate doublePredicate, DoublePredicate doublePredicate2) {
        this.f$0 = doublePredicate;
        this.f$1 = doublePredicate2;
    }

    public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
        return DoublePredicate.CC.$default$and(this, doublePredicate);
    }

    public /* synthetic */ DoublePredicate negate() {
        return DoublePredicate.CC.$default$negate(this);
    }

    public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
        return DoublePredicate.CC.$default$or(this, doublePredicate);
    }

    public final boolean test(double d) {
        return DoublePredicate.CC.lambda$or$2(this.f$0, this.f$1, d);
    }
}
