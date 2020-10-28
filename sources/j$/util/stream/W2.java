package j$.util.stream;

import j$.CLASSNAMEm0;
import j$.util.function.h;
import j$.util.function.y;

class W2 extends Y2 implements CLASSNAMEs5 {
    final /* synthetic */ Z2 c;
    final /* synthetic */ CLASSNAMEm0 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    W2(Z2 z2, CLASSNAMEm0 m0Var) {
        super(z2);
        this.c = z2;
        this.d = m0Var;
    }

    public void accept(long j) {
        if (!this.a && this.d.b(j) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEc3.c(this, l);
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }
}
