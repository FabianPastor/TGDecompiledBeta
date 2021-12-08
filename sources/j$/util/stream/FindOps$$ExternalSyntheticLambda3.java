package j$.util.stream;

import j$.util.OptionalLong;
import j$.util.function.Predicate;

public final /* synthetic */ class FindOps$$ExternalSyntheticLambda3 implements Predicate {
    public static final /* synthetic */ FindOps$$ExternalSyntheticLambda3 INSTANCE = new FindOps$$ExternalSyntheticLambda3();

    private /* synthetic */ FindOps$$ExternalSyntheticLambda3() {
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
        return ((OptionalLong) obj).isPresent();
    }
}
