package j$.util.stream;

import j$.I;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.CLASSNAMEv;

/* renamed from: j$.util.stream.a3  reason: case insensitive filesystem */
class CLASSNAMEa3 extends CLASSNAMEb3 implements B5 {
    final /* synthetic */ CLASSNAMEc3 c;
    final /* synthetic */ CLASSNAMEv d;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        v((Double) obj);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    public /* synthetic */ void v(Double d2) {
        A5.a(this, d2);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEa3(CLASSNAMEc3 c3Var, CLASSNAMEv vVar) {
        super(c3Var);
        this.c = c3Var;
        this.d = vVar;
    }

    public void accept(double t) {
        if (!this.a && ((I) this.d).e(t) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }
}
