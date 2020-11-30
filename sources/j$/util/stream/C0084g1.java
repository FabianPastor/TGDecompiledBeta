package j$.util.stream;

import j$.util.Optional;
import j$.util.function.Predicate;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.m;

/* renamed from: j$.util.stream.g1  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg1 implements Predicate {
    public static final /* synthetic */ CLASSNAMEg1 a = new CLASSNAMEg1();

    private /* synthetic */ CLASSNAMEg1() {
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
        return ((Optional) obj).isPresent();
    }
}
