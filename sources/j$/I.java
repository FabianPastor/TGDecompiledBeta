package j$;

import j$.util.function.CLASSNAMEv;
import java.util.function.DoublePredicate;

public final /* synthetic */ class I implements CLASSNAMEv {
    final /* synthetic */ DoublePredicate a;

    private /* synthetic */ I(DoublePredicate doublePredicate) {
        this.a = doublePredicate;
    }

    public static /* synthetic */ CLASSNAMEv b(DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof J ? ((J) doublePredicate).a : new I(doublePredicate);
    }

    public /* synthetic */ CLASSNAMEv a(CLASSNAMEv vVar) {
        return b(this.a.and(J.a(vVar)));
    }

    public /* synthetic */ CLASSNAMEv c() {
        return b(this.a.negate());
    }

    public /* synthetic */ CLASSNAMEv d(CLASSNAMEv vVar) {
        return b(this.a.or(J.a(vVar)));
    }

    public /* synthetic */ boolean e(double d) {
        return this.a.test(d);
    }
}
