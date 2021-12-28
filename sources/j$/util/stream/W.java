package j$.util.stream;

import j$.util.CLASSNAMEj;
import j$.util.function.Predicate;

public final /* synthetic */ class W implements Predicate {
    public static final /* synthetic */ W a = new W();

    private /* synthetic */ W() {
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
        return ((CLASSNAMEj) obj).c();
    }
}
