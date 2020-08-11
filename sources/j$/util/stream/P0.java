package j$.util.stream;

import j$.util.CLASSNAMEz;
import j$.util.function.Predicate;
import j$.util.function.U;

public final /* synthetic */ class P0 implements Predicate {
    public static final /* synthetic */ P0 a = new P0();

    private /* synthetic */ P0() {
    }

    public /* synthetic */ Predicate a(Predicate predicate) {
        return U.c(this, predicate);
    }

    public /* synthetic */ Predicate b(Predicate predicate) {
        return U.a(this, predicate);
    }

    public /* synthetic */ Predicate negate() {
        return U.b(this);
    }

    public final boolean test(Object obj) {
        return ((CLASSNAMEz) obj).c();
    }
}
