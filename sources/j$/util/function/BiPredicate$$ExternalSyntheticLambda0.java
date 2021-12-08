package j$.util.function;

import j$.util.function.BiPredicate;

public final /* synthetic */ class BiPredicate$$ExternalSyntheticLambda0 implements BiPredicate {
    public final /* synthetic */ BiPredicate f$0;

    public /* synthetic */ BiPredicate$$ExternalSyntheticLambda0(BiPredicate biPredicate) {
        this.f$0 = biPredicate;
    }

    public /* synthetic */ BiPredicate and(BiPredicate biPredicate) {
        return BiPredicate.CC.$default$and(this, biPredicate);
    }

    public /* synthetic */ BiPredicate negate() {
        return BiPredicate.CC.$default$negate(this);
    }

    public /* synthetic */ BiPredicate or(BiPredicate biPredicate) {
        return BiPredicate.CC.$default$or(this, biPredicate);
    }

    public final boolean test(Object obj, Object obj2) {
        return BiPredicate.CC.lambda$negate$1(this.f$0, obj, obj2);
    }
}
