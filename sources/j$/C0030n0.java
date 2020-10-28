package j$;

import java.util.function.LongPredicate;

/* renamed from: j$.n0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn0 implements LongPredicate {
    final /* synthetic */ CLASSNAMEm0 a;

    public static /* synthetic */ LongPredicate a(CLASSNAMEm0 m0Var) {
        if (m0Var == null) {
            return null;
        }
        return m0Var.a;
    }

    public LongPredicate and(LongPredicate longPredicate) {
        return a(CLASSNAMEm0.a(this.a.a.and(a(CLASSNAMEm0.a(longPredicate)))));
    }

    public LongPredicate negate() {
        return a(CLASSNAMEm0.a(this.a.a.negate()));
    }

    public LongPredicate or(LongPredicate longPredicate) {
        return a(CLASSNAMEm0.a(this.a.a.or(a(CLASSNAMEm0.a(longPredicate)))));
    }

    public boolean test(long j) {
        return this.a.a.test(j);
    }
}
