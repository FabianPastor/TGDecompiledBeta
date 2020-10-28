package j$.util.stream;

import j$.util.CLASSNAMEt;
import j$.util.function.f;
import j$.util.function.q;

final class N1 extends R1 implements CLASSNAMEq5 {
    N1() {
    }

    public void accept(double d) {
        accept((Object) Double.valueOf(d));
    }

    public Object get() {
        if (this.a) {
            return CLASSNAMEt.d(((Double) this.b).doubleValue());
        }
        return null;
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }
}
