package j$.util.stream;

import j$.util.B;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;

final class O1 extends S1 implements B5 {
    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    O1() {
    }

    public /* bridge */ /* synthetic */ void v(Double d) {
        super.accept((Object) d);
    }

    public void accept(double value) {
        accept((Object) Double.valueOf(value));
    }

    /* renamed from: a */
    public B get() {
        if (this.a) {
            return B.d(((Double) this.b).doubleValue());
        }
        return null;
    }
}
