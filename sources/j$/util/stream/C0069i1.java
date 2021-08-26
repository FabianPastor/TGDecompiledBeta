package j$.util.stream;

import j$.util.function.o;
import j$.util.function.p;
import j$.wrappers.CLASSNAMEj0;

/* renamed from: j$.util.stream.i1  reason: case insensitive filesystem */
class CLASSNAMEi1 extends CLASSNAMEk1 implements CLASSNAMEm3 {
    final /* synthetic */ CLASSNAMEl1 c;
    final /* synthetic */ CLASSNAMEj0 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEi1(CLASSNAMEl1 l1Var, CLASSNAMEj0 j0Var) {
        super(l1Var);
        this.c = l1Var;
        this.d = j0Var;
    }

    public void accept(long j) {
        if (!this.a && this.d.b(j) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEp1.c(this, l);
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }
}
