package a;

import j$.util.function.E;
import java.util.function.LongPredicate;

/* renamed from: a.n0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn0 implements LongPredicate {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ E var_a;

    private /* synthetic */ CLASSNAMEn0(E e) {
        this.var_a = e;
    }

    public static /* synthetic */ LongPredicate a(E e) {
        if (e == null) {
            return null;
        }
        return e instanceof CLASSNAMEm0 ? ((CLASSNAMEm0) e).var_a : new CLASSNAMEn0(e);
    }

    public /* synthetic */ LongPredicate and(LongPredicate longPredicate) {
        return a(CLASSNAMEm0.a(((CLASSNAMEm0) this.var_a).var_a.and(a(CLASSNAMEm0.a(longPredicate)))));
    }

    public /* synthetic */ LongPredicate negate() {
        return a(CLASSNAMEm0.a(((CLASSNAMEm0) this.var_a).var_a.negate()));
    }

    public /* synthetic */ LongPredicate or(LongPredicate longPredicate) {
        return a(CLASSNAMEm0.a(((CLASSNAMEm0) this.var_a).var_a.or(a(CLASSNAMEm0.a(longPredicate)))));
    }

    public /* synthetic */ boolean test(long j) {
        return ((CLASSNAMEm0) this.var_a).var_a.test(j);
    }
}
