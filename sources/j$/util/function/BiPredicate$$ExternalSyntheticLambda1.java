package j$.util.function;

import j$.util.function.BiPredicate;

public final /* synthetic */ class BiPredicate$$ExternalSyntheticLambda1 implements BiPredicate {
    public final /* synthetic */ BiPredicate f$0;
    public final /* synthetic */ BiPredicate f$1;

    public /* synthetic */ BiPredicate$$ExternalSyntheticLambda1(BiPredicate biPredicate, BiPredicate biPredicate2) {
        this.f$0 = biPredicate;
        this.f$1 = biPredicate2;
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
        return BiPredicate.CC.lambda$and$0(this.f$0, this.f$1, obj, obj2);
    }
}
