package j$.util.stream;

import j$.util.function.g;
import j$.util.function.u;

final class U1 extends X1 implements CLASSNAMEr5 {
    final u b;

    U1(u uVar, boolean z) {
        super(z);
        this.b = uVar;
    }

    public void accept(int i) {
        this.b.accept(i);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEc3.b(this, num);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }
}
