package j$.util.stream;

import j$.util.function.b;
import j$.util.function.r;
import j$.util.u;
import java.util.concurrent.CountedCompleter;

class N1 extends CLASSNAMEf {
    protected final CLASSNAMEy2 h;
    protected final r i;
    protected final b j;

    N1(N1 n1, u uVar) {
        super((CLASSNAMEf) n1, uVar);
        this.h = n1.h;
        this.i = n1.i;
        this.j = n1.j;
    }

    N1(CLASSNAMEy2 y2Var, u uVar, r rVar, b bVar) {
        super(y2Var, uVar);
        this.h = y2Var;
        this.i = rVar;
        this.j = bVar;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEs1 s1Var = (CLASSNAMEs1) this.i.apply(this.h.q0(this.b));
        this.h.u0(s1Var, this.b);
        return s1Var.a();
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf f(u uVar) {
        return new N1(this, uVar);
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            g((A1) this.j.apply((A1) ((N1) this.d).b(), (A1) ((N1) this.e).b()));
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
