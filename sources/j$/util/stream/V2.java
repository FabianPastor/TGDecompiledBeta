package j$.util.stream;

import j$.util.u;
import java.util.concurrent.CountedCompleter;

final class V2 extends CLASSNAMEf {
    private final U2 h;

    V2(U2 u2, CLASSNAMEy2 y2Var, u uVar) {
        super(y2Var, uVar);
        this.h = u2;
    }

    V2(V2 v2, u uVar) {
        super((CLASSNAMEf) v2, uVar);
        this.h = v2.h;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEy2 y2Var = this.a;
        S2 a = this.h.a();
        y2Var.u0(a, this.b);
        return a;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf f(u uVar) {
        return new V2(this, uVar);
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            S2 s2 = (S2) ((V2) this.d).b();
            s2.h((S2) ((V2) this.e).b());
            g(s2);
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
