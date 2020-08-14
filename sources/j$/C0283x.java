package j$;

import j$.util.function.CLASSNAMEv;
import java.util.function.DoublePredicate;

/* renamed from: j$.x  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEx implements CLASSNAMEv {
    final /* synthetic */ DoublePredicate a;

    private /* synthetic */ CLASSNAMEx(DoublePredicate doublePredicate) {
        this.a = doublePredicate;
    }

    public static /* synthetic */ CLASSNAMEv b(DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof CLASSNAMEy ? ((CLASSNAMEy) doublePredicate).a : new CLASSNAMEx(doublePredicate);
    }

    public /* synthetic */ CLASSNAMEv a(CLASSNAMEv vVar) {
        return b(this.a.and(CLASSNAMEy.a(vVar)));
    }

    public /* synthetic */ CLASSNAMEv c() {
        return b(this.a.negate());
    }

    public /* synthetic */ CLASSNAMEv d(CLASSNAMEv vVar) {
        return b(this.a.or(CLASSNAMEy.a(vVar)));
    }

    public /* synthetic */ boolean e(double d) {
        return this.a.test(d);
    }
}
