package j$.util.stream;

import j$.util.function.f;
import j$.util.function.q;

final class T1 extends X1 implements CLASSNAMEq5 {
    final q b;

    T1(q qVar, boolean z) {
        super(z);
        this.b = qVar;
    }

    public void accept(double d) {
        this.b.accept(d);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEc3.a(this, d);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }
}
