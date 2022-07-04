package j$.util.function;

import j$.util.function.DoublePredicate;

public final /* synthetic */ class DoublePredicate$$ExternalSyntheticLambda0 implements DoublePredicate {
    public final /* synthetic */ DoublePredicate f$0;

    public /* synthetic */ DoublePredicate$$ExternalSyntheticLambda0(DoublePredicate doublePredicate) {
        this.f$0 = doublePredicate;
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
        return DoublePredicate.CC.lambda$negate$1(this.f$0, d);
    }
}
