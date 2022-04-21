package j$.util.function;

import j$.util.Objects;
import j$.util.function.Predicate;

public final /* synthetic */ class Predicate$$ExternalSyntheticLambda4 implements Predicate {
    public static final /* synthetic */ Predicate$$ExternalSyntheticLambda4 INSTANCE = new Predicate$$ExternalSyntheticLambda4();

    private /* synthetic */ Predicate$$ExternalSyntheticLambda4() {
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
        return Objects.isNull(obj);
    }
}
