package j$.util.stream;

import j$.util.function.CLASSNAMEb;
import j$.util.function.q;
import j$.util.y;
import java.util.concurrent.CountedCompleter;

class O1 extends CLASSNAMEf {
    protected final CLASSNAMEz2 h;
    protected final q i;
    protected final CLASSNAMEb j;

    O1(O1 o1, y yVar) {
        super((CLASSNAMEf) o1, yVar);
        this.h = o1.h;
        this.i = o1.i;
        this.j = o1.j;
    }

    O1(CLASSNAMEz2 z2Var, y yVar, q qVar, CLASSNAMEb bVar) {
        super(z2Var, yVar);
        this.h = z2Var;
        this.i = qVar;
        this.j = bVar;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEt1 t1Var = (CLASSNAMEt1) this.i.apply(this.h.q0(this.b));
        this.h.u0(t1Var, this.b);
        return t1Var.a();
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf f(y yVar) {
        return new O1(this, yVar);
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            g((B1) this.j.apply((B1) ((O1) this.d).b(), (B1) ((O1) this.e).b()));
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
