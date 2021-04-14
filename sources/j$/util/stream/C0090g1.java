package j$.util.stream;

import j$.util.Optional;
import j$.util.function.Predicate;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.m;

/* renamed from: j$.util.stream.g1  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg1 implements Predicate {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ CLASSNAMEg1 var_a = new CLASSNAMEg1();

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
