package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.j;
import j$.util.function.k;

/* renamed from: j$.util.stream.f0  reason: case insensitive filesystem */
final class CLASSNAMEf0 extends CLASSNAMEi0 implements CLASSNAMEl3 {
    CLASSNAMEf0() {
    }

    public void accept(int i) {
        accept((Object) Integer.valueOf(i));
    }

    public Object get() {
        if (this.a) {
            return CLASSNAMEk.d(((Integer) this.b).intValue());
        }
        return null;
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }
}
