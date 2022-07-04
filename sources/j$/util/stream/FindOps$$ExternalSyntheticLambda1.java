package j$.util.stream;

import j$.util.OptionalDouble;
import j$.util.function.Predicate;

public final /* synthetic */ class FindOps$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ FindOps$$ExternalSyntheticLambda1 INSTANCE = new FindOps$$ExternalSyntheticLambda1();

    private /* synthetic */ FindOps$$ExternalSyntheticLambda1() {
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
        return ((OptionalDouble) obj).isPresent();
    }
}
