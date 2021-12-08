package j$.util.function;

import j$.util.function.Predicate;

public final /* synthetic */ class Predicate$$ExternalSyntheticLambda0 implements Predicate {
    public final /* synthetic */ Object f$0;

    public /* synthetic */ Predicate$$ExternalSyntheticLambda0(Object obj) {
        this.f$0 = obj;
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return Predicate.CC.$default$and(this, predicate);
    }

    public /* synthetic */ Predicate negate() {
        return Predicate.CC.$default$negate(this);
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return Predicate.CC.$default$or(this, predicate);
    }

    public final boolean test(Object obj) {
        return this.f$0.equals(obj);
    }
}
