package j$.util.stream;

import j$.util.CLASSNAMEj;
import j$.util.function.e;
import j$.util.function.f;

/* renamed from: j$.util.stream.e0  reason: case insensitive filesystem */
final class CLASSNAMEe0 extends CLASSNAMEi0 implements CLASSNAMEj3 {
    CLASSNAMEe0() {
    }

    public void accept(double d) {
        accept((Object) Double.valueOf(d));
    }

    public Object get() {
        if (this.a) {
            return CLASSNAMEj.d(((Double) this.b).doubleValue());
        }
        return null;
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
