package j$.util.stream;

import j$.util.CLASSNAMEu;
import j$.util.function.g;
import j$.util.function.u;

final class O1 extends R1 implements CLASSNAMEr5 {
    O1() {
    }

    public void accept(int i) {
        accept((Object) Integer.valueOf(i));
    }

    public Object get() {
        if (this.a) {
            return CLASSNAMEu.d(((Integer) this.b).intValue());
        }
        return null;
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }
}
