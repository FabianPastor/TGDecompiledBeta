package j$.util.stream;

import j$.V;
import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.D;

class Y2 extends CLASSNAMEb3 implements D5 {
    final /* synthetic */ CLASSNAMEc3 c;
    final /* synthetic */ D d;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        t((Integer) obj);
    }

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    public /* synthetic */ void t(Integer num) {
        C5.a(this, num);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Y2(CLASSNAMEc3 c3Var, D d2) {
        super(c3Var);
        this.c = c3Var;
        this.d = d2;
    }

    public void accept(int t) {
        if (!this.a && ((V) this.d).e(t) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }
}
