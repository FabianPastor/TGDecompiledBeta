package j$.util.stream;

import j$.util.function.j;
import j$.util.function.k;

/* renamed from: j$.util.stream.l0  reason: case insensitive filesystem */
final class CLASSNAMEl0 extends CLASSNAMEo0 implements CLASSNAMEl3 {
    final k b;

    CLASSNAMEl0(k kVar, boolean z) {
        super(z);
        this.b = kVar;
    }

    public void accept(int i) {
        this.b.accept(i);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEp1.b(this, num);
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }
}
