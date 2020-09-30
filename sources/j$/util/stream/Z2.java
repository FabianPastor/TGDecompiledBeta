package j$.util.stream;

import j$.U;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.function.L;

class Z2 extends CLASSNAMEb3 implements F5 {
    final /* synthetic */ CLASSNAMEc3 c;
    final /* synthetic */ L d;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        n((Long) obj);
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* synthetic */ void n(Long l) {
        E5.a(this, l);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Z2(CLASSNAMEc3 c3Var, L l) {
        super(c3Var);
        this.c = c3Var;
        this.d = l;
    }

    public void accept(long t) {
        if (!this.a && ((U) this.d).e(t) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }
}
