package j$.util.stream;

import j$.Y;
import j$.util.function.g;
import j$.util.function.u;

class V2 extends Y2 implements CLASSNAMEr5 {
    final /* synthetic */ Z2 c;
    final /* synthetic */ Y d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    V2(Z2 z2, Y y) {
        super(z2);
        this.c = z2;
        this.d = y;
    }

    public void accept(int i) {
        if (!this.a && this.d.b(i) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEc3.b(this, num);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }
}
