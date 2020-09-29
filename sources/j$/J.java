package j$;

import j$.util.function.CLASSNAMEv;
import java.util.function.DoublePredicate;

public final /* synthetic */ class J implements DoublePredicate {
    final /* synthetic */ CLASSNAMEv a;

    private /* synthetic */ J(CLASSNAMEv vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ DoublePredicate a(CLASSNAMEv vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof I ? ((I) vVar).a : new J(vVar);
    }

    public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
        return a(((I) this.a).a(I.b(doublePredicate)));
    }

    public /* synthetic */ DoublePredicate negate() {
        return a(((I) this.a).c());
    }

    public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
        return a(((I) this.a).d(I.b(doublePredicate)));
    }

    public /* synthetic */ boolean test(double d) {
        return ((I) this.a).e(d);
    }
}
