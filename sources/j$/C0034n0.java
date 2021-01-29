package j$;

import j$.util.function.E;
import java.util.function.LongPredicate;

/* renamed from: j$.n0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn0 implements LongPredicate {
    final /* synthetic */ E a;

    private /* synthetic */ CLASSNAMEn0(E e) {
        this.a = e;
    }

    public static /* synthetic */ LongPredicate a(E e) {
        if (e == null) {
            return null;
        }
        return e instanceof CLASSNAMEm0 ? ((CLASSNAMEm0) e).a : new CLASSNAMEn0(e);
    }

    public /* synthetic */ LongPredicate and(LongPredicate longPredicate) {
        return a(CLASSNAMEm0.a(((CLASSNAMEm0) this.a).a.and(a(CLASSNAMEm0.a(longPredicate)))));
    }

    public /* synthetic */ LongPredicate negate() {
        return a(CLASSNAMEm0.a(((CLASSNAMEm0) this.a).a.negate()));
    }

    public /* synthetic */ LongPredicate or(LongPredicate longPredicate) {
        return a(CLASSNAMEm0.a(((CLASSNAMEm0) this.a).a.or(a(CLASSNAMEm0.a(longPredicate)))));
    }

    public /* synthetic */ boolean test(long j) {
        return ((CLASSNAMEm0) this.a).a.test(j);
    }
}
