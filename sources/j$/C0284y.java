package j$;

import j$.util.function.CLASSNAMEv;
import java.util.function.DoublePredicate;

/* renamed from: j$.y  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy implements DoublePredicate {
    final /* synthetic */ CLASSNAMEv a;

    private /* synthetic */ CLASSNAMEy(CLASSNAMEv vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ DoublePredicate a(CLASSNAMEv vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof CLASSNAMEx ? ((CLASSNAMEx) vVar).a : new CLASSNAMEy(vVar);
    }

    public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
        return a(((CLASSNAMEx) this.a).a(CLASSNAMEx.b(doublePredicate)));
    }

    public /* synthetic */ DoublePredicate negate() {
        return a(((CLASSNAMEx) this.a).c());
    }

    public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
        return a(((CLASSNAMEx) this.a).d(CLASSNAMEx.b(doublePredicate)));
    }

    public /* synthetic */ boolean test(double d) {
        return ((CLASSNAMEx) this.a).e(d);
    }
}
