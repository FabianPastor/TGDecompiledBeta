package j$.util.stream;

import j$.H;
import j$.util.function.f;
import j$.util.function.q;

class X2 extends Y2 implements CLASSNAMEq5 {
    final /* synthetic */ Z2 c;
    final /* synthetic */ H d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    X2(Z2 z2, H h) {
        super(z2);
        this.c = z2;
        this.d = h;
    }

    public void accept(double d2) {
        if (!this.a && this.d.b(d2) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d2) {
        CLASSNAMEc3.a(this, d2);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }
}
