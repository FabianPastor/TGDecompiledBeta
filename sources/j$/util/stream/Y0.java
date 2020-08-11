package j$.util.stream;

import j$.util.C;
import j$.util.function.Predicate;
import j$.util.function.U;

public final /* synthetic */ class Y0 implements Predicate {
    public static final /* synthetic */ Y0 a = new Y0();

    private /* synthetic */ Y0() {
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
        return ((C) obj).c();
    }
}
