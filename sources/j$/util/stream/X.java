package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.Predicate;

public final /* synthetic */ class X implements Predicate {
    public static final /* synthetic */ X a = new X();

    private /* synthetic */ X() {
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
        return ((CLASSNAMEk) obj).c();
    }
}
