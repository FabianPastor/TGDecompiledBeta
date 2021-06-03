package j$;

import j$.util.function.E;
import java.util.function.LongPredicate;

/* renamed from: j$.k0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk0 implements LongPredicate {
    final /* synthetic */ E a;

    private /* synthetic */ CLASSNAMEk0(E e) {
        this.a = e;
    }

    public static /* synthetic */ LongPredicate a(E e) {
        if (e == null) {
            return null;
        }
        return e instanceof CLASSNAMEj0 ? ((CLASSNAMEj0) e).a : new CLASSNAMEk0(e);
    }

    public /* synthetic */ LongPredicate and(LongPredicate longPredicate) {
        return a(CLASSNAMEj0.a(((CLASSNAMEj0) this.a).a.and(a(CLASSNAMEj0.a(longPredicate)))));
    }

    public /* synthetic */ LongPredicate negate() {
        return a(CLASSNAMEj0.a(((CLASSNAMEj0) this.a).a.negate()));
    }

    public /* synthetic */ LongPredicate or(LongPredicate longPredicate) {
        return a(CLASSNAMEj0.a(((CLASSNAMEj0) this.a).a.or(a(CLASSNAMEj0.a(longPredicate)))));
    }

    public /* synthetic */ boolean test(long j) {
        return ((CLASSNAMEj0) this.a).a.test(j);
    }
}
