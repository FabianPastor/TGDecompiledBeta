package j$.util.stream;

import j$.util.B;
import j$.util.function.Predicate;
import j$.util.function.U;

/* renamed from: j$.util.stream.e1  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe1 implements Predicate {
    public static final /* synthetic */ CLASSNAMEe1 a = new CLASSNAMEe1();

    private /* synthetic */ CLASSNAMEe1() {
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
        return ((B) obj).c();
    }
}
