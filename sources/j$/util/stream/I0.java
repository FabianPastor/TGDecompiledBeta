package j$.util.stream;

import j$.util.D;
import j$.util.function.Predicate;
import j$.util.function.U;

public final /* synthetic */ class I0 implements Predicate {
    public static final /* synthetic */ I0 a = new I0();

    private /* synthetic */ I0() {
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
        return ((D) obj).c();
    }
}
