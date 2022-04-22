package j$.util.stream;

import j$.util.CLASSNAMEl;
import j$.util.function.Predicate;

public final /* synthetic */ class Y implements Predicate {
    public static final /* synthetic */ Y a = new Y();

    private /* synthetic */ Y() {
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
        return ((CLASSNAMEl) obj).c();
    }
}
