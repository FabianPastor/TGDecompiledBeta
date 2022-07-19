package j$.util.stream;

import j$.util.function.p;
import j$.util.function.q;
import j$.wrappers.CLASSNAMEj0;

/* renamed from: j$.util.stream.h1  reason: case insensitive filesystem */
class CLASSNAMEh1 extends CLASSNAMEj1 implements CLASSNAMEl3 {
    final /* synthetic */ CLASSNAMEk1 c;
    final /* synthetic */ CLASSNAMEj0 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEh1(CLASSNAMEk1 k1Var, CLASSNAMEj0 j0Var) {
        super(k1Var);
        this.c = k1Var;
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
        CLASSNAMEo1.c(this, l);
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }
}
