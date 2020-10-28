package j$.util.stream;

import j$.util.CLASSNAMEv;
import j$.util.function.Predicate;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.m;

public final /* synthetic */ class Z0 implements Predicate {
    public static final /* synthetic */ Z0 a = new Z0();

    private /* synthetic */ Z0() {
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
        return ((CLASSNAMEv) obj).c();
    }
}
