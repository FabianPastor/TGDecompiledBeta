package j$.util.stream;

import j$.util.CLASSNAMEu;
import j$.util.function.Predicate;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.m;

public final /* synthetic */ class R0 implements Predicate {
    public static final /* synthetic */ R0 a = new R0();

    private /* synthetic */ R0() {
    }

    public Predicate a(Predicate predicate) {
        predicate.getClass();
        return new k(this, predicate);
    }

    public Predicate b(Predicate predicate) {
        predicate.getClass();
        return new m(this, predicate);
    }

    public Predicate negate() {
        return new l(this);
    }

    public final boolean test(Object obj) {
        return ((CLASSNAMEu) obj).c();
    }
}
