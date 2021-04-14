package j$.util.stream;

import j$.util.function.Predicate;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.q;

public final /* synthetic */ class R0 implements Predicate {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ R0 var_a = new R0();

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
        return ((q) obj).c();
    }
}
